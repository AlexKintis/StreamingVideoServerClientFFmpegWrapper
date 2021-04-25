package Server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.ListIterator;

public class User extends Server{

    private ArrayList<VideoFile> files = new ArrayList<>();
    protected BigDecimal userDownloadRateBits;
    protected double userDownloadRatekbps;
    protected FFmpegWrapper.videoResolution highestResolution;

    User(BigDecimal userDownload) {

        this.userDownloadRateBits = userDownload;
        userDownloadRatekbps = userDownloadRateBits.doubleValue() * 0.001;

        calculateUserVideoFiles();
    }

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
                files.add(file);
            }
        }

    }

    public ArrayList<VideoFile> getFiles() {
        return this.files;
    }

    public ArrayList<String> getDinstictiveFileNames() {

        ArrayList<String> filenames = new ArrayList<>();
        files.forEach(file -> filenames.add(file.getName().split("\\-")[0]));

        HashSet<String> hashSet = new HashSet<>(filenames);

        return new ArrayList<String>(hashSet);
    }
}
