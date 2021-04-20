package Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App{

	static Logger log = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		log.debug("This is debug");
		log.fatal("This is fatal");
		log.error("This is error");
		log.warn("This is warn");
		log.info("This is info");
	}

}
