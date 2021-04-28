package Server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class FFmpegWrapper {

    private final static String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private final static String FFPROBE_PATH = "/usr/bin/ffprobe";

    private static FFmpeg ffmpeg = null;
    private static FFprobe ffprobe = null;

    protected enum videoType { avi, mp4, mkv }
    protected enum videoResolution { _240p, _360p, _480p, _720p, _1080p }
    protected enum streamingProtocol { TCP, UDP, RTP_UDP };

    private void initiateFFprobe() throws IOException {
        ffprobe = new FFprobe(FFPROBE_PATH);
    }

    private void initiateFFmpeg() throws IOException {
        ffmpeg = new FFmpeg(FFMPEG_PATH);
    }

    public static java.util.Map<String, Integer> getFileResolution(java.nio.file.Path path) {

        java.util.Map<String, Integer> resolution = new java.util.HashMap<String, Integer>();

        try {
            new FFmpegWrapper().initiateFFprobe();

            FFmpegStream stream = ffprobe.probe(path.toString()).getStreams().get(0);

            resolution.put("Width", stream.width);
            resolution.put("Height", stream.height);

        } catch(IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }

        return resolution;
    }

    public static void convertFile(VideoFile file, videoType type, videoResolution resolution, Path outputFile) {

        final int videoResolution = Integer.parseInt(resolution.toString().substring(1, resolution.toString().length() - 1));
        final double originalVideoAspectRatio = file.getWidth() / (double)file.getHeight() ;

        int outputVideoWidth = (int)Math.floor(videoResolution * originalVideoAspectRatio);

        if(outputVideoWidth % 2 == 1) {
            outputVideoWidth++;
        }

        try {
            new FFmpegWrapper().initiateFFmpeg();
            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(file.getPath().toAbsolutePath().toString())
                .addOutput(outputFile.toAbsolutePath().toString())
                .setVideoResolution(outputVideoWidth, videoResolution)
                .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder).run();

        } catch (IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }

    }

    public void streamVideo(VideoFile video, String protocol, ObjectOutputStream oos) {

        ProcessBuilder pb = null;
        List<String> commandArgs = new ArrayList<>();

        String videoCodecName;
        switch(video.getExtension().name()) { // change video codec name to ffmpeg known formats
            case "mp4":
                videoCodecName = "mpegts";
                break;
            case "mkv":
                videoCodecName = "matroska";
                break;
            default:
                videoCodecName = "avi";
                break;
        }

        /* TCP, UDP, RTP/UDP */
        try {

            commandArgs.add(FFMPEG_PATH);
            commandArgs.addAll(Arrays.asList("-i", video.getPath().toAbsolutePath().toString())); // Filepath

            switch(protocol) {
                case "TCP":
                    commandArgs.addAll(Arrays.asList("-f", videoCodecName));
                    commandArgs.add(String.format("tcp://%s:%d?listen", App.inetAddress.getHostAddress(), App.SERVER_VIDEO_PORT));
                    break;
                case "UDP":
                    commandArgs.addAll(Arrays.asList("-f", videoCodecName));
                    commandArgs.add(String.format("udp://%s:%d", App.inetAddress.getHostAddress(), App.SERVER_VIDEO_PORT));
                    break;
                case "RTP/UDP":
                    commandArgs.add(1, "-re"); // add -re after the command
                    commandArgs.add("-an");
                    //commandArgs.addAll(Arrays.asList("-c:v", "copy")); // Does not work
                    commandArgs.addAll(Arrays.asList("-f", "rtp"));
                    commandArgs.addAll(Arrays.asList("-sdp_file", "video.sdp"));
                    commandArgs.add(String.format("rtp://%s:%d", App.inetAddress.getHostAddress(), App.SERVER_VIDEO_PORT));

                    break;
                default:
                    throw new Exception("A protocol must be specified between \"TCP,UDP,RTP/UDP\"");
            }

            pb = new ProcessBuilder(commandArgs);
            pb.inheritIO();

            if(protocol.equals("RTP/UDP")) {

                Process process = pb.start();

                File rdpFile = new File(System.getProperty("user.dir") + File.separator + "video.sdp");

                oos.writeObject(Files.readAllBytes(rdpFile.toPath())); // Sending rtp description file to client

                process.waitFor();

            }

        } catch(Exception ex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ex.getMessage());
        }

    }

}
