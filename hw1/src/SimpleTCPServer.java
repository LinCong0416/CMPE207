import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleTCPServer {
    public static void main(String[] args) {

        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server started, start listen" + DEFAULT_PORT);

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("Client Port [" + socket.getPort()+"]connected");

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );
                writer.write("Hello World" + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("close serverSocket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
