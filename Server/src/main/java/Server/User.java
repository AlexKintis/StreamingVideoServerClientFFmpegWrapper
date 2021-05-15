package Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ListIterator;

/**
 * Class for each client definition
 * Describes which video files can user download based on download rate
 */
public class User extends Server{

    private ArrayList<VideoFile> downloadableFiles = new ArrayList<>();
    private VideoFile userSelectedFileForStream;
    protected BigDecimal userDownloadRateBits;
    protected double userDownloadRatekbps;
    protected FFmpegWrapper.videoResolution highestResolution;

    User(BigDecimal userDownload) {

        this.userDownloadRateBits = userDownload;
        userDownloadRatekbps = userDownloadRateBits.doubleValue() * 0.001;

        calculateUserVideoFiles();
    }

    /*
     * Calculate user video files that is able to see depending from download speed
     */
    public void calculateUserVideoFiles() {

        ListIterator<FFmpegWrapper.videoResolution> iterator =
            new ArrayList<>(this.speedEquivalentResolutions.keySet()).listIterator(this.speedEquivalentResolutions.keySet().size());

        while(iterator.hasPrevious()) {

            var key = iterator.previous();
            var innerHmap = this.speedEquivalentResolutions.get(key);

            //System.out.println(innerHmap.get("Minimum").getClass().getSimpleName());
            if(innerHmap.get("Minimum") < userDownloadRatekbps) {
                highestResolution = key;
                iterator = null;
                break;
            }
        }

        for(VideoFile file : App.getVideoFiles()) {

            var keys = new ArrayList<>(this.speedEquivalentResolutions.keySet());

            Collections.sort(keys, Collections.reverseOrder());
            keys.subList(0, keys.indexOf(highestResolution)).clear();

            if(keys.contains(file.getResolution())) {
                downloadableFiles.add(file);
            }
        }

        Collections.sort(downloadableFiles, new Comparator<VideoFile>() {
                @Override
                public int compare(VideoFile o1, VideoFile o2) {
                    return Integer.valueOf(o2.getHeight()).compareTo(Integer.valueOf(o1.getHeight()));
                }
            });

    }

    // Getter for downloadableFiles
    public ArrayList<VideoFile> getFiles() {
        return this.downloadableFiles;
    }

    // get user Selected video for streaming
    public VideoFile getSelectedVideo(String filename) {

        userSelectedFileForStream = null;

        for(VideoFile file : downloadableFiles) {
            if(String.format("%s.%s",file.getName(), file.getExtension().name()).equals(filename)) {
                userSelectedFileForStream = file;
            }
        }

        return userSelectedFileForStream;
    }

    /*
     * Get filenames only without resolution and extension
     * eg. filename-1080p.mp4 -> filename
     */
    public ArrayList<String> getDinstictiveFileNames() {

        ArrayList<String> filenames = new ArrayList<>();
        downloadableFiles.forEach(file -> filenames.add(file.getName().split("\\-")[0]));

        HashSet<String> hashSet = new HashSet<>(filenames);

        return new ArrayList<String>(hashSet);
    }
}
