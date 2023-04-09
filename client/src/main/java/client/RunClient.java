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
    private static String host = "localhost";
    private static int port;





    public static void main(String[] args) {
        try {
            Scanner userScanner = new Scanner(System.in);
            UserHandler userHandler = new UserHandler(userScanner);
            Client client = new Client(host, Integer.parseInt(args[0]), userHandler);
            DatagramChannel datagramChannel = DatagramChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, Integer.parseInt(args[0]));
            datagramChannel.connect(address);
            System.out.println("Соединение выполнено, хост = " + host);
            client.run();
            userScanner.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Возникла ошибка");
        }
    }

}