package Server;

import java.util.ArrayList;
import java.net.InetAddress;

public class App{

	protected static InetAddress inetAddress;
	protected static final String hostName = "localhost";
	protected static final int SERVER_PORT = 9999;
	protected static final int SERVER_VIDEO_PORT = 1234;

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

		new Server().start();
	}

	protected void showAllVideoFiles() {
		videoFiles.forEach(x -> System.out.println(x.toString()));
	}

	public static ArrayList<VideoFile> getVideoFiles() {
		return videoFiles;
	}

}
