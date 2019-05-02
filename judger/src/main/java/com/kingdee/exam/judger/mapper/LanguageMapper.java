package com.kingdee.exam.judger.mapper;

import java.util.List;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.kingdee.exam.judger.model.Language;

@CacheNamespace(implementation = org.mybatis.caches.ehcache.EhcacheCache.class)
public interface LanguageMapper {
	/**
	 * 通过编程语言的唯一标识符获取编程语言对象.
	 * @param languageId - 编程语言的唯一标识符
	 * @return 预期的编程语言对象或空引用
	 */
	@Select("SELECT * FROM voj_languages WHERE language_id = #{languageId}")
	@Options()
	@Results({
		 @Result(property = "languageId", column = "language_id"),
		 @Result(property = "languageSlug", column = "language_slug"),
		 @Result(property = "languageName", column = "language_name"),
		 @Result(property = "compileCommand", column = "language_compile_command"),
		 @Result(property = "runCommand", column = "language_run_command"),
	})
	Language getLanguageUsingId(@Param("languageId") int languageId);
	
	/**
	 * 通过编程语言的唯一英文缩写获取编程语言对象.
	 * @param languageSlug - 编程语言的唯一英文缩写
	 * @return 预期的编程语言对象或空引用
	 */
	@Select("SELECT * FROM voj_languages WHERE language_slug = #{languageSlug}")
	@Options()
	@Results({
		 @Result(property = "languageId", column = "language_id"),
		 @Result(property = "languageSlug", column = "language_slug"),
		 @Result(property = "languageName", column = "language_name"),
		 @Result(property = "compileCommand", column = "language_compile_command"),
		 @Result(property = "runCommand", column = "language_run_command"),
	})
	Language getLanguageUsingSlug(@Param("languageSlug") String languageSlug);
	
	/**
	 * 获取支持的编程语言.
	 * @return 编程语言列表(List<Language>对象)
	 */
	@Select("SELECT * FROM voj_languages")
	@Options()
	@Results({
		 @Result(property = "languageId", column = "language_id"),
		 @Result(property = "languageSlug", column = "language_slug"),
		 @Result(property = "languageName", column = "language_name"),
		 @Result(property = "compileCommand", column = "language_compile_command"),
		 @Result(property = "runCommand", column = "language_run_command"),
	})
	List<Language> getAllLanguages();
}
