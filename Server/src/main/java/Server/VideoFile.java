package Server;

import java.nio.file.Path;

public class VideoFile {

    private String name;
    private Path path;
    private int height;
    private int width;
    private FFmpegWrapper.videoType extension;
    private FFmpegWrapper.videoResolution resolution;

    VideoFile(String name, Path path, String extension, int height, int width) {
        this.name = name.split("\\.")[0];
        this.path = path;
        this.height = height;
        this.width = width;
        this.extension = initiateExtension(extension);
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

    private FFmpegWrapper.videoType initiateExtension(String extension) {

        FFmpegWrapper.videoType video_type = null;

        switch(extension) {
            case "avi":
                video_type = FFmpegWrapper.videoType.avi;
                break;
            case "mp4":
                video_type = FFmpegWrapper.videoType.mp4;
                break;
            case "mkv":
                video_type = FFmpegWrapper.videoType.mkv;
                break;
        }

        return video_type;
    }

    public String getName() {
        return this.name;
    }

    public Path getPath() {
        return this.path;
    }

    public FFmpegWrapper.videoType getExtension() {
        return this.extension;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public FFmpegWrapper.videoResolution getResolution() {
        return this.resolution;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %d %s", this.name, this.extension, this.height, this.width, this.resolution);
    }

}
