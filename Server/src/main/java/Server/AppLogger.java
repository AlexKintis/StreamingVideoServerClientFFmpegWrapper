package Server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AppLogger Class
 */
public class AppLogger {

    private static Logger log = LogManager.getLogger(Logger.class);

    protected static enum LogLevel {
        DEBUG,
        FATAL,
        ERROR,
        WARN,
        INFO
    }

    public static Logger log(LogLevel logLevel, String message) {
        switch(logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case FATAL:
                log.fatal(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case INFO:
                log.info(message);
                break;
        default:
            log.error("Wrong logger type");
            System.exit(1);
        }
        return log;
    }

}
