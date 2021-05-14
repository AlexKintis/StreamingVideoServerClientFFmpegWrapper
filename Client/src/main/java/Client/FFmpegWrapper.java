package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FFmpegWrapper extends Thread {

    private static final String FFPLAY_PATH = "/usr/bin/ffplay";
    protected static File rdpFile = new File(System.getProperty("user.dir") + File.separator + "video.sdp");

    public static void playVideo(String protocol, ObjectInputStream ois, int selectedVideoResolution) {

        ProcessBuilder pb = null;
        List<String> commandArgs = new ArrayList<>();

        commandArgs.add(FFPLAY_PATH);

        try {

            final String message = "Protocol used : ";
            if(protocol.equals("None")) {
                switch(selectedVideoResolution) {
                    case 240:
                        protocol = "TCP";
                        break;
                    case 360:
                        protocol = "UDP";
                        break;
                    case 480:
                        protocol = "UDP";
                        break;
                    case 720:
                        protocol = "RTP/UDP";
                    case 1080:
                        protocol = "RTP/UDP";
                        break;
                }
            }


            switch(protocol) {
                case "TCP":
                    //ffplay tcp://127.0.0.1:1234
                    commandArgs.add(String.format("tcp://%s:%d", App.SERVER_IP, App.SERVER_VIDEO_PORT));
                    AppLogger.log(AppLogger.LogLevel.INFO, message + protocol);
                    break;
                case "UDP":
                    commandArgs.add(String.format("udp://%s:%d", App.SERVER_IP, App.SERVER_VIDEO_PORT));
                    AppLogger.log(AppLogger.LogLevel.INFO, message + protocol);
                    break;
                case "RTP/UDP":

                    Files.write(rdpFile.toPath(), (byte[])ois.readObject()); // Receive Rdp File

                    // ffplay -protocol_whitelist file,rtp,udp -i video.sdp
                    commandArgs.addAll(Arrays.asList("-protocol_whitelist", "file,rtp,udp"));
                    commandArgs.addAll(Arrays.asList("-i", "video.sdp"));

                    AppLogger.log(AppLogger.LogLevel.INFO, message + protocol);

                    break;
            }

            pb = new ProcessBuilder(commandArgs);

            pb.inheritIO();

            if(protocol.equals("RTP/UDP")) {
                synchronized (pb){
                    try{
                        pb.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            SocketClient.FFmpegProccess = pb.start();
            SocketClient.FFmpegProccess.waitFor();

        } catch(ClassNotFoundException | InterruptedException ex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ex.getMessage());
            System.exit(1);
        } catch(IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.WARN, ioex.getMessage());
        }

    }

    private static void printCommand(ProcessBuilder pb) {
        AppLogger.log(AppLogger.LogLevel.DEBUG, pb.command().toString());
    }

}
