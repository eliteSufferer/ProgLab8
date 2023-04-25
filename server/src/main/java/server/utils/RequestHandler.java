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
    private Server server;
    private DatagramSocket clientSocket;
    private CommandControl commandManager;

    private DatagramPacket receivePacket;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    public RequestHandler(Server server, DatagramSocket clientSocket, DatagramPacket packet, CommandControl commandManager) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.receivePacket = packet;
        this.commandManager = commandManager;
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

            // Получение ответа и отправка клиенту в отдельном потоке
            fixedThreadPool2.submit(new ResponseSender(futureResponse.get(), receivePacket));
        } catch (IOException | ClassNotFoundException | InterruptedException | ExecutionException e) {
            RunServer.logger.error("Ошибка RequestHandler");
        } finally {
            // Завершение работы пулов потоков
            fixedThreadPool1.shutdown();
            fixedThreadPool2.shutdown();
            try {
                // Дождаться завершения всех задач
                fixedThreadPool1.awaitTermination(10, TimeUnit.SECONDS);
                fixedThreadPool2.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                RunServer.logger.error("Ошибка c закрытием потоков");
            }
            // Закрытие сокета
        }
    }
}

