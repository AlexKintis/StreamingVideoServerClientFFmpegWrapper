package Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * This Class is for handling video files conversion
 */
public class FilesHandler {

    private ArrayList<VideoFile> videoFiles = new ArrayList<VideoFile>();

    FilesHandler() {
        App.videosFolder = Paths.get(App.folderName).normalize().toAbsolutePath();
        try{
            checkIfFolderVideoFolderExists(App.videosFolder);
            storeVideoFileNames(App.videosFolder);
        } catch (IOException ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        }
    }

    // Check if videos folders exists
    private void checkIfFolderVideoFolderExists(Path folder) throws IOException {
        if(!Files.exists(folder)) {
                Files.createDirectory(folder);
        }
    }

    public void storeVideoFileNames(Path folder) throws IOException {

        for(Path path : Files.newDirectoryStream(folder)) {
            if(!Files.isDirectory(path)) {
                Map<String, Integer> dimentions = FFmpegWrapper.getFileResolution(path);
                this.videoFiles.add(new VideoFile(path.getFileName().toString(), path, FilenameUtils.getExtension(path.toString()), dimentions.get("Height"), dimentions.get("Width")));
            }
        }

        Collections.sort(this.videoFiles, new Comparator<VideoFile>(){
            public int compare(VideoFile o1, VideoFile o2){
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

    }

    /*
     * Start the conversion process
     */
    public void startVideosConversionProcess(ArrayList<VideoFile> files) {

        AppLogger.log(AppLogger.LogLevel.INFO, "Starting Conversion Process");

        int videoConvertedExists = 0;
        int allFilesToBeConverted = 0;
        for(VideoFile file : files) {

            ArrayList<FFmpegWrapper.videoResolution> resolutions =  new ArrayList<>(Arrays.asList(FFmpegWrapper.videoResolution.values()));

            FFmpegWrapper.videoResolution currentVideoResolution = file.getResolution();
            resolutions.subList(resolutions.indexOf(currentVideoResolution), resolutions.size()).clear();

            ListIterator<FFmpegWrapper.videoResolution> it;
            for(var videoType : FFmpegWrapper.videoType.values()) {

                if(file.getExtension() != videoType) {
                    resolutions.add(file.getResolution());
                }

                it = resolutions.listIterator(resolutions.size());
                allFilesToBeConverted += resolutions.size();

                int videoConvertedCounter = 1;

                while(it.hasPrevious()) {

                    String type = videoType.toString();

                    FFmpegWrapper.videoResolution cResolution = it.previous();
                    int videoResolution = Integer.parseInt(cResolution.toString().substring(1, cResolution.toString().length() - 1));
                    Path outputFile = Paths.get(App.videosFolder.toAbsolutePath().toString() + String.format("%s%s-%dp.%s", java.io.File.separator, file.getName().split("\\-")[0], videoResolution, type));

                    if(outputFile.toAbsolutePath().toFile().exists()) {
                        videoConvertedExists++;
                        continue;
                    } else {
                        AppLogger.log(AppLogger.LogLevel.INFO, String.format("Converting Video: %s [%s] %d/%d", file.getName().split("\\-")[0], type ,videoConvertedCounter++, resolutions.size()));
                        FFmpegWrapper.convertFile(file, FFmpegWrapper.videoType.mp4, cResolution, outputFile);
                        videoConvertedExists++;
                    }
                }

            }

        }

        // Display that all files are converted
        if(videoConvertedExists == allFilesToBeConverted) {
            AppLogger.log(AppLogger.LogLevel.INFO, "All files are fully converted");
            try {
                storeVideoFileNames(App.videosFolder);
            } catch( IOException ioex ) {
                AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            }
        }
    }

    // Getter for videoFiles
    public ArrayList<VideoFile> getFiles() {
        return this.videoFiles;
    }

}
