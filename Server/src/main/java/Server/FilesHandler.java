package Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

public class FilesHandler {

    private ArrayList<VideoFile> files = new ArrayList<VideoFile>();

    FilesHandler() {
        Path folder = Paths.get(App.folderName).normalize().toAbsolutePath();
        try{
            checkIfFolderVideoFolderExists(folder);
            storeVideoFileNames(folder);
        } catch (IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }
    }

    private void checkIfFolderVideoFolderExists(Path folder) throws IOException {
        if(!Files.exists(folder)) {
                Files.createDirectory(folder);
        }
    }

    private void storeVideoFileNames(Path folder) throws IOException {
        for(Path path : Files.newDirectoryStream(folder)) {
            if(!Files.isDirectory(path)) {
                Map<String, Integer> dimentions = FFmpegWrapper.getFileResolution(path);
                files.add(new VideoFile(path.getFileName().toString(), path, FilenameUtils.getExtension(path.toString()), dimentions.get("Height"), dimentions.get("Width")));
            }
        }
    }

    public ArrayList<VideoFile> getFiles() {
        return this.files;
    }

}
