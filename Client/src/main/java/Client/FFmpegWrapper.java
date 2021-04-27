package Client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

public class FFmpegWrapper {

    private final static String FFPLAY_PATH = "/usr/bin/ffplay";

    public static void playVideo(String protocol) {

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
                    break;
            }

            pb = new ProcessBuilder(commandArgs);

            pb.inheritIO();

            /*
            pb.redirectOutput(Redirect.appendTo(new PrintStream(new OutputStream(){
                    public void write(int b) {

                    }
                })));
                */

            //printCommand(pb);
            pb.start();

        } catch(IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }

    }

    private static void printCommand(ProcessBuilder pb) {
        AppLogger.log(AppLogger.LogLevel.DEBUG, pb.command().toString());
    }

}
