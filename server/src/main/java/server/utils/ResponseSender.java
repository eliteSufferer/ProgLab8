package server.utils;

import common.functional.Response;
import server.RunServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ResponseSender implements Runnable {
    private final Response response;
    private final DatagramPacket packet;

    public ResponseSender(Response response, DatagramPacket packet) {
        this.response = response;
        this.packet = packet;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket();
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(response);
            oos.flush();
            byte[] sendData = bos.toByteArray();
            System.out.println("sala5");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
            RunServer.logger.info("Пакет был отправлен клиенту!");
        } catch (IOException e) {
            RunServer.logger.error("Неожиданная ошибка!");
        }
    }
}


