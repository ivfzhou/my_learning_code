package cn.ivfzhou.java.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogbackTest {

    private final static Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    public static void main(String[] args) {
        logger.info("hello {}", "logback");
        logger.warn("hello {}", "logback");
        logger.error("hello {}", "logback");
        logger.debug("hello {}", "logback");
    }

}
