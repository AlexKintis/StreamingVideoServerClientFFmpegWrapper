package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends App {

    private ServerSocket server;
    private final int PORT = 9999;

    public void start() {

        AppLogger.log(AppLogger.LogLevel.INFO, "Server is starting");

        try {

            server = new ServerSocket(PORT);

            while (true) {
                AppLogger.log(AppLogger.LogLevel.INFO, "Waiting for client request");

                Socket socket = server.accept();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new  ObjectOutputStream(socket.getOutputStream());


            }

        } catch (IOException ioex) {

            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);

        } finally {

            try {

                // Server Close
                if(server != null)
                    server.close();

                // Socket Close

            } catch ( IOException ioex ) {

                AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());

            }

        }


    }
}
