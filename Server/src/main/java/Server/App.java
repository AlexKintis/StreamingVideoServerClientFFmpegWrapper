package Server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.simple.SimpleLoggerContext;


public class App{

	private static Logger log = LogManager.getLogger(App.class);

	public static enum LogLevel {
		DEBUG,
		FATAL,
		ERROR,
		WARN,
		INFO
	}

	public static void main(String[] args) {
		VideoHandler handler = new VideoHandler();

		for(var i : handler.getVideoFiles().keySet()) {
			System.out.println(i + " " + handler.getVideoFiles().get(i));
		}

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
