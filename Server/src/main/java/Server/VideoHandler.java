package Server;

import org.apache.logging.log4j.Logger;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class VideoHandler {

    private DirectoryStream<Path> videoFiles;
    private final String folderName = "videos";

    VideoHandler( ) {

        App.log(App.LogLevel.INFO, "Sequence Starting");

        try {
            Path videosFolder = checkFolder();
            this.videoFiles = Files.newDirectoryStream(videosFolder);
        } catch(IOException ioex) {
            App.log(App.LogLevel.ERROR, ioex.getMessage());
        }

    }

    private Path checkFolder() throws IOException {

        Path folder = Paths.get(folderName).normalize().toAbsolutePath();

        if(!Files.exists(folder)) {
            Files.createDirectory(folder);
        }

        return folder;
    }

    // Video files getter
    public DirectoryStream<Path> getVideoFiles() {
        return this.videoFiles;
    }

}
