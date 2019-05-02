package com.kingdee.exam.judger.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.kingdee.exam.judger.model.User;

/**
 * UserMapper测试类.
 */
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration({"classpath:test-spring-context.xml"})
public class UserMapperTest {
	/**
	 * 测试用例: 测试getUserUsingUsername(String)方法
	 * 测试数据: 使用用户名为admin的用户
	 * 预期结果: 返回预期的用户对象
	 */
	@Test
	public void testGetUserUsingUsernameExists() {
		User user = userMapper.getUserUsingUsername("admin");
		Assertions.assertNotNull(user);

        long uid = user.getUserId();
		Assertions.assertEquals(1, uid);
	}
	
	/**
	 * 测试用例: 测试getUserUsingUsername(String)方法
	 * 测试数据: 使用不存在的用户名
	 * 预期结果: 返回空引用
	 */
	@Test
	public void testGetUserUsingUsernameNotExists() {
		User user = userMapper.getUserUsingUsername("Not-Exists");
		Assertions.assertNull(user);
	}
	
	/**
	 * 待测试的UserMapper对象.
	 */
	@Autowired
	private UserMapper userMapper;
}
