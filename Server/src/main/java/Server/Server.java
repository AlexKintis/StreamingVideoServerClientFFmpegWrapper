package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends App {

    private ServerSocket server = null;
    private Socket socket = null;
    private final int PORT = 9999;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;


    public void start() {

        AppLogger.log(AppLogger.LogLevel.INFO, "Server is starting");

        try {

            server = new ServerSocket(PORT);

            AppLogger.log(AppLogger.LogLevel.INFO, "Waiting for client request");

            while (true) {

                socket = server.accept();

                if(socket.isBound()) {
                    AppLogger.log(AppLogger.LogLevel.INFO, "Client Connected");

                    ois = new ObjectInputStream(socket.getInputStream());
                    oos = new  ObjectOutputStream(socket.getOutputStream());

                    System.out.println((String)ois.readObject());
                    oos.writeObject("Hey From server");

                    ois.close();
                    oos.close();
                    socket.close();
                }

            }

        } catch (IOException | ClassNotFoundException ioex) {

            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);

        } finally {

            try {
                // Server Close
                if(server != null) server.close();

                // Socket Close
                if(socket != null) socket.close();

                // Object Input Sream Close
                if(ois != null) ois.close();

                // Object Output Sream Close
                if(oos != null) oos.close();


            } catch ( IOException ioex ) {
                AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            }

        }

    }
}
