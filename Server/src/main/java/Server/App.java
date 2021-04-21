package Server;

import java.util.ArrayList;
import java.util.Arrays;

public class App{

	private ArrayList<VideoFile> videoFiles;
    public static final String folderName = "videos";

	public static void main(String[] args) {
		new App().startProcess();
	}

	private void startProcess() {
        AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		FilesHandler handler = new FilesHandler();
		videoFiles = handler.getFiles();

		handler.startVideosConversionProcess(this.videoFiles);

		for(VideoFile file : videoFiles) {
			System.out.format("%s %s %d %d %s\n",file.getName(), file.getExtension(), file.getHeight(), file.getWidth(), file.getResolution());
		}
	}

}
