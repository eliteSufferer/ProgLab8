package client;


import client.utils.ScriptControl;
import common.exceptions.UserAlreadyExists;
import common.exceptions.UserIsNotFoundException;
import common.functional.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class Client{
    private String host;
    private int port;

    private DatagramChannel datagramChannel = DatagramChannel.open();
    private User user;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        datagramChannel.configureBlocking(false);
    }

    public void sendRequest(Request requestToServer) throws IOException {
        ByteArrayOutputStream serverWriter = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverWriter);
        objectOutputStream.writeObject(requestToServer);
        byte[] bytes;
        bytes = serverWriter.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(100000);
        buffer.put(bytes);
        buffer.flip();
        InetSocketAddress address = new InetSocketAddress(host, port);
        datagramChannel.send(buffer, address);
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException, InterruptedException {
        ByteBuffer receiveBuffer = ByteBuffer.allocate(100000);

        long timeout = 5000;
        long start = System.currentTimeMillis();
        while (datagramChannel.receive(receiveBuffer) == null && System.currentTimeMillis() - start < timeout) {
            Thread.sleep(100);
        }

        if (System.currentTimeMillis() - start >= timeout) {
            System.out.println("Превышено время ожидания ответа от сервера");
            return null;
        }

        receiveBuffer.flip();
        byte[] data = new byte[receiveBuffer.limit()];
        receiveBuffer.get(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object deserializedObject = objectInputStream.readObject();
        return (Response) deserializedObject;
    }


    public boolean processScriptToServer(File scriptFile) {
        Request requestToServer = null;
        Response serverResponse = null;
        ScriptControl scriptControl = new ScriptControl(scriptFile);
        do {
            try {
                requestToServer = serverResponse != null ? scriptControl.handle(serverResponse.getResponseCode(), user) :
                        scriptControl.handle(null, user);
                if (requestToServer == null) return false;
                if (requestToServer.isEmpty()) continue;
                ByteBuffer buffer = ByteBuffer.allocate(1000000);
                DatagramChannel datagramChannel;
                InetSocketAddress serverAddress = new InetSocketAddress(host, port);
                try {
                    datagramChannel = DatagramChannel.open();
                    datagramChannel.configureBlocking(true);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при создании DatagramChannel", e);
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(requestToServer);
                byte[] dataToSend = baos.toByteArray();

                buffer.clear();
                buffer.put(dataToSend);
                buffer.flip();
                datagramChannel.send(buffer, serverAddress);

                buffer.clear();
                datagramChannel.receive(buffer);
                buffer.flip();

                byte[] responseData = new byte[buffer.limit()];
                buffer.get(responseData);

                ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                serverResponse = (Response) ois.readObject();
            } catch (InvalidClassException | NotSerializableException exception) {
                JOptionPane.showMessageDialog(null, "DataSendingException");
            } catch (ClassNotFoundException exception) {
                JOptionPane.showMessageDialog(null, "DataReadingException");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(null, "EndConnectionToServerException");
            }
        } while (!requestToServer.getCommandName().equals("exit"));
        return true;
    }
    public boolean processAuthentication(boolean registered, String username, String password){
        Request requestToServer = null;
        Response serverResponse = null;
        String command;

        ByteBuffer buffer = ByteBuffer.allocate(1000000);
        DatagramChannel datagramChannel;
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);

        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(true);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании DatagramChannel", e);
        }
        try {
            command = registered ? "login" : "register";
            User user = new User(username, password);
            System.out.println(user.getUsername());
            requestToServer = new Request(command, "", user);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(requestToServer);
            byte[] dataToSend = baos.toByteArray();

            buffer.clear();
            buffer.put(dataToSend);
            buffer.flip();
            datagramChannel.send(buffer, serverAddress);

            buffer.clear();
            datagramChannel.receive(buffer);
            buffer.flip();

            byte[] responseData = new byte[buffer.limit()];
            buffer.get(responseData);

            ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            serverResponse = (Response) ois.readObject();

            Printer.print(serverResponse.getResponseBody(), serverResponse.getResponseCode());

        } catch (InvalidClassException | NotSerializableException exception) {
            Printer.printerror("Произошла ошибка при отправке данных на сервер!");
        } catch (ClassNotFoundException exception) {
            Printer.printerror("Произошла ошибка при чтении полученных данных!");
        } catch (IOException exception) {
            Printer.printerror("Соединение с сервером разорвано!");
        }
        if (serverResponse != null && (serverResponse.getResponseCode().equals(ServerResponseCode.OK) || serverResponse.getResponseCode().equals(ServerResponseCode.PEAK_SIZE))){
            user = requestToServer.getUser();
            System.out.println(user);
            return true;
        }
        return false;
    }
    public User getCurrentUser(){
        return user;
    }
}




