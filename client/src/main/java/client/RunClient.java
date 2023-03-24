package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RunClient {
    public static void main(String[] args) throws IOException {
        byte[] arr = {0,1,2,3,4,5,6,7,8,9};
        int len = arr.length;
        DatagramSocket ds; DatagramPacket dp;
        InetAddress host; int port;
        ds = new DatagramSocket();

        host = InetAddress.getLocalHost();
        port = 6789;


        dp = new DatagramPacket(arr,len,host,port);
        ds.send(dp);


        dp = new DatagramPacket(arr,len);
        ds.receive(dp);
        for (byte j : arr) {
            System.out.println(j);
        }

    }
}