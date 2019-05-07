package com.kingdee.exam.judger.exception;

/**
 * 无效的提交记录异常.
 * 当getSubmission(long)操作返回null时被抛出.
 */
public class IllegalSubmissionException extends Exception {
	public IllegalSubmissionException(String message) {
		super(message);
	}
}
