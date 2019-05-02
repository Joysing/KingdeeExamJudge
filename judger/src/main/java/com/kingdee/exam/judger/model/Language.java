package com.kingdee.exam.judger.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 编程语言的Model.
 * 对应数据库中的voj_languages数据表.
 */
@Getter
@Setter
public class Language implements Serializable {
	@Override
	public String toString() {
		return String.format("Language [ID=%d, Slug=%s, Name=%s, CompileCommand=%s, runCommand=%s]",
                languageId, languageSlug, languageName, compileCommand, runCommand);
	}
	
	/**
	 * 编程语言的唯一标识符. 
	 */
	private int languageId;
	
	/**
	 * 编程语言的唯一英文简称.
	 */
	private String languageSlug;
	
	/**
	 * 编程语言的名称.
	 */
	private String languageName;
	
	/**
	 * 编程语言的编译命令.
	 */
	private String compileCommand;
	
	/**
	 * 编程语言的运行命令.
	 */
	private String runCommand;
	
}
