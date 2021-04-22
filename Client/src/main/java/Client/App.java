package Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App{

	static Logger log = LogManager.getLogger(Logger.class);

	public static void main(String[] args) {

		AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		new SpeedTestWrapper("http://speedtest.ftp.otenet.gr/files/test10Mb.db");

	}

	public static void connectToServer(double downloadRate) {

	}

}
