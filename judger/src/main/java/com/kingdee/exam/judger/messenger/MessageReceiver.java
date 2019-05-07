package com.kingdee.exam.judger.messenger;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kingdee.exam.judger.application.ApplicationDispatcher;

/**
 * 消息接收服务.
 */
@Component
public class MessageReceiver implements MessageListener {
    @Autowired
    public MessageReceiver(ApplicationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void onMessage(Message message) {
		if ( message instanceof MapMessage ) {
			final MapMessage mapMessage = (MapMessage) message;
			
			try {
				String event = mapMessage.getString("event");
				
				if ( "SubmissionCreated".equals(event) ) {
					newSubmissionHandler(mapMessage);
				} else LOGGER.warn(String.format("接收到未知事件. [Event = %s]",
                        event));
			} catch (Exception ex) {
				LOGGER.catching(ex);
			}
		}
	}
	
	/**
	 * 处理新提交请求.
	 * @param mapMessage - 消息队列中收到的MapMessage对象
	 */
	private void newSubmissionHandler(MapMessage mapMessage) throws JMSException {
		long submissionId = mapMessage.getLong("submissionId");
		LOGGER.info(String.format("接收到新的submission任务 #%d",
                submissionId));
		
		dispatcher.onSubmissionCreated(submissionId);
	}
	
	/**
	 * 用于完成接收消息后的回调操作.
	 */
	private final ApplicationDispatcher dispatcher;

	private static final Logger LOGGER = LogManager.getLogger(MessageReceiver.class);
}
