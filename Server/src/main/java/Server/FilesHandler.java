package Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

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

        Collections.sort(this.videoFiles, new Comparator<VideoFile>(){
            public int compare(VideoFile o1, VideoFile o2){
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

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

            ArrayList<FFmpegWrapper.videoResolution> resolutions =  new ArrayList<>(Arrays.asList(FFmpegWrapper.videoResolution.values()));

            FFmpegWrapper.videoResolution currentVideoResolution = file.getResolution();
            resolutions.subList(resolutions.indexOf(currentVideoResolution), resolutions.size()).clear();

            int counter = 0;

            AppLogger.log(AppLogger.LogLevel.INFO, String.format("[ Converting Video(%d/%d) : %s ]", files.indexOf(file), files.size() ,file.getName()));

            ListIterator<FFmpegWrapper.videoResolution> it;
            for(var videoType : FFmpegWrapper.videoType.values()) {

                it = resolutions.listIterator(resolutions.size());
                String type = videoType.toString();

                while(it.hasPrevious()) {

                    FFmpegWrapper.videoResolution cResolution = it.previous();
                    int videoResolution = Integer.parseInt(cResolution.toString().substring(1, cResolution.toString().length() - 1));
                    Path outputFile = Paths.get(App.videosFolder.toAbsolutePath().toString() + String.format("%s%s-%dp.%s", java.io.File.separator, file.getName().split("\\-")[0], videoResolution, type));

                    if(outputFile.toAbsolutePath().toFile().exists()) {
                        AppLogger.log(AppLogger.LogLevel.INFO, String.format("File already exists : %s.%s", file.getName(), file.getExtension()));
                        continue;
                    } else {
                        AppLogger.log(AppLogger.LogLevel.INFO, String.format("Video %d/%d", counter++, (resolutions.size() * 3) - 1));
                        //FFmpegWrapper.convertFile(file, FFmpegWrapper.videoType.mp4, FFmpegWrapper.videoResolution._480p, outputFile);
                    }
                }

            }

            //FFmpegWrapper.convertFile(file, FFmpegWrapper.videoType.mp4, FFmpegWrapper.videoResolution._240p);

        }
    }

    public ArrayList<VideoFile> getFiles() {
        return this.videoFiles;
    }
}
