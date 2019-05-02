package com.kingdee.exam.judger.core;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.kingdee.exam.judger.mapper.SubmissionMapper;
import com.kingdee.exam.judger.model.Submission;

/**
 * 程序执行器的测试类.
 */
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration({"classpath:test-spring-context.xml"})
public class RunnerTest {
	/**
	 * 测试用例: 测试getRuntimeResult(Submission, int, String, String, String, String)方法
	 * 测试数据: 使用可以编译通过的C++代码
	 * 预期结果: 编译成功并运行产生输出
	 */
	@Test
	public void testGetRuntimeResultCpp() throws Exception {
		String workDirectory = workBaseDirectory + "/voj-1000";
		String baseFileName = "random-name";
		Submission submission = submissionMapper.getSubmission(1000);

		String inputFilePath = workBaseDirectory + "/testpoints/1000/input#0.txt";
		String outputFilePath = workBaseDirectory + "/voj-1000/output#0.txt";
		
		preprocessor.createTestCode(submission, workDirectory, baseFileName);
		preprocessor.fetchTestPoints(submission.getProblem().getProblemId());
		compiler.getCompileResult(submission, workDirectory, baseFileName);
		
		Map<String, Object> result =
				runner.getRuntimeResult(submission, workDirectory, baseFileName, inputFilePath, outputFilePath);
		Assertions.assertEquals("AC", result.get("runtimeResult"));
	}

	/**
	 * 待测试的Runner对象.
	 */
	@Autowired
	private Runner runner;
	
	/**
	 * 自动注入的Compiler对象.
	 * 用于构建测试用例.
	 */
	@Autowired
	private Compiler compiler;
	
	/**
	 * 自动注入的Preprocessor对象.
	 * 用于构建测试用例.
	 */
	@Autowired
	private Preprocessor preprocessor;
	
	/**
	 * 自动注入的SubmissionMapper对象.
	 * 用于构建测试用例.
	 */
	@Autowired
	private SubmissionMapper submissionMapper;
	
	/**
	 * 评测机的工作目录.
	 * 用于存储编译结果以及程序输出结果.
	 */
	@Value("${judger.workDir}")
	private String workBaseDirectory;
}
