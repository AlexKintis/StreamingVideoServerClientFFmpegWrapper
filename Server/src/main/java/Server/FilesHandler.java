package Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.apache.commons.io.FilenameUtils;

public class FilesHandler {

    private ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();
    private HashMap<FFmpegWrapper.videoType, ArrayList<FFmpegWrapper.videoResolution>> allPossibleResolutionsForEachVideoExtension;

    FilesHandler() {
        App.videosFolder = Paths.get(App.folderName).normalize().toAbsolutePath();
        try{
            checkIfFolderVideoFolderExists(App.videosFolder);
            storeVideoFileNames(App.videosFolder);
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
                this.videoFiles.add(new VideoFile(path.getFileName().toString(), path, FilenameUtils.getExtension(path.toString()), dimentions.get("Height"), dimentions.get("Width")));
            }
        }
        Collections.reverse(this.videoFiles);
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

        var tempHmap = this.allPossibleResolutionsForEachVideoExtension;

        FFmpegWrapper.videoType origExtension = files.get(0).getExtension();
        FFmpegWrapper.videoResolution origResolution = files.get(0).getResolution();

        // Preparing the hashmap to host the formats and resolutions that the video
        // have to be converted
        for(var key : tempHmap.keySet()) {
            var i = tempHmap.get(key);
            i.removeAll(i.subList(i.indexOf(origResolution) + 1, i.size()));
        }
        tempHmap.get(origExtension).remove(origResolution);

        /*
        tempHmap.keySet().forEach((key) -> {
            System.out.format("%s %s\n", key, tempHmap.get(key).toString());
        });
        */

        // Remove unnecessary resolutions
        for(VideoFile file : files) {

            //FFmpegWrapper.convertFile(file, FFmpegWrapper.videoType.mp4, FFmpegWrapper.videoResolution._480p);
            //FFmpegWrapper.convertFile(file, FFmpegWrapper.videoType.mp4, FFmpegWrapper.videoResolution._240p);

            //System.out.println(FFmpegWrapper.videoType.mp4.toString());

            /*
            int count = 0;
            for(var key : tempHmap.keySet()) {
                System.out.print(key + " ");
                for(var resolution : tempHmap.get(key)) {
                    System.out.print(resolution + " ");
                    //FFmpegWrapper.convertFile(file, key, resolution);
                }
                System.out.println();
            }
            System.out.println(count);
            */


        }
    }

    public ArrayList<VideoFile> getFiles() {
        return this.videoFiles;
    }
}
