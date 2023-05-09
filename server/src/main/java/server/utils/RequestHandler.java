package server.utils;
import common.exceptions.WrongArgumentsException;
import common.functional.Request;
import common.functional.Response;
import common.functional.ServerResponseCode;
import common.functional.User;
import org.apache.logging.log4j.core.util.JsonUtils;
import server.RunServer;
import server.Server;
import server.commands.Command;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class RequestHandler implements Runnable {
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private InetAddress clientAddress;
    private int clientPort;
    private CommandControl commandControl;
    private ExecutorService fixedThreadPool1 = Executors.newFixedThreadPool(10);
    private ExecutorService fixedThreadPool2 = Executors.newFixedThreadPool(10);
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public RequestHandler(DatagramSocket serverSocket, DatagramPacket receivePacket, InetAddress clientAddress, int clientPort, CommandControl commandControl) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.commandControl = commandControl;
    }

    /**
     * Main handling cycle.
     */
    @Override
    public void run() {
        try {
            // Чтение данных из пакета
            byte[] receiveData = receivePacket.getData();
            ByteArrayInputStream bis = new ByteArrayInputStream(receiveData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Request request = (Request) ois.readObject();



            // Обработка запроса в отдельном потоке
            Future<Response> futureResponse = fixedThreadPool1.submit(new HandleRequestTask(request, commandControl));

            // Добавление задачи отправки ответа в очередь
            queue.put(() -> {
                try {
                    // Получение ответа и отправка клиенту
                    new ResponseSender(futureResponse.get(), receivePacket).run();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            for (int i = 0; i < 4; i++) {
                fixedThreadPool2.submit(() -> {
                    while (true) {
                        try {
                            Runnable responseSender = queue.take();
                            responseSender.run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            RunServer.logger.error("Ошибка RequestHandler");
        } finally {
            // Завершение работы пулов потоков
            fixedThreadPool1.shutdown();
//            fixedThreadPool2.shutdown();
            try {
                // Дождаться завершения всех задач
                fixedThreadPool1.awaitTermination(10, TimeUnit.SECONDS);
//                fixedThreadPool2.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                RunServer.logger.error("Ошибка c закрытием потоков");
            }
            // Закрытие сокета
        }
    }
}

