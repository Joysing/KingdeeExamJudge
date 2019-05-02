package com.kingdee.exam.judger.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kingdee.exam.judger.model.Checkpoint;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kingdee.exam.judger.application.ApplicationDispatcher;
import com.kingdee.exam.judger.exception.IllgealSubmissionException;
import com.kingdee.exam.judger.mapper.CheckpointMapper;
import com.kingdee.exam.judger.mapper.SubmissionMapper;
import com.kingdee.exam.judger.model.Submission;
import com.kingdee.exam.judger.util.DigestUtils;

/**
 * 评测机调度器.
 * 用于完成评测机的评测流程.
 * 每个阶段结束后推送消息至消息队列; 评测结束后写入数据库.
 */
@Component
public class Dispatcher {
    @Autowired
    public Dispatcher(ApplicationDispatcher applicationDispatcher, Preprocessor preprocessor, Compiler compiler, Runner runner, Comparator comparator, SubmissionMapper submissionMapper, CheckpointMapper checkpointMapper) {
        this.applicationDispatcher = applicationDispatcher;
        this.preprocessor = preprocessor;
        this.compiler = compiler;
        this.runner = runner;
        this.comparator = comparator;
        this.submissionMapper = submissionMapper;
        this.checkpointMapper = checkpointMapper;
    }

    /**
	 * 创建新的评测任务.
	 * 每次只运行一个评测任务.
	 * @param submissionId - 提交记录的唯一标识符
	 * @throws IllgealSubmissionException 无效的提交记录异常
	 * @throws InterruptedException Thread中断异常
	 */
	public void createNewTask(long submissionId) throws IllgealSubmissionException, InterruptedException {
		synchronized(this) {
			String baseDirectory = String.format("%s/voj-%s", workBaseDirectory, submissionId);
			String baseFileName = DigestUtils.getRandomString(12, DigestUtils.Mode.ALPHA);
			
			// 解决由于未知原因无法获取到数据记录的问题
			int tryTimes = 0;
			Submission submission;
			do {
				Thread.sleep(1000);
				submission = submissionMapper.getSubmission(submissionId);
			} while ( submission == null && ++ tryTimes <= 3 );
			
			if ( submission == null ) {
				throw new IllgealSubmissionException(
						String.format("无效的submission #%s",submissionId));
			}
			preprocess(submission, baseDirectory, baseFileName);
			if ( compile(submission, baseDirectory, baseFileName) ) {
				runProgram(submission, baseDirectory, baseFileName);
			}
			cleanUp(baseDirectory);
		}
	}

	/**
	 * 完成评测前的预处理工作.
	 * 说明: 随机文件名用于防止应用程序自身递归调用.
	 *
	 * @param submission - 评测记录对象
	 * @param workDirectory - 用于产生编译输出的目录
	 * @param baseFileName - 随机文件名(不包含后缀)
	 */
	private void preprocess(Submission submission,
							String workDirectory, String baseFileName) {
		try {
			long problemId = submission.getProblem().getProblemId();
			preprocessor.createTestCode(submission, workDirectory, baseFileName);
			preprocessor.fetchTestPoints(problemId);
		} catch (Exception ex) {
			LOGGER.catching(ex);

			long submissionId = submission.getSubmissionId();
			applicationDispatcher.onErrorOccurred(submissionId);
		}
	}

	/**
	 * 创建编译任务.
	 * 说明: 随机文件名用于防止应用程序自身递归调用.
	 *
	 * @param submission - 评测记录对象
	 * @param workDirectory - 用于产生编译输出的目录
	 * @param baseFileName - 随机文件名(不包含后缀)
	 */
	private boolean compile(Submission submission,
							String workDirectory, String baseFileName) {
		long submissionId = submission.getSubmissionId();
		Map<String, Object> result =
				compiler.getCompileResult(submission, workDirectory, baseFileName);

		applicationDispatcher.onCompileFinished(submissionId, result);
		return (Boolean)result.get("isSuccessful");
	}

