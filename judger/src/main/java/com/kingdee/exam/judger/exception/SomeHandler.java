package com.kingdee.exam.judger.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Service
public class SomeHandler implements ErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(SomeHandler.class);
    @Override
    public void handleError(Throwable t) {
        LOGGER.error("ActiveMQ发生错误了！", t);
    }
}