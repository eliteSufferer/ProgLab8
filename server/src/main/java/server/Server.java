package server;

import common.exceptions.UniversalException;
import server.utils.CollectionControl;
import server.utils.CommandControl;
import server.utils.RequestHandler;
import common.functional.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private int port;
    private CommandControl commandManager;
    private int maxClients;
    private ExecutorService fixedThreadPool;
    private Semaphore semaphore;
    private DatagramSocket serverSocket;

    public Server(int port, int maxClients, CommandControl commandManager) {
        this.port = port;
        this.commandManager = commandManager;
        this.maxClients = maxClients;
        this.semaphore = new Semaphore(maxClients);
        this.fixedThreadPool = Executors.newFixedThreadPool(maxClients);
    }

    public void run() {
        try {
            serverSocket = new DatagramSocket(port);
            RunServer.logger.info("Server started on port " + port);
            while (true){
                byte[] receiveData = new byte[50000];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                RunServer.logger.info("Ожидание пакета от клиента...");
                serverSocket.receive(receivePacket);

                RunServer.logger.info("Пакет был принят от клиента...");
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                semaphore.acquire();
                RequestHandler requestHandler = new RequestHandler(serverSocket, receivePacket, clientAddress, clientPort, commandManager);
                fixedThreadPool.submit(requestHandler);
            }

        }catch (Exception e ){
            RunServer.logger.error("Ошибка");
        } finally {
            fixedThreadPool.shutdown();
            try {
                fixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                RunServer.logger.error("InterruptedException");
            }
        }
    }


}