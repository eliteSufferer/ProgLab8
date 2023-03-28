package client;

import client.utils.UserHandler;
import common.functional.Printer;
import common.functional.Request;
import common.functional.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class Client {
    private String host;
    private int port;

    private UserHandler userHandler;
    private DatagramChannel datagramChannel = DatagramChannel.open();



    public Client(String host, int port, UserHandler userHandler) throws IOException {
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
                ByteArrayOutputStream serverWriter = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverWriter);
                objectOutputStream.writeObject(requestToServer);
                byte[] bytes;
                bytes = serverWriter.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                buffer.put(bytes);
                buffer.flip();
                InetSocketAddress address = new InetSocketAddress(host, port);
                datagramChannel.send(buffer, address);
                if (requestToServer.getCommandName().equals("exit")){
                    System.exit(0);
                }
                ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);

                datagramChannel.receive(receiveBuffer);
                receiveBuffer.flip();
                byte[] data = new byte[receiveBuffer.limit()];
                receiveBuffer.get(data);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object deserializedObject = objectInputStream.readObject();
                serverResponse = (Response) deserializedObject;
                Printer.print(serverResponse.getResponseBody(), serverResponse.getResponseCode());
                if (requestToServer.getCommandName().equals("exit")) {
                    // Создаем и отправляем новый Request с командой "close"
                    Request closeRequest = new Request("close", null, null);
                    ByteArrayOutputStream closeRequestWriter = new ByteArrayOutputStream();
                    ObjectOutputStream closeRequestObjectOutputStream = new ObjectOutputStream(closeRequestWriter);
                    closeRequestObjectOutputStream.writeObject(closeRequest);
                    byte[] closeRequestBytes = closeRequestWriter.toByteArray();
                    ByteBuffer closeRequestBuffer = ByteBuffer.allocate(4096);
                    closeRequestBuffer.put(closeRequestBytes);
                    closeRequestBuffer.flip();
                    datagramChannel.send(closeRequestBuffer, new InetSocketAddress(host, port));
                    // Завершаем работу программы
                    System.exit(0);
                }
            } catch (NullPointerException e){
                System.out.println("Недопустимый ввод");
                assert serverResponse != null;
                requestToServer = userHandler.handle(serverResponse.getResponseCode());
            }
            catch (ClassNotFoundException e) {
                System.out.println("Ошибка при чтении пакета");
            } catch (IOException e) {
                System.out.println("Непредвиденная ошибка при отправке данных");
            }
        } while (!requestToServer.getCommandName().equals("exit"));

        return false;
    }
    public void run() {
        try {
            boolean processingStatus = true;
            while (processingStatus) {
                try {
                    processingStatus = processRequestToServer();
                } catch (Exception exception) {
                    System.out.println("Фатальная ошибка при работе клиента");
                }
                if (datagramChannel != null) datagramChannel.close();
                Printer.println("Работа клиента завершена.");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Возникла ошибочка!");
        }

    }
}