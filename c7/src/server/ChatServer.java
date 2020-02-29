package server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

public class ChatServer {

    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER);
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public ChatServer() {
        this(DEFAULT_PORT);
    }
    public ChatServer(int port) {
        this.port = port;
    }

    private void start() {
        try{
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started, listen:" + port + "...");

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key: selectionKeys) {
                    handles(key);
                }
                selectionKeys.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(selector);
        }
    }

    private void handles(SelectionKey key) throws IOException {
        //ACCEPT--server socket channel
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);
            System.out.println(getClientName(client) + "]connected.");
        }
        //READ--client socket channel
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel)key.channel();
            String fwdMsg = receive(client);
            if (fwdMsg.isEmpty()) {
                key.cancel();
                selector.wakeup();
            } else {
                forwardMessage(client,fwdMsg);
                if(readyToQuit(fwdMsg)) {
                    key.cancel();
                    selector.wakeup();
                    System.out.println(getClientName(client) + "]disconnected.");
                }
            }
        }
    }

    private void forwardMessage(SocketChannel client, String fwdMsg) throws IOException {
        for (SelectionKey key : selector.keys()) {
            Channel connectedClient = key.channel();
            if (connectedClient instanceof ServerSocketChannel) {
                continue;
            }
            if (key.isValid() && !client.equals(connectedClient)) {
                wBuffer.clear();
                wBuffer.put(charset.encode(getClientName(client) + ":" + fwdMsg));
                wBuffer.flip();
                while (wBuffer.hasRemaining()) {
                    ((SocketChannel)connectedClient).write(wBuffer);
                }
            }
        }
    }

    private String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        while (client.read(rBuffer) > 0);
        rBuffer.flip();
        return String.valueOf(charset.decode(rBuffer));
    }

    private String getClientName(SocketChannel client) {
        return "Client[" + client.socket().getPort();
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

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(7777);
        chatServer.start();
    }
}
