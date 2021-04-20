package Server;

import java.io.IOException;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class FFmpegWrapper {

    private static FFmpeg ffmpeg = null;
    private static FFprobe ffprobe = null;

    protected enum videoType { avi, mp4, mkv }
    protected enum videoResolution { _240p, _360p, _480p, _720p, _1080p }

    private void initiateFFprobe(String path) {
        try {
            ffprobe = new FFprobe(path);
        } catch (IOException ioex) {
           AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
        }
    }

    private void initiateFFmpeg(String path) throws IOException {
        ffmpeg = new FFmpeg(path);
    }

    public static java.util.Map<String, Integer> getFileResolution(java.nio.file.Path path) {

        java.util.Map<String, Integer> resolution = new java.util.HashMap<String, Integer>();

        try {
            new FFmpegWrapper().initiateFFprobe("/usr/bin/ffprobe");

            FFmpegStream stream = ffprobe.probe(path.toString()).getStreams().get(0);

            resolution.put("Width", stream.width);
            resolution.put("Height", stream.height);

        } catch(IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }

        return resolution;
    }

}
