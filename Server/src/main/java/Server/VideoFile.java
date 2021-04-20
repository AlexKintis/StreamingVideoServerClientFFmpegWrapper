package Server;

import java.nio.file.Path;

public class VideoFile {

    private String name;
    private Path path;
    private String extension;
    private int height;
    private int widht;
    private FFmpegWrapper.videoResolution resolution;

    VideoFile(String name, Path path, String extension, int height, int width) {
        this.name = name;
        this.path = path;
        this.extension = extension;
        this.height = height;
        this.widht = width;
        this.resolution = initiateResolution();
    }

    private FFmpegWrapper.videoResolution initiateResolution() {

        FFmpegWrapper.videoResolution video_resolution = null;

        switch(this.height) {
            case 1080:
                video_resolution = FFmpegWrapper.videoResolution._1080p;
                break;
            case 720:
                video_resolution = FFmpegWrapper.videoResolution._720p;
                break;
            case 480:
                video_resolution = FFmpegWrapper.videoResolution._480p;
                break;
            case 360:
                video_resolution = FFmpegWrapper.videoResolution._360p;
                break;
            case 240:
                video_resolution = FFmpegWrapper.videoResolution._240p;
                break;
            default:
                AppLogger.log(AppLogger.LogLevel.ERROR, "Unparsable Video Resolution");
                System.exit(1);
        }

        return video_resolution;
    }

    public String getName() {
        return this.name;
    }

    public Path getPath() {
        return this.path;
    }

    public String getExtension() {
        return this.extension;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.widht;
    }

    public FFmpegWrapper.videoResolution getResolution() {
        return this.resolution;
    }
}
