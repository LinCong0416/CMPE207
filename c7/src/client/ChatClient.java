package client;

import server.ChatServer;
import java.util.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChatClient {
    private  static final String DEAFAULT_SERVER_HOST = "127.0.0.1";
    private  static final int DEAFAULT_SERVER_PORT = 8888;
    private  static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private String host;
    private int port;
    private SocketChannel client;
    private ByteBuffer rBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer wBuffer = ByteBuffer.allocate(1024);
    private Selector selector;
    private Charset charset = Charset.forName("UTF-8");

    private ChatClient() {
        this(DEAFAULT_SERVER_HOST,DEAFAULT_SERVER_PORT);
    }

    private ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        try {
            client = SocketChannel.open();
            client.configureBlocking(false);

            selector = Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
            client.connect(new InetSocketAddress(host,port));

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key:selectionKeys) {
                    handles(key);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handles(SelectionKey key) throws IOException {
        //CONNECT
        if (key.isConnectable()) {
            SocketChannel client = (SocketChannel) key.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
                // client input
                new Thread(new UserInputHandler(this)).start();
            }
            client.register(selector, SelectionKey.OP_READ);
        }
        //READ
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            String msg = receive(client);
            if (msg.isEmpty()) {
                //server err
                close(selector);
            } else {
                System.out.println(msg);
            }
        }
    }

    public void send (String msg) throws IOException {
        if (msg.isEmpty()) {
            return;
        }
        wBuffer.clear();
        wBuffer.put(charset.encode(msg));
        wBuffer.flip();
        while(wBuffer.hasRemaining()) {
            client.write(wBuffer);
        }
        //QUIT
        if (readyToQuit(msg)) {
            close(selector);
        }

    }

    private String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        while (client.read(rBuffer) > 0);
        rBuffer.flip();
        return String.valueOf(charset.decode(rBuffer));
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("127.0.0.1",7777);
        client.start();
    }

    private class UserInputHandler implements Runnable {
        public UserInputHandler(ChatClient chatClient) {
        }
    }
}
