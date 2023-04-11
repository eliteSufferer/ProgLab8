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
    private CommandControl commandControl;
    private boolean isStopped;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private Semaphore semaphore;

    public Server(int port, int maxClients, CommandControl commandControl) {
        this.port = port;
        this.semaphore = new Semaphore(maxClients);
        this.commandControl = commandControl;
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }

    public void run() {
        try {
            openServerSocket();
            while (!isStopped()) {
                try {
                    acquireConnection();
                    if (isStopped()) throw new UniversalException();
                    fixedThreadPool.submit(() -> {
                        try {
                            DatagramPacket packet = receiveFromClient();
                            RequestHandler requestHandler = new RequestHandler(this, packet, commandControl);
                            requestHandler.handleConnection();
                        } catch (IOException e) {
                            System.out.println("Ошибка при обработке соединения с клиентом: " + e.getMessage());
                        }
                    });
                } catch (UniversalException e) {
                    if (!isStopped()) {
                        RunServer.logger.error("Произошла ошибка при соединении с клиентом!");
                    } else break;
                }
            }
            fixedThreadPool.shutdown();
            fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Printer.println("Работа сервера завершена.");
        } catch (UniversalException | InterruptedException exception) {
            RunServer.logger.fatal("Сервер не может быть запущен!");
        }
    }


    public void acquireConnection() {
        try {
            semaphore.acquire();
            RunServer.logger.info("Разрешение на новое соединение получено.");
        } catch (InterruptedException exception) {
            RunServer.logger.error("Произошла ошибка при получении разрешения на новое соединение!");
        }
    }


    public void releaseConnection() {
        semaphore.release();
        RunServer.logger.info("Разрыв соединения зарегистрирован.");
    }


    public synchronized void stop() {
        try {
            RunServer.logger.info("Завершение работы сервера...");
            if (datagramSocket == null) throw new UniversalException();
            isStopped = true;
            datagramSocket.close();
            RunServer.logger.info("Работа сервера завершена.");
        } catch (UniversalException exception) {
            RunServer.logger.error("Невозможно завершить работу еще не запущенного сервера!");
        }
    }


    private void openServerSocket() throws UniversalException {
        try {
            RunServer.logger.info("Запуск сервера...");
            datagramSocket = new DatagramSocket(port);
            RunServer.logger.info("Сервер запущен.");
        } catch (IllegalArgumentException exception) {
            RunServer.logger.fatal("Порт '" + port + "' находится за пределами возможных значений!");
            throw new UniversalException();
        } catch (IOException exception) {
            RunServer.logger.fatal("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new UniversalException();
        }
    }

    private DatagramPacket receiveFromClient() throws IOException {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);
        RunServer.logger.info("Получен запрос от клиента.");
        return packet;
    }
}