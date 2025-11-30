package cn.ivfzhou.java.log4j;

import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;

public final class Log4jTest {

    public final static org.slf4j.Logger logger = LoggerFactory.getLogger(Log4jTest.class);
    public final static org.apache.logging.log4j.Logger logger2 = LogManager.getLogger(Log4jTest.class);
    public final static java.util.logging.Logger logger3 = java.util.logging.Logger.getLogger(Log4jTest.class.getName());

    public static void main(String[] args) {
        logger.info("hello {} {}", "slf4j", 2);
        logger2.info("hello {} {}", "log4j", 2);
        logger3.info("hello jul");
    }

}
