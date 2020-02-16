import java.io.*;
import java.net.Socket;

public class SimpleTCPClient {
    public static void main(String[] arg) {

        final String DEFAULT_SERVER_HOST = "94.142.241.111";
        final int DEFAULT_SERVER_PORT = 23;
        Socket socket = null;

        try {
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);

            BufferedReader reader= new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            while (true) {
                String msg = reader.readLine();
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("close socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
