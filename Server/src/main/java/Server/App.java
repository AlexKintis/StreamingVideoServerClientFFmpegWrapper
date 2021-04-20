package Server;

import java.util.ArrayList;

public class App{

	private ArrayList<VideoFile> files;
    public static final String folderName = "videos";

	public static void main(String[] args) {
		new App().startProcess();
	}

	private void startProcess() {
        AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		FilesHandler handler = new FilesHandler();
		files = handler.getFiles();

		for(VideoFile file : files) {
			System.out.format("%s %s %d %d %s",file.getName(), file.getExtension(), file.getHeight(), file.getWidth(), file.getResolution());
		}
	}

}
