package com.kingdee.exam.judger.messenger;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发送服务.
 */
@Component
public class MessageSender {
    @Autowired
    public MessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
	 * 发送消息至消息队列.
	 * @param mapMessage - Key-Value格式的消息
	 */
	public void sendMessage(final Map<String, Object> mapMessage) {
		jmsTemplate.convertAndSend(mapMessage);
	}

	/**
	 * 用于发送消息至消息队列.
	 */
	private final JmsTemplate jmsTemplate;
}