package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class Server extends App {

    private ServerSocket server = null;
    private Socket socket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    protected SortedMap<FFmpegWrapper.videoResolution, HashMap<String, Integer>> speedEquivalentResolutions = new TreeMap<>();

    Server() {
       initializeSpeedEquivalentResolutions();
    }

    public void start() {

        AppLogger.log(AppLogger.LogLevel.INFO, "Server is starting");

        try {

            server = new ServerSocket(super.SERVER_PORT);

            AppLogger.log(AppLogger.LogLevel.INFO, "Waiting for client request");

            while (true) {

                socket = server.accept();

                if(socket.isBound()) {
                    AppLogger.log(AppLogger.LogLevel.INFO, "Client Connected");

                    ois = new ObjectInputStream(socket.getInputStream());
                    oos = new  ObjectOutputStream(socket.getOutputStream());

                    User user = new User((BigDecimal)ois.readObject());
                    AppLogger.log(AppLogger.LogLevel.INFO, String.format("Client connection to server is [%f kbps | %f Mbps]", user.userDownloadRatekbps, user.userDownloadRatekbps * 0.001) );

                    // View which videos can user ask for stream

                    //oos.writeObject("Hey From server");

                    ois.close();
                    oos.close();
                    socket.close();

                }

            }

        } catch (IOException | ClassNotFoundException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }


    }

    public void initializeSpeedEquivalentResolutions() {

       // 1080p
       HashMap<String, Integer> hmap1080 = new HashMap<>();
       hmap1080.put("Maximum", 6000);
       hmap1080.put("Recommented", 4500);
       hmap1080.put("Minimum", 3000);

       speedEquivalentResolutions.put(FFmpegWrapper.videoResolution._1080p, hmap1080);

       // 720p
       HashMap<String, Integer> hmap720 = new HashMap<>();
       hmap720.put("Maximum", 4000);
       hmap720.put("Recommented", 2500);
       hmap720.put("Minimum", 1500);

       speedEquivalentResolutions.put(FFmpegWrapper.videoResolution._720p, hmap720);

       // 480
       HashMap<String, Integer> hmap480 = new HashMap<>();
       hmap480.put("Maximum", 2000);
       hmap480.put("Recommented", 1000);
       hmap480.put("Minimum", 500);

       speedEquivalentResolutions.put(FFmpegWrapper.videoResolution._480p, hmap480);

       // 360
       HashMap<String, Integer> hmap360 = new HashMap<>();
       hmap360.put("Maximum", 1000);
       hmap360.put("Recommented", 750);
       hmap360.put("Minimum", 400);

       speedEquivalentResolutions.put(FFmpegWrapper.videoResolution._360p, hmap360);

       // 240
       HashMap<String, Integer> hmap240 = new HashMap<>();
       hmap240.put("Maximum", 700);
       hmap240.put("Recommented", 400);
       hmap240.put("Minimum", 300);

       speedEquivalentResolutions.put(FFmpegWrapper.videoResolution._240p, hmap240);

    }

}
