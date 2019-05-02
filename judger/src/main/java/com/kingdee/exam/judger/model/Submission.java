package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 评测记录的Model.
 * 对应数据库中的voj_submissions数据表.
 */
@Getter
@Setter
public class Submission implements Serializable {
	/**
	 * 评测记录类的默认构造函数.
	 */
	public Submission() { }
	
	/**
	 * 评测记录类的构造函数.
	 * @param submissionId - 评测记录的唯一标识符
	 * @param problem - 评测对应的试题对象
	 * @param uid - 评测提交者的用户的唯一标识符
	 * @param language - 提交所使用的语言对象
	 * @param submitTime - 评测提交时间
	 * @param executeTime - 评测开始执行时间
	 * @param usedTime - 评测运行总时间
	 * @param usedMemory - 评测运行占用最大内存
	 * @param judgeResultSlug - 评测结果的唯一标识符
	 * @param judgeScore - 评测运行得分
	 * @param judgeLog - 评测运行日志
	 * @param code - 评测所执行的代码
	 */
	public Submission(long submissionId, Problem problem, long uid, Language language, Date submitTime, 
			Date executeTime, int usedTime, int usedMemory, String judgeResultSlug, int judgeScore, 
			String judgeLog, String code) { 
		this.submissionId = submissionId;
		this.problem = problem;
		this.uid = uid;
		this.language = language;
		this.submitTime = submitTime;
		this.executeTime = executeTime;
		this.usedTime = usedTime;
		this.usedMemory = usedMemory;
		this.judgeResultSlug = judgeResultSlug;
		this.judgeScore = judgeScore;
		this.judgeLog = judgeLog;
		this.code = code;
	}
	@Override
	public String toString() {
		return String.format("Submission [ID=%d, Problem={%s}, Uid={%s}, Language={%s}, SubmitTime={%s}, "
				+ "ExecuteTime={%s}, UsedTime=%d, UsedMemory=%d, JudgeResultSlug={%s}, JudgeScore=%d, "
				+ "JudgeLog=%s, Code=%s]",
                submissionId, problem, uid, language, submitTime, executeTime, usedTime, usedMemory,
                judgeResultSlug, judgeScore, judgeLog, code);
	}
	
	/**
	 * 评测记录的唯一标识符. 
	 */
	private long submissionId;
	
	/**
	 * 评测对应的试题对象.
	 */
	private Problem problem;
	
	/**
	 * 评测提交者用户的唯一标识符. 
	 */
	private long uid;
	
	/**
	 * 提交所使用的语言对象.
	 */
	private Language language;
	
	/**
	 * 评测提交时间.
	 */
	private Date submitTime;
	
	/**
	 * 评测开始执行时间.
	 */
	private Date executeTime;
	
	/**
	 * 评测运行总时间.
	 */
	private int usedTime;
	
	/**
	 * 评测运行占用最大内存.
	 */
	private int usedMemory;
	
	/**
	 * 评测结果.
	 */
	private String judgeResultSlug;
	
	/**
	 * 评测运行得分.
	 */
	private int judgeScore;
	
	/**
	 * 评测运行日志.
	 */
	private String judgeLog;
	
	/**
	 * 评测所执行的代码.
	 */
	private String code;
	
}
