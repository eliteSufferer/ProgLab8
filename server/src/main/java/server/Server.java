package server;

import server.commands.SaveCollection;
import server.utils.CollectionControl;
import server.utils.FileControl;
import server.utils.RequestHandler;
import common.functional.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private int port;
    private final BufferedReader serverReader = new BufferedReader(new InputStreamReader(System.in));
    private final byte[] BUFFER = new byte[4096];
    private Selector selector;
    private DatagramChannel datagramChannel;
    private RequestHandler requestHandler;
    private InetAddress host;
    private FileControl fileControl;
    private CollectionControl collectionControl;
    private static final Logger logger = LogManager.getLogger(RunServer.class);

    public Server(int port, RequestHandler requestHandler, FileControl fileControl, CollectionControl collectionControl) throws IOException {
        this.port = port;
        this.collectionControl = collectionControl;
        this.requestHandler = requestHandler;
        this.fileControl = fileControl;
        this.selector = Selector.open();
        this.datagramChannel = DatagramChannel.open();
        this.datagramChannel.configureBlocking(false);
        this.datagramChannel.bind(new InetSocketAddress(this.port));
        this.datagramChannel.register(selector, SelectionKey.OP_READ);
    }


    public void connection() {
        while (true) {
            try {
                if (selector.selectNow() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            handleClientRequest(key);
                        }
                    }
                }

                if (System.in.available() > 0) {
                    String line = serverReader.readLine();
                    if (line.trim().equals("save")) {
                        SaveCollection save = new SaveCollection(fileControl, collectionControl);
                        System.out.println("Saving the collection...");
                        save.execute("", null);
                    }
                } else {
                    Thread.sleep(100); // Delay between checking server input availability
                }
            } catch (IOException e) {
                System.out.println("Error during I/O operations: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted: " + e.getMessage());
            }
        }
    }

    private void handleClientRequest(SelectionKey key) {
        DatagramChannel clientChannel = (DatagramChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(4096);

        try {
            // Receive data from client
            SocketAddress clientAddress = clientChannel.receive(buffer);
            buffer.flip();

            // Deserialize the request
            ByteArrayInputStream in = new ByteArrayInputStream(buffer.array());
            ObjectInputStream ois = new ObjectInputStream(in);
            Request userRequest = (Request) ois.readObject();
            logger.info("Запрос получен");

            // Handle the request and get the response
            Response response = requestHandler.handle(userRequest);

            // Serialize the response
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(response);
            byte[] responseData = bos.toByteArray();

            // Send the response back to the client
            buffer.clear();
            buffer.put(responseData);
            buffer.flip();
            clientChannel.send(buffer, clientAddress);
            logger.info("Ответ отправлен");

        } catch (IOException e) {
            System.out.println("Ошибка с I/O потоками");
        } catch (ClassNotFoundException e) {
            System.out.println("Объект не может быть сериализован");
        }
    }
}