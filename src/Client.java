import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] arg) {

        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_SERVER_PORT = 8888;
        Socket socket = null;
        BufferedWriter writer = null;

        try {
            //build socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);

            //build IO
            BufferedReader reader= new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

            //wait for the user input info
            BufferedReader consoleReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String input = consoleReader.readLine();

            // send to server
            writer.write(input + "\n");
            writer.flush();

            //read the msg for server
            String msg = reader.readLine();
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    System.out.println("close socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
