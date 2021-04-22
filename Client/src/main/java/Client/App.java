package Client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class App implements ISpeedTestListener {

	static Logger log = LogManager.getLogger(Logger.class);
    private final static int SOCKET_TIMEOUT = 5000;
    private SpeedTestSocket speedTestSocket;

	public static void main(String[] args) {

		AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		new App().speedTest("http://speedtest.ftp.otenet.gr/files/test10Mb.db");

	}

	private void speedTest(String url) {

		AppLogger.log(AppLogger.LogLevel.INFO, "Speedtest Starting");

        speedTestSocket = new SpeedTestSocket();
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);

		speedTestSocket.addSpeedTestListener(this);

        speedTestSocket.startDownload(url);
	}

	public void connectToServer(double downloadRate) {
		System.out.println(downloadRate);
	}

	@Override
	public void onCompletion(final SpeedTestReport report) {
		//called when download/upload is complete
		connectToServer(report.getTransferRateBit().doubleValue() * 0.000001);
		AppLogger.log(AppLogger.LogLevel.INFO, String.format("Download rate : %.1f Mbps",report.getTransferRateBit().doubleValue() * 0.000001));
	}

	@Override
	public void onError(final SpeedTestError speedTestError, final String errorMessage) {
		AppLogger.log(AppLogger.LogLevel.ERROR, String.format("%s : %s\n", speedTestError, errorMessage));
	}

	@Override
	public void onProgress(final float percent, final SpeedTestReport downloadReport) {
	}

}
