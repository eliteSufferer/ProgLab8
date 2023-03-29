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
import java.sql.SQLOutput;
import java.util.Scanner;

public class RunClient {
    private static String host = "192.168.10.80";
    private static int port = 23332;

    private static File file;



    private static boolean initializeConnectionAddress(String[] args) {
        try {
            if (args.length != 3) System.out.println("hello");
            file = new File(args[0]);

            if (port < 0) throw new Exception();
            return true;
        } catch (Exception exception) {
            Printer.println("Передайте название файла в качетчве аргументов");
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
                datagramChannel.send(buffer, address);
                System.out.println("Отправлено! Можете вводить команды");
                datagramChannel.close();
            }
            client.run();
            userScanner.close();
        } catch (Exception e){
            System.out.println("Возникла ошибка");
        }
    }

}