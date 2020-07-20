package org.cc.util;

import org.cc.org.apache.log4j.Level;
import org.cc.org.apache.log4j.LogManager;
import org.cc.org.apache.log4j.Logger;
import org.cc.org.apache.log4j.PropertyConfigurator;
import org.cc.org.apache.log4j.spi.LoggerFactory;

import java.net.URL;

/**
 * @author William
 */
public class CCLogger {

    private static final String FQCN = CCLogger.class.getName();

    private static Logger log = Logger.getLogger(CCLogger.class);

    static {
        URL url = CCLogger.class.getClassLoader().getResource("jo.properties");
        if (url != null) {
            PropertyConfigurator.configure(url);
        }
    }

    public static void info(Object msg) {
        log.log(FQCN, Level.INFO, msg, null);
    }

    public static void info(Object msg, Throwable t) {
        log.log(FQCN, Level.INFO, msg, t);
    }

    public static void error(Object msg) {
        log.log(FQCN, Level.ERROR, msg, null);
    }

    public static void error(Object msg, Throwable t) {
        log.log(FQCN, Level.ERROR, msg, t);
    }

    public static void debug(Object msg) {
        log.log(FQCN, Level.DEBUG, msg, null);
    }

    public static void debug(Object msg, Throwable t) {
        log.log(FQCN, Level.DEBUG, msg, t);
    }

    public static void warn(Object msg) {
        log.log(FQCN, Level.WARN, msg, null);
    }

    public static void warn(Object msg, Throwable t) {
        log.log(FQCN, Level.WARN, msg, t);
    }

    public static void fatal(Object msg, Throwable t) {
        log.log(FQCN, Level.FATAL, msg, t);
    }

    public static void fatal(Object msg) {
        log.log(FQCN, Level.FATAL, msg, null);
    }

    public static
        Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    public static
        Logger getLogger(Class clazz) {
        return LogManager.getLogger(clazz.getName());
    }

    public static
        Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    public static
        Logger getLogger(String name, LoggerFactory factory) {
        return LogManager.getLogger(name, factory);
    }

}
