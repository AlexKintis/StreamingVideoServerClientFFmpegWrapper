package Client;

import java.math.BigDecimal;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestWrapper extends App {

    private String SPEED_TEST_SERVER_URI_DL;
    private final static int SOCKET_TIMEOUT = 5000;

    private SpeedTestSocket speedTestSocket;

    SpeedTestWrapper(String URL) {
        this.SPEED_TEST_SERVER_URI_DL = URL;

        speedTestSocket = new SpeedTestSocket();
        speedTestSocket.setSocketTimeout(SOCKET_TIMEOUT);

        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(final SpeedTestReport report) {
                //called when download/upload is complete
                App.connectToServer(report.getTransferRateBit().doubleValue() * 0.000001);
                AppLogger.log(AppLogger.LogLevel.INFO, String.format("Download rate : %.1f Mbps",report.getTransferRateBit().doubleValue() * 0.000001));
            }

            @Override
            public void onError(final SpeedTestError speedTestError, final String errorMessage) {
                AppLogger.log(AppLogger.LogLevel.ERROR, String.format("%s : %s\n", speedTestError, errorMessage));
            }

            @Override
            public void onProgress(final float percent, final SpeedTestReport downloadReport) {
            }

        });

        speedTestSocket.startDownload(SPEED_TEST_SERVER_URI_DL);
    }


}
