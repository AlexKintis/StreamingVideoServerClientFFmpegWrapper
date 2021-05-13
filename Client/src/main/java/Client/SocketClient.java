package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SocketClient extends App {

    private static Socket socket = null;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private Scanner sc = new Scanner(System.in);

    protected static Process FFmpegProccess;

    SocketClient() {
        AppLogger.log(AppLogger.LogLevel.INFO, "Client is starting");
    }

    public void connectToServer(BigDecimal downloadRate) {

        try {
            AppLogger.log(AppLogger.LogLevel.INFO, String.format("Client is trying to connect to %s:%d", super.SERVER_IP, super.SERVER_PORT));

            socket = new Socket(super.SERVER_IP, super.SERVER_PORT);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(downloadRate);

            startVideoSelectionProcess();

            oos.close();
            ois.close();
            socket.close();

            if(FFmpegProccess != null) {
                AppLogger.log(AppLogger.LogLevel.INFO, "FFplay Process Stopped!");
                FFmpegProccess.destroy();
            }

        } catch(Exception ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        } finally {

        }
    }

    private void startVideoSelectionProcess() throws Exception {

        String choice = "";

        // Video name
        System.out.println(ois.readObject());
        var videoNames = (ArrayList<String>)ois.readObject();
        videoNames .forEach(k -> System.out.format("%d. %s\n", videoNames.indexOf(k) + 1 ,k));

        choice = sc.nextLine();

        choice = videoNames.get(Integer.parseInt(choice) - 1);
        oos.writeObject(choice);

        // Resolution
        System.out.println((String)ois.readObject());
        var videoResolutions = (ArrayList<Integer>)ois.readObject();
        videoResolutions.forEach(k -> System.out.format("%d. %s\n", videoResolutions.indexOf(k) + 1 ,k));

        choice = sc.nextLine();
        oos.writeObject(String.valueOf(videoResolutions.get(Integer.parseInt(choice) - 1)));


        // codec
        System.out.println((String)ois.readObject());

        var videoCodecs = (ArrayList<String>)ois.readObject();
        videoCodecs.forEach(k -> System.out.format("%d. %s\n", videoCodecs.indexOf(k) + 1 ,k));

        choice = sc.nextLine();
        choice = videoCodecs.get(Integer.parseInt(choice) - 1);

        oos.writeObject(choice);

        // Stream Method
        System.out.println((String)ois.readObject());

        var streamMethod = (ArrayList<String>)ois.readObject();

        streamMethod.forEach(k -> System.out.format("%d. %s\n", streamMethod.indexOf(k) + 1 ,k));

        choice = sc.nextLine();
        choice = streamMethod.get(Integer.parseInt(choice) - 1);

        oos.writeObject(choice);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(InterruptedException ex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ex.getMessage());
        }

        FFmpegWrapper.playVideo(choice, ois);

        // if(FFmpegWrapper.rdpFile.exists()) {
        //     FFmpegWrapper.rdpFile.delete();
        // }

    }

}
