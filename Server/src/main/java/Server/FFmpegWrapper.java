package Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

    private void initiateFFprobe(String path) throws IOException {
        ffprobe = new FFprobe(path);
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

    public static void convertFile(VideoFile file, videoType type, videoResolution resolution, Path outputFile) {

        // ffmpeg -i Falcon\ Heavy\ \&\ Starman-1080p.mp4 -vf scale=-1:720 -c:a copy output1.avi
        final int videoResolution = Integer.parseInt(resolution.toString().substring(1, resolution.toString().length() - 1));
        final double originalVideoAspectRatio = file.getWidth() / (double)file.getHeight() ;

        int outputVideoWidth = (int)Math.floor(videoResolution * originalVideoAspectRatio);

        if(outputVideoWidth % 2 == 1) {
            outputVideoWidth++;
        }

        try {
            new FFmpegWrapper().initiateFFmpeg("/usr/bin/ffmpeg");
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

}
