package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;

public class SocketClient extends App {

    private Socket socket = null;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    SocketClient() {
        AppLogger.log(AppLogger.LogLevel.INFO, "Client is starting");
    }

    public void connectToServer(BigDecimal downloadRate) {

        try {
            AppLogger.log(AppLogger.LogLevel.INFO, String.format("Client is trying to connect to %s:%d", super.SERVER_IP, super.SERVER_PORT));

            socket = new Socket(super.SERVER_IP, super.SERVER_PORT);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(downloadRate);

            oos.close();
            ois.close();
            socket.close();

        } catch(Exception ioex) {
            AppLogger.log(AppLogger.LogLevel.ERROR, ioex.getMessage());
            System.exit(1);
        } finally {

        }
    }

}
