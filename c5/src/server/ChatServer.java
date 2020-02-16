package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private int DEAFAULT_PORT = 8888;
    private final String QUIT = "quit";

    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private Map<Integer, Writer> connectedClients;

    public ChatServer() {
        executorService = Executors.newFixedThreadPool(10);
        connectedClients = new HashMap<>();
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    //synchronized 保证同一时间只有一个线程向map中添加元素
    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            connectedClients.put(port,writer);
            System.out.println("Client[" + port + "]has connected");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.get(port).close();
            }
            connectedClients.remove(port);
            System.out.println("Client[" + port + "]has disconnected");
        }
    }

    public synchronized void forwardMessage(Socket socket, String fwdMsg) throws IOException {
        for (Integer id : connectedClients.keySet()) {
            if (!id.equals(socket.getPort())) {
                Writer writer = connectedClients.get(id);
                writer.write(fwdMsg);
                writer.flush();
            }
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(DEAFAULT_PORT);
            System.out.println("Start the server and start listen:" + DEAFAULT_PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                //new Thread(new ChatHandler(this,socket)).start();
                executorService.execute(new ChatHandler(this,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           close();
        }
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println("Server closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
