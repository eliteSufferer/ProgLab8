package client;

import client.utils.UserHandler;
import common.functional.Printer;
import common.functional.Request;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class RunClient {
    private static String host;
    private static int port;

    private static File file;

    private static boolean initializeConnectionAddress(String[] args) {
        try {
            if (args.length != 3) throw new Exception();
            host = args[0];
            port = Integer.parseInt(args[1]);
            file = new File(args[2]);

            if (port < 0) throw new Exception();
            return true;
        } catch (Exception exception) {
            Printer.println("Передайте хост, порт и название файла в качетчве аргументов");
        }
        return false;
    }

    public static void main(String[] args) {
        if (!initializeConnectionAddress(args)) return;
        Scanner userScanner = new Scanner(System.in);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, userHandler);
        new Request(file);
        client.run();
        userScanner.close();
    }
}