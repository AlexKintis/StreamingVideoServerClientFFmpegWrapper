package Server;

import java.util.ArrayList;

public class App{

	protected static ArrayList<VideoFile> videoFiles;
    public static final String folderName = "videos";
	public static java.nio.file.Path videosFolder;

	public static void main(String[] args) {
		new App().startProcess();
	}

	private void startProcess() {
        AppLogger.log(AppLogger.LogLevel.INFO, "Sequence Starting");

		FilesHandler handler = new FilesHandler();
		videoFiles = handler.getFiles();

		handler.startVideosConversionProcess(videoFiles);

		Server server = new Server();
		server.start();

	}

	protected void showAllVideoFiles() {
		videoFiles.forEach(x -> System.out.println(x.toString()));
	}

	public ArrayList<VideoFile> getVideoFiles() {
		return videoFiles;
	}

}
