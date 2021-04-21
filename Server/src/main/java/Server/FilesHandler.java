package Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.io.FilenameUtils;

public class FilesHandler {

    private ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();
    private HashMap<FFmpegWrapper.videoType, ArrayList<FFmpegWrapper.videoResolution>> allPossibleResolutionsForEachVideoExtension;

    FilesHandler() {
        Path folder = Paths.get(App.folderName).normalize().toAbsolutePath();
        try{
            checkIfFolderVideoFolderExists(folder);
            storeVideoFileNames(folder);
        } catch (IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }
        initiateAllPossibleResolutionsForEachVideoExtension();
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
                videoFiles.add(new VideoFile(path.getFileName().toString(), path, FilenameUtils.getExtension(path.toString()), dimentions.get("Height"), dimentions.get("Width")));
            }
        }
    }

    private void initiateAllPossibleResolutionsForEachVideoExtension() {

        this.allPossibleResolutionsForEachVideoExtension = new HashMap<>();

        for(FFmpegWrapper.videoType type : FFmpegWrapper.videoType.values()) {
            ArrayList<FFmpegWrapper.videoResolution> res = new ArrayList<>(Arrays.asList(FFmpegWrapper.videoResolution.values()));
            this.allPossibleResolutionsForEachVideoExtension.put(type, res);
        }
    }

    public void startVideosConversionProcess(ArrayList<VideoFile> files) {

        AppLogger.log(AppLogger.LogLevel.INFO, "Starting Conversion Process");

        for(VideoFile file : files) {
            FFmpegWrapper.videoType origExtension = file.getExtension();
            FFmpegWrapper.videoResolution origResolution = file.getResolution();
        }
    }

    public ArrayList<VideoFile> getFiles() {
        return this.videoFiles;
    }
}
