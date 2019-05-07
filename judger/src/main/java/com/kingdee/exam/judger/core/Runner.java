package com.kingdee.exam.judger.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kingdee.exam.judger.model.Language;
import com.kingdee.exam.judger.model.Submission;
import com.kingdee.exam.judger.util.NativeLibraryLoader;

/**
 * 本地程序执行器, 用于执行本地应用程序. 包括编译器(gcc)以及用户提交的代码所编译出的程序.
 */
@Component
public class Runner {
	/**
	 * 获取(用户)程序运行结果.
	 * 
	 * @param submission - 评测记录对象
	 * @param workDirectory - 编译生成结果的目录以及程序输出的目录
	 * @param baseFileName - 待执行的应用程序文件名(不包含文件后缀)
	 * @param inputFilePath - 输入文件路径
	 * @param outputFilePath - 输出文件路径
	 * @return 一个包含程序运行结果的Map<String, Object>对象
	 */
    Map<String, Object> getRuntimeResult(Submission submission, String workDirectory,
                                         String baseFileName, String inputFilePath, String outputFilePath) {
		String commandLine = getCommandLine(submission, workDirectory, baseFileName);
		int timeLimit = getTimeLimit(submission);
		int memoryLimit = getMemoryLimit(submission);

		Map<String, Object> result = new HashMap<>(4, 1);
		String runtimeResultSlug = "SE";
		int usedTime = 0;
		int usedMemory = 0;
		
		try {
			LOGGER.info(String.format("[Submission #%d] 正在使用命令运行 %s (时间限制=%d, 内存限制=%s)",
                    submission.getSubmissionId(), commandLine, timeLimit, memoryLimit));
			Map<String, Object> runtimeResult = getRuntimeResult(commandLine, 
					systemUsername, systemPassword, inputFilePath, outputFilePath, 
					timeLimit, memoryLimit);
			
			int exitCode = (Integer) runtimeResult.get("exitCode");
			usedTime = (Integer) runtimeResult.get("usedTime");
			usedMemory = (Integer) runtimeResult.get("usedMemory");
			runtimeResultSlug = getRuntimeResultSlug(exitCode, timeLimit, usedTime, memoryLimit, usedMemory);
		} catch ( Exception ex ) {
			LOGGER.catching(ex);
		}

		result.put("runtimeResult", runtimeResultSlug);
		result.put("usedTime", usedTime);
		result.put("usedMemory", usedMemory);
		return result;
	}
	
	/**
	 * 获取待执行的命令行.
	 * @param submission - 评测记录对象
	 * @param workDirectory - 编译生成结果的目录以及程序输出的目录
	 * @param baseFileName - 待执行的应用程序文件名(不包含文件后缀)
	 * @return 待执行的命令行
	 */
	private String getCommandLine(Submission submission, 
			String workDirectory, String baseFileName) {
		Language language = submission.getLanguage();
		String filePathWithoutExtension = String.format("%s/%s",
                workDirectory, baseFileName);
		StringBuilder runCommand = new StringBuilder(language.getRunCommand()
													.replaceAll("\\{filename}", filePathWithoutExtension));
		
		if ( language.getLanguageName().equalsIgnoreCase("Java") ) {
			int lastIndexOfSpace = runCommand.lastIndexOf("/");
			runCommand.setCharAt(lastIndexOfSpace, ' ');
		}
		return runCommand.toString();
	}

	/**
	 * 根据不同语言获取最大时间限制.
	 * @param submission - 评测记录对象
	 * @return 最大时间限制
	 */
	private int getTimeLimit(Submission submission) {
		Language language = submission.getLanguage();
		int timeLimit = submission.getProblem().getTimeLimit();
		
		if ( language.getLanguageName().equalsIgnoreCase("Java") ) {
			timeLimit *= 2;
		}
		return timeLimit;
	}
	
	/**
	 * 根据不同语言获取最大空间限制.
	 * @param submission - 评测记录对象
	 * @return 最大空间限制
	 */
	private int getMemoryLimit(Submission submission) {
        return submission.getProblem().getMemoryLimit();
	}

	/**
	 * 根据JNI返回的结果封装评测结果.
	 * @param exitCode - 程序退出状态位
	 * @param timeLimit - 最大时间限制
	 * @param timeUsed - 程序运行所用时间
	 * @param memoryLimit - 最大空间限制
	 * @param memoryUsed - 程序运行所用空间(最大值)
	 * @return 程序运行结果的唯一英文缩写
	 */
	private String getRuntimeResultSlug(int exitCode, int timeLimit, int timeUsed, int memoryLimit, int memoryUsed) {
		if ( exitCode == 0 ) {
			// 将在下一步进行输出结果的比较
			return "AC";
		}
		if ( timeUsed >= timeLimit ) {
			return "TLE";
		}
		if ( memoryUsed >= memoryLimit ) {
			return "MLE";
		}
		return "RE";
	}
	
	/**
	 * 获取(编译)程序运行结果.
	 * @param commandLine - 待执行程序的命令行
	 * @param outputFilePath - 输出文件路径(可为NULL)
	 * @param timeLimit - 时间限制(单位ms, 0表示不限制)
	 * @param memoryLimit - 内存限制(单位KB, 0表示不限制)
	 * @return 一个包含程序运行结果的Map<String, Object>对象
	 */
    Map<String, Object> getRuntimeResult(String commandLine,
                                         String outputFilePath, int timeLimit,
                                         int memoryLimit) {
		Map<String, Object> result = null;
		try {
			result = getRuntimeResult(commandLine, systemUsername, systemPassword,
                    null, outputFilePath, timeLimit, memoryLimit);
		} catch ( Exception ex ) {
			LOGGER.catching(ex);
		}
		return result;
	}

	/**
	 * 获取程序运行结果.
	 * @param commandLine - 待执行程序的命令行
	 * @param systemUsername - 登录操作系统的用户名
	 * @param systemPassword - 登录操作系统的密码
	 * @param inputFilePath - 输入文件路径(可为NULL)
	 * @param outputFilePath - 输出文件路径(可为NULL)
	 * @param timeLimit - 时间限制(单位ms, 0表示不限制)
	 * @param memoryLimit - 内存限制(单位KB, 0表示不限制)
	 * @return 一个包含程序运行结果的Map<String, Object>对象
	 */
	public native Map<String, Object> getRuntimeResult(String commandLine,
			String systemUsername, String systemPassword, String inputFilePath,
			String outputFilePath, int timeLimit, int memoryLimit);

	/**
	 * 登录操作系统的用户名. 
	 * 为了安全, 我们建议评测程序以低权限的用户运行.
	 */
	@Value("${system.username}")
	private String systemUsername;

	/**
	 * 登录操作系统的密码. 
	 * 为了安全, 我们建议评测程序以低权限的用户运行.
	 */
	@Value("${system.password}")
	private String systemPassword;

	private static final Logger LOGGER = LogManager.getLogger(Runner.class);

	static {
		try {
			NativeLibraryLoader.loadLibrary("JudgerCore");
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.catching(ex);
		}
	}
}
