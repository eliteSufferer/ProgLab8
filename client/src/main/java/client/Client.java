package client;

import client.utils.UserHandler;
import common.functional.Printer;
import common.functional.Request;
import common.functional.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public class Client {
    private String host;
    private int port;

    private UserHandler userHandler;
    private DatagramChannel datagramChannel;
    private ByteArrayInputStream serverReader;
    private ByteArrayOutputStream serverWriter;

    public Client(String host, int port, UserHandler userHandler) {
        this.host = host;
        this.port = port;
        this.userHandler = userHandler;
    }


    private boolean processRequestToServer() {
        Request requestToServer = null;
        Response serverResponse = null;
        do {
            try {
                requestToServer = serverResponse != null ? userHandler.handle(serverResponse.getResponseCode()) :
                        userHandler.handle(null);
                if (requestToServer.isEmpty()) continue;
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverWriter);
                objectOutputStream.writeObject(requestToServer);
                objectOutputStream.flush();
                ObjectInputStream objectInputStream = new ObjectInputStream(serverReader);
                serverResponse = (Response) objectInputStream.readObject();
                Printer.print(serverResponse.getResponseBody());
            } catch (InvalidClassException | NotSerializableException exception) {
                Printer.printerror("Произошла ошибка при отправке данных на сервер!");
            } catch (ClassNotFoundException exception) {
                Printer.printerror("Произошла ошибка при чтении полученных данных!");
            } catch (IOException exception) {
                Printer.printerror("Соединение с сервером разорвано!");
                try {
                    connectToServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
        return false;
    }

    private void connectToServer() {
        try {
            datagramChannel = DatagramChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            datagramChannel.connect(address);
            Printer.println("Соединение с сервером успешно установлено.");
            Printer.println("Ожидание разрешения на обмен данными...");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = datagramChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[bytesRead];
                buffer.get(data);
                serverWriter = new ByteArrayOutputStream();
                serverWriter.write(data);
                serverReader = new ByteArrayInputStream(data);
            }
            Printer.println("Разрешение на обмен данными получено.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            boolean processingStatus = true;
            while (processingStatus) {
                try {
                    connectToServer();
                    processingStatus = processRequestToServer();
                } catch (Exception exception) {
                    Printer.printerror("Ошибка подключения к серверу");
                }
                if (datagramChannel != null) datagramChannel.close();
                Printer.println("Работа клиента успешно завершена.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
