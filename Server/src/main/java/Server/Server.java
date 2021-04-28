package Server;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

//import org.apache.tools.ant.taskdefs.TempFile;

public class Server extends App {

    private ServerSocket server = null;
    private static Socket socket = null;
    private static ObjectInputStream ois = null;
    private static ObjectOutputStream oos = null;

    protected SortedMap<FFmpegWrapper.videoResolution, HashMap<String, Integer>> speedEquivalentResolutions = new TreeMap<>();
    private VideoFile candidateForStream;
    protected static Process FFmpegProcess;

    Server() {
        try{
            inetAddress = InetAddress.getByName(App.hostName);
        } catch(UnknownHostException uhex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, uhex.getMessage());
        }

        initializeSpeedEquivalentResolutions();
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

                    startVideoSelectionProcess(user);

                    ois.close();
                    oos.close();
                    socket.close();
                    /*
                    if(FFmpegProcess != null) {
                        AppLogger.log(AppLogger.LogLevel.INFO, "FFmpeg Process Stopped!");
                        FFmpegProcess.destroy();
                    }
                    */

                }

            }

        } catch (Exception ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }


    }

    /*
     *    < Initiate Socket Connection >
     *    Ask user for which video wants to see (File, Resolution, codec)
     *    and also asks for streaming protocol
     */
    private void startVideoSelectionProcess(User user) throws Exception {

        StringBuilder videoFileName = new StringBuilder();

        /*
         *    Video Name
         */
        oos.writeObject("Which video you would like to see : "); // 1
        oos.writeObject(user.getDinstictiveFileNames()); // 2

        String choosedFilename = (String)ois.readObject();

        videoFileName.append(choosedFilename);

        /*
         *    Video Resolution
         */
        oos.writeObject("In which resolution : "); // 3

        ArrayList<Integer> resolutions = new ArrayList<>();

        for(var resolution : FFmpegWrapper.videoResolution.values()) {
            String temp = resolution.name().substring(1, resolution.name().length() - 1);
            int numTemp = Integer.valueOf(temp);
            resolutions.add(numTemp);
        }

        ArrayList<VideoFile> tempUserFiles = new ArrayList<>();
        user.getFiles().forEach(k -> {
                if(k.getName().contains(videoFileName.toString())) {
                    tempUserFiles.add(k);
                }
            });

        resolutions.subList(resolutions.indexOf(tempUserFiles.get(0).getHeight())+1, resolutions.size()).clear();

        Collections.sort(resolutions, Collections.reverseOrder());

        oos.writeObject(resolutions); // 4
        videoFileName.append("-" + (String)ois.readObject());

        /*
         *    Video Codec
         */
        oos.writeObject("In which codec : "); // 5

        ArrayList<String> codecs = new ArrayList<>();

        for(var codec : FFmpegWrapper.videoType.values())
            codecs.add(codec.name());

        oos.writeObject(codecs); // 6

        videoFileName.append("p." + (String)ois.readObject());

        candidateForStream = user.getSelectedVideo(videoFileName.toString());
        //System.out.println(videoFileName); // print Video path

        /*
         *    Streaming Protocol
         */
        oos.writeObject("In wich streaming protocol : "); // 7

        ArrayList<String> streamProtArrList = new ArrayList<>();

        for(var streamProtocol : FFmpegWrapper.streamingProtocol.values())
            streamProtArrList.add(streamProtocol.name());

        streamProtArrList.set(streamProtArrList.indexOf("RTP_UDP"), "RTP/UDP");
        streamProtArrList.add("None");

        oos.writeObject(streamProtArrList);

        String protocol = (String)ois.readObject();

        new FFmpegWrapper().streamVideo(candidateForStream, protocol, oos);

        /* Delete rdp file if exists */
        File rdpFile = new File(System.getProperty("user.dir") + File.separator + "video.sdp");

        if(rdpFile.exists()) {
            rdpFile.delete();
        }

    }

}
