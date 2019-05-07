package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 试题的Model.
 * 对应数据库中的voj_problems数据表.
 */
@Getter
@Setter
public class Problem implements Serializable {

	/**
	 * 试题的唯一标识符.
	 */
	private long problemId;
	
	/**
	 * 试题是否公开.
	 */
	private boolean isPublic;
	
	/**
	 * 试题名称. 
	 */
	private String problemName;
	
	/**
	 * 最大运行时间.
	 */
	private int timeLimit;
	
	/**
	 * 最大运行内存.
	 */
	private int memoryLimit;
	
	/**
	 * 试题描述.
	 */
	private String description;
	
	/**
	 * 输入格式.
	 */
	private String inputFormat;
	
	/**
	 * 输出格式.
	 */
	private String outputFormat;
	
	/**
	 * 样例输入.
	 */
	private String sampleInput;
	
	/**
	 * 样例输出.
	 */
	private String sampleOutput;
	
	/**
	 * 试题提示.
	 */
	private String hint;
}
