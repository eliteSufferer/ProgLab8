package server.utils;

import common.data.Worker;
import common.functional.Response;
import common.functional.ServerResponseCode;
import server.RunServer;
import server.commands.SendNewList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;

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
                if (response.getResponseCode() == ServerResponseCode.PEAK_SIZE) {
                    ArrayList<ArrayList<Worker>> ara = (ArrayList<ArrayList<Worker>>) response.getResponseObject();
                    Response count = new Response(ara.size(), ServerResponseCode.PEAK_SIZE);
                    oos.writeObject(count);
                    oos.flush();
                    byte[] sendDataC = bos.toByteArray();
                    System.out.println(sendDataC.length);
                    DatagramPacket sendPacketC = new DatagramPacket(sendDataC, sendDataC.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacketC);
                    RunServer.logger.info("Пакет был отправлен клиенту!");

                    for (int i = 0; i < ara.size(); i++) {
                        try (ByteArrayOutputStream tempBos = new ByteArrayOutputStream();
                             ObjectOutputStream tempOos = new ObjectOutputStream(tempBos)) {
                            Response temp = new Response(ara.get(i), ServerResponseCode.PEAK_SIZE);
                            tempOos.writeObject(temp);
                            tempOos.flush();
                            byte[] sendData = tempBos.toByteArray();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            socket.send(sendPacket);

                            RunServer.logger.info("Пакет был отправлен клиенту!");
                        } catch (IOException e) {
                            RunServer.logger.error("Ошибка при отправке пакета клиенту: ", e);
                        }
                    }
                }else {
                    oos.writeObject(response);
                    oos.flush();
                    byte[] sendData = bos.toByteArray();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    RunServer.logger.info("Пакет был отправлен клиенту!");
                }
            } catch (IOException e) {
                RunServer.logger.error("Неожиданная ошибка!");
                e.printStackTrace();
            }
    }
}


