package com.kingdee.exam.judger.application;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.kingdee.exam.judger.messenger.MessageSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.kingdee.exam.judger.mapper.UserMapper;
import com.kingdee.exam.judger.model.User;

/**
 * 应用程序心跳.
 * 用于向Web模块发送Keep-Alive信息.
 */
@Component
public class ApplicationHeartbeat implements Runnable {
    @Autowired
    public ApplicationHeartbeat(MessageSender messageSender, UserMapper userMapper) {
        this.messageSender = messageSender;
        this.userMapper = userMapper;
    }

    public void run() {
		if ( !isIdentityValid() ) {
			LOGGER.error("验证失败: 请检查评测机登录数据库的用户名和密码");
			System.exit(-1);
		}
		Calendar calendar = Calendar.getInstance();
		long currentTime = calendar.getTimeInMillis();
		
		Map<String, Object> mapMessage = new HashMap<>();
		mapMessage.put("event", "KeepAlive");
		mapMessage.put("username", judgerUsername);
		mapMessage.put("description", getDescription());
		mapMessage.put("heartbeatTime", currentTime);
		messageSender.sendMessage(mapMessage);
		LOGGER.info("发送心跳到web服务器。");
	}
	
	/**
	 * 检查评测机的身份信息是否有效.
	 * @return 评测机的身份信息是否有效
	 */
	private boolean isIdentityValid() {
		User user = userMapper.getUserUsingUsername(judgerUsername);
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		return user != null && "ROLE_JUDGE".equals(user.getRoles()) &&
				encoder.matches(judgerPassword,user.getPassword());
	}
	
	/**
	 * 获取评测机的描述信息.
	 * @return 评测机的描述信息
	 */
	private String getDescription() {
		return judgerDescription;
	}
	
	/**
	 * 评测机的用户名.
	 */
	@Value("${judger.username}")
	private String judgerUsername;
	
	/**
	 * 评测机的密码.
	 */
	@Value("${judger.password}")
	private String judgerPassword;
	
	/**
	 * 评测机的描述.
	 */
	@Value("${judger.description}")
	private String judgerDescription;
	
	/**
	 * 用于向消息队列发送消息.
	 */
	private final MessageSender messageSender;
	
	/**
	 * 用于验证评测机的身份信息.
	 */
	private final UserMapper userMapper;
	
	private static final Logger LOGGER = LogManager.getLogger(ApplicationHeartbeat.class);
}