	/**
	 * 执行程序.
	 * @param submission - 评测记录对象
	 * @param workDirectory - 编译生成结果的目录以及程序输出的目录
	 * @param baseFileName - 待执行的应用程序文件名(不包含文件后缀)
	 */
	private void runProgram(Submission submission,
							String workDirectory, String baseFileName) {
		List<Map<String, Object>> runtimeResults = new ArrayList<>();
		long submissionId = submission.getSubmissionId();
		long problemId = submission.getProblem().getProblemId();

		List<Checkpoint> checkpoints = checkpointMapper.getCheckpointsUsingProblemId(problemId);
		for ( Checkpoint checkpoint : checkpoints ) {
			int checkpointId = checkpoint.getCheckpointId();
			int checkpointScore = checkpoint.getScore();
			String inputFilePath = String.format("%s/%s/input#%s.txt",
                    checkpointDirectory, problemId, checkpointId);
			String stdOutputFilePath = String.format("%s/%s/output#%s.txt",
                    checkpointDirectory, problemId, checkpointId);
			String outputFilePath = getOutputFilePath(workDirectory, checkpointId);

			Map<String, Object> runtimeResult = getRuntimeResult(
					runner.getRuntimeResult(submission, workDirectory, baseFileName, inputFilePath, outputFilePath),
					stdOutputFilePath, outputFilePath);
			runtimeResult.put("score", checkpointScore);
			runtimeResults.add(runtimeResult);
			applicationDispatcher.onOneTestPointFinished(submissionId, checkpointId, runtimeResult);
		}
		applicationDispatcher.onAllTestPointsFinished(submissionId, runtimeResults);
	}

	/**
	 * 获取当前测试点输出路径.
	 * @param workDirectory - 编译生成结果的目录以及程序输出的目录
	 * @param checkpointId - 当前测试点编号
	 * @return 当前测试点输出路径
	 */
	private String getOutputFilePath(String workDirectory, int checkpointId) {
		return String.format("%s/output#%s.txt",
                workDirectory, checkpointId);
	}

	/**
	 * 获取程序运行结果(及答案比对结果).
	 * @param result - 包含程序运行结果的Map对象
	 * @param standardOutputFilePath - 标准输出文件路径
	 * @param outputFilePath - 用户输出文件路径
	 * @return 包含程序运行结果的Map对象
	 */
	private Map<String, Object> getRuntimeResult(Map<String, Object> result,
		String standardOutputFilePath, String outputFilePath) {
		String runtimeResultSlug = (String)result.get("runtimeResult");
		int usedTime = (Integer)result.get("usedTime");
		int usedMemory = (Integer)result.get("usedMemory");

		if ( runtimeResultSlug.equals("AC") && 
				!isOutputTheSame(standardOutputFilePath, outputFilePath) ) {
			runtimeResultSlug = "WA";
			result.put("runtimeResult", runtimeResultSlug);
		}
		LOGGER.info(String.format("运行结果: [%s, Time: %d ms, Memory: %d KB]",
                runtimeResultSlug, usedTime, usedMemory));

		return result;
	}

	/**
	 * 获取用户输出和标准输出的比对结果.
	 * @param standardOutputFilePath - 标准输出文件路径
	 * @param outputFilePath - 用户输出文件路径
	 * @return 用户输出和标准输出是否相同
	 */
	private boolean isOutputTheSame(String standardOutputFilePath, String outputFilePath) {
		try {
			return comparator.isOutputTheSame(standardOutputFilePath, outputFilePath);
		} catch (IOException ex) {
			LOGGER.catching(ex);
		}
		return false;
	}

	/**
	 * 评测完成后, 清理所生成的文件.
	 * @param baseDirectory - 用于产生输出结果目录
	 */
	private void cleanUp(String baseDirectory) {
		File baseDirFile = new File(baseDirectory);
		if ( baseDirFile.exists() ) {
			try {
				FileUtils.deleteDirectory(baseDirFile);
			} catch (IOException ex) {
				LOGGER.catching(ex);
			}
		}
	}

	/**
	 * 自动注入的ApplicationDispatcher对象.
	 * 完成每个阶段的任务后推送消息至消息队列.
	 */
	private final ApplicationDispatcher applicationDispatcher;

	/**
	 * 自动注入的Preprocessor对象.
	 * 完成编译前的准备工作.
	 */
	private final Preprocessor preprocessor;

	/**
	 * 自动注入的Compiler对象.
	 * 完成编译工作.
	 */
	private final Compiler compiler;

	/**
	 * 自动注入的Runner对象.
	 * 完成程序运行工作.
	 */
	private final Runner runner;

	/**
	 * 自动注入的Matcher对象.
	 * 完成输出结果比对工作.
	 */
	private final Comparator comparator;

	/**
	 * 自动注入的SubmissionMapper对象.
	 */
	private final SubmissionMapper submissionMapper;

	/**
	 * 自动注入的CheckpointMapper对象.
	 * 用于获取试题的测试点.
	 */
	private final CheckpointMapper checkpointMapper;

	/**
	 * 评测机的工作目录.
	 * 用于存储编译结果以及程序输出结果.
	 */
	@Value("${judger.workDir}")
	private String workBaseDirectory;

	/**
	 * 测试点的存储目录.
	 * 用于存储测试点的输入输出数据.
	 */
	@Value("${judger.checkpointDir}")
	private String checkpointDirectory;

	/**
	 * 日志记录器.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Dispatcher.class);
}
