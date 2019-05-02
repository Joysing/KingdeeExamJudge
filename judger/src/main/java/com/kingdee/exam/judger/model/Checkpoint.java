package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 程序测试点的Model.
 * 对应数据库中的voj_problem_checkpoints数据表.
 */
@Getter
@Setter
public class Checkpoint implements Serializable {

	public String toString() {
		return String.format("CheckPoint [ProblemId=%d, CheckpointId=%d, Score=%d]",
                problemId, checkpointId, score);
	}

	/**
	 * 试题的唯一标识符.
	 */
	private long problemId;
	
	/**
	 * 测试点的唯一标识符.
	 */
	private int checkpointId;
	
	/**
	 * 是否精确匹配测试点.
	 */
	private boolean isExactlyMatch;
	
	/**
	 * 测试点分值.
	 */
	private int score;
	
	/**
	 * 测试点的标准输入.
	 */
	private String input;
	
	/**
	 * 测试点的标准输出.
	 */
	private String output;
	
}
