package Client;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

/**
 * This class implements ISpeedTestListener interface in order to use speed test utility
 */
public class App implements ISpeedTestListener {
	//
	// Socket attributes
	protected static final int SERVER_PORT = 9999;
	protected static final String SERVER_IP = "127.0.0.1";
	protected static final int SERVER_VIDEO_PORT = 1234;

	// SpeedTest attributes
	static Logger log = LogManager.getLogger(Logger.class);
    private final static int SOCKET_TIMEOUT = 5000;
    private SpeedTestSocket speedTestSocket;

	public static void main(String[] args) {

		AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		// Get 10mb file from otenet ftp -> because is close to us
		new App().speedTest("http://speedtest.ftp.otenet.gr/files/test10Mb.db");

	}

	// speed test method
	private void speedTest(String url) {

		AppLogger.log(AppLogger.LogLevel.INFO, "Speedtest Starting");

        speedTestSocket = new SpeedTestSocket();
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);

		speedTestSocket.addSpeedTestListener(this);

        speedTestSocket.startDownload(url);
	}

	@Override
	//called when download/upload is complete
	public void onCompletion(final SpeedTestReport report) {

		//double speedTestValue = report.getTransferRateBit().doubleValue() * 0.000001;
		BigDecimal speedTestValue = report.getTransferRateBit();
		AppLogger.log(AppLogger.LogLevel.INFO, String.format("Download rate : %.1f Mbps", speedTestValue.doubleValue() * 0.000001));

		new SocketClient().connectToServer(speedTestValue);

	}

	@Override
	public void onError(final SpeedTestError speedTestError, final String errorMessage) {
		AppLogger.log(AppLogger.LogLevel.ERROR, String.format("%s : %s\n", speedTestError, errorMessage));
	}

	@Override
	public void onProgress(final float percent, final SpeedTestReport downloadReport) {
	}

}
