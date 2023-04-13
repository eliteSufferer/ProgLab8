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
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private int port;
    private DatagramSocket datagramSocket;
    private CommandControl commandManager;
    private boolean isStopped;
    private int maxClients;
    private ExecutorService fixedThreadPool;
    private Semaphore semaphore;

    public Server(int port, int maxClients, CommandControl commandManager) {
        this.port = port;
        this.commandManager = commandManager;
        this.maxClients = maxClients;
        this.semaphore = new Semaphore(maxClients);
        this.fixedThreadPool = Executors.newFixedThreadPool(maxClients);
    }

    public void run() {
        try {
            openDatagramSocket();
            byte[] buffer = new byte[4096];
            while (!isStopped()) {
                try {
                    //acquireConnection();
                    System.out.println("после акьюр");
                    if (isStopped()) {
                        System.out.println("is Stopped");
                        throw new UniversalException();

                    }
                    DatagramPacket packet = receiveFromClient(buffer);
                    System.out.println("получили пакет от пользователя");
                    fixedThreadPool.execute(new RequestHandler(this, datagramSocket, packet, commandManager));
                    System.out.println("ahahaha");
                } catch (UniversalException exception) {
                    if (!isStopped()) {
                        RunServer.logger.error("Произошла ошибка при соединении с клиентом!");
                        //exception.printStackTrace();
                    } else break;
                }
            }
            fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (UniversalException exception) {
            RunServer.logger.fatal("Сервер не может быть запущен!");
        } catch (InterruptedException e) {
            RunServer.logger.error("Произошла ошибка при завершении работы с уже подключенными клиентами!");
        }
    }
//
//    public void acquireConnection() {
//        try {
//            semaphore.acquire();
//            RunServer.logger.info("Разрешение на новое соединение получено.");
//        } catch (InterruptedException exception) {
//            RunServer.logger.error("Произошла ошибка при получении разрешения на новое соединение!");
//        }
//    }

    public void releaseConnection() {
        semaphore.release();
        RunServer.logger.info("Разрыв соединения зарегистрирован.");
    }

    public synchronized void stop() {
        try {
            RunServer.logger.info("Завершение работы сервера...");
            if (datagramSocket == null) throw new UniversalException();
            isStopped = true;
            fixedThreadPool.shutdown();
            datagramSocket.close();
            RunServer.logger.info("Работа сервера завершена.");
        } catch (UniversalException exception) {
            RunServer.logger.error("Невозможно завершить работу еще не запущенного сервера!");
        }
    }
    private synchronized boolean isStopped() {
        return isStopped;
    }

    private void openDatagramSocket() throws UniversalException{
        try {
            RunServer.logger.info("Запуск сервера...");
            datagramSocket = new DatagramSocket(port);
            RunServer.logger.info("Сервер запущен.");
        } catch (SocketException exception) {
            RunServer.logger.fatal("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new UniversalException();
        }
    }

    private DatagramPacket receiveFromClient(byte[] buffer) throws UniversalException {
        try {
            RunServer.logger.info("Прослушивание порта '" + port + "'...");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);
            RunServer.logger.info("Соединение с клиентом установлено.");
            return packet;
        } catch (IOException exception) {
            //exception.printStackTrace();
            System.exit(0);
            throw new UniversalException();

        }
    }
}