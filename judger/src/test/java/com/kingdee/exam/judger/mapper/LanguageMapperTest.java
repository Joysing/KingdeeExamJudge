package com.kingdee.exam.judger.mapper;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.kingdee.exam.judger.model.Language;

/**
 * LanguageMapper测试类.
 */
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration({"classpath:test-spring-context.xml"})
public class LanguageMapperTest {
	/**
	 * 测试用例: 测试getLanguageUsingId(int)方法
	 * 测试数据: C语言的编程语言唯一标识符
	 * 预期结果: 返回C语言的编程语言对象
	 */
	@Test
	public void testGetLanguageUsingIdExists() {
		Language language = languageMapper.getLanguageUsingId(1);
		Assertions.assertNotNull(language);
		
		String languageName = language.getLanguageName();
		Assertions.assertEquals("C", languageName);
	}
	
	/**
	 * 测试用例: 测试getLanguageUsingId(int)方法
	 * 测试数据: 不存在的编程语言唯一标识符
	 * 预期结果: 返回空引用
	 */
	@Test
	public void testGetLanguageUsingIdNotExists() {
		Language language = languageMapper.getLanguageUsingId(0);
		Assertions.assertNull(language);
	}
	
	/**
	 * 测试用例: 测试getLanguageUsingSlug(String)方法
	 * 测试数据: C语言的编程语言唯一英文缩写
	 * 预期结果: 返回C语言的编程语言对象
	 */
	@Test
	public void testGetLanguageUsingSlugExists() {
		Language language = languageMapper.getLanguageUsingSlug("text/x-csrc");
		Assertions.assertNotNull(language);
		
		String languageName = language.getLanguageName();
		Assertions.assertEquals("C", languageName);
	}
	
	/**
	 * 测试用例: 测试getLanguageUsingSlug(String)方法
	 * 测试数据: 不存在的编程语言唯一英文缩写
	 * 预期结果: 返回空引用
	 */
	@Test
	public void testGetLanguageUsingSlugNotExists() {
		Language language = languageMapper.getLanguageUsingSlug("Not-Exists");
		Assertions.assertNull(language);
	}

	/**
	 * 测试用例: 测试getAllLanguages()方法
	 * 测试数据: N/a
	 * 预期结果: 返回全部的编程语言列表(共6种语言)
	 */
	@Test
	public void testGetAllLanguages() {
		List<Language> languages = languageMapper.getAllLanguages();
		Assertions.assertNotNull(languages);
		Assertions.assertEquals(6, languages.size());
		
		Language firstLanguage = languages.get(0);
		Assertions.assertNotNull(firstLanguage);
		
		String languageName = firstLanguage.getLanguageName();
		Assertions.assertEquals("C", languageName);
	}
	/**
	 * 待测试的LanguageMapper对象.
	 */
	@Autowired
	private LanguageMapper languageMapper;
}
