package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 程序评测结果的Model.
 * 对应数据库中的voj_judge_results数据表.
 */
@Getter
@Setter
public class JudgeResult implements Serializable {
	@Override
	public String toString() {
		return String.format("JudgeResult [Id=%d, Slug=%s, Name=%s]",
                judgeResultId, judgeResultSlug, judgeResultName);
	}

	/**
	 * 评测结果的唯一标识符.
	 */
	private int judgeResultId;
	
	/**
	 * 评测结果的英文唯一缩写.
	 */
	private String judgeResultSlug;
	
	/**
	 * 评测结果的名称.
	 */
	private String judgeResultName;
}
