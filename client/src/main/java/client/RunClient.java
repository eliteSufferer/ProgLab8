package client;

import client.utils.UserHandler;
import common.functional.Printer;
import common.functional.Request;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class RunClient {
    private static String host = "localhost";
    private static int port = 23332;

    private static File file;



    private static boolean initializeConnectionAddress(String[] args) {
        try {
            if (args.length != 3) System.out.println("hello");
            file = new File("test.xml");

            if (port < 0) throw new Exception();
            return true;
        } catch (Exception exception) {
            Printer.println("Передайте хост, порт и название файла в качетчве аргументов");
        }
        return false;

    }

    public static void main(String[] args) {
        try {
            if (!initializeConnectionAddress(args)) return;
            Scanner userScanner = new Scanner(System.in);
            UserHandler userHandler = new UserHandler(userScanner);
            Client client = new Client(host, port, userHandler);
            DatagramChannel datagramChannel = DatagramChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            datagramChannel.connect(address);
            System.out.println("Соединение выполнено, хост = " + host);
            Request initialRequest = new Request(file);
            byte[] bytes = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(initialRequest);
                bytes = bos.toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                buffer.put(bytes);
                buffer.flip();
                System.out.println("Соединение выполнено, хост  повторно = " + host);
                datagramChannel.send(buffer, address);
                System.out.println("Отправлено!");
                datagramChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.run();
            userScanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}