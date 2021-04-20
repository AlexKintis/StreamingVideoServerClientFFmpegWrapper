package Server;

import org.apache.logging.log4j.Logger;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class VideoHandler {

    private final String folderName = "videos";
    private HashMap<String, Path> videoFiles = new HashMap<String, Path>();

    VideoHandler() {
        App.log(App.LogLevel.INFO, "Sequence Starting");
        checkVideoFolder();
    }

    private void checkVideoFolder() {

        try {
            Path folder = Paths.get(folderName).normalize().toAbsolutePath();

            if(!Files.exists(folder)) {
                Files.createDirectory(folder);
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(folder);

            for(Path path : stream) {
                if(!Files.isDirectory(path)) {
                    videoFiles.put(path.getFileName().toString(), path);
                }
            }

        } catch(IOException ioex) {
            App.log(App.LogLevel.ERROR, ioex.getMessage());
        }
    }

    // Video files getter
    public HashMap<String, Path> getVideoFiles() {
        return this.videoFiles;
    }

}
