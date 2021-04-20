package Server;

import java.util.ArrayList;

public class App{

	private static ArrayList<VideoFile> files;
    public static final String folderName = "videos";

	public static void main(String[] args) {

		FilesHandler handler = new FilesHandler();
		files = handler.getFiles();

	}



}
