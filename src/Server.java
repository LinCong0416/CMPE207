import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;


        try {
            //bind listen port
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server started, start listen" + DEFAULT_PORT);

            while (true) {
                // wait for the client to connect
                Socket socket = serverSocket.accept();
                System.out.println("Client Port [" + socket.getPort()+"]connected");
                BufferedReader reader= new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );
                // read the msg from client
                String msg = reader.readLine();
                if (msg != null) {
                    System.out.println("Client Port [" + socket.getPort()+"]:" + msg);
                    //response
                    writer.write("Server" + msg + "\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //finally is executed with or without exceptions
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("close serversocket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
