package Server;

import java.nio.file.Path;

public class VideoFile {

    private String name;
    private Path path;

    VideoFile(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public Path getPath() {
        return this.path;
    }
}
