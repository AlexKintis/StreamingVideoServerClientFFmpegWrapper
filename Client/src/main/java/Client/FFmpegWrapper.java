package Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FFmpegWrapper {

    private static final String FFPLAY_PATH = "/usr/bin/ffplay";
    protected static File rdpFile = new File(System.getProperty("user.dir") + File.separator + "video.sdp");

    public static void playVideo(String protocol, ObjectInputStream ois) {

        ProcessBuilder pb = null;
        List<String> commandArgs = new ArrayList<>();

        commandArgs.add(FFPLAY_PATH);

        try {

            switch(protocol) {
                case "TCP":
                    //ffplay tcp://127.0.0.1:1234
                    commandArgs.add(String.format("tcp://%s:%d", App.SERVER_IP, App.SERVER_VIDEO_PORT));
                    break;
                case "UDP":
                    commandArgs.add(String.format("udp://%s:%d", App.SERVER_IP, App.SERVER_VIDEO_PORT));
                    break;
                case "RTP/UDP":

                    Files.write(rdpFile.toPath(), (byte[])ois.readObject()); // Receive Rdp File

                    // ffplay -protocol_whitelist file,rtp,udp -i video.sdp
                    commandArgs.addAll(Arrays.asList("-protocol_whitelist", "file,rtp,udp"));
                    commandArgs.addAll(Arrays.asList("-i", "video.sdp"));

                    break;
            }

            pb = new ProcessBuilder(commandArgs);

            pb.inheritIO();

            Process process = pb.start();
            process.waitFor();

        } catch(IOException | ClassNotFoundException | InterruptedException ex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ex.getMessage());
            System.exit(1);
        }

    }

    private static void printCommand(ProcessBuilder pb) {
        AppLogger.log(AppLogger.LogLevel.DEBUG, pb.command().toString());
    }

}
