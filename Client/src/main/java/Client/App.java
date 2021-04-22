package Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App{

	static Logger log = LogManager.getLogger(Logger.class);

	public static void main(String[] args) {
		AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");
	}

}
