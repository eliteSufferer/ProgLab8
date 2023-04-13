package server.utils;
import common.exceptions.WrongArgumentsException;
import common.functional.Request;
import common.functional.Response;
import common.functional.ServerResponseCode;
import common.functional.User;
import server.RunServer;
import server.Server;
import server.commands.Command;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
        Request userRequest = null;
        Response responseToUser = null;
        boolean stopFlag = false;

        try {
            byte[] receiveData = new byte[1024];
            AtomicReference<byte[]> sendData = new AtomicReference<>();
            InetAddress clientAddress;
            int clientPort;

            do {
                // Receiving data from client
                clientAddress = receivePacket.getAddress();
                clientPort = receivePacket.getPort();
                ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                userRequest = (Request) ois.readObject();
                System.out.println(userRequest.getCommandName());

                System.out.println(userRequest.getCommandObjectArgument());
                System.out.println(userRequest.getCommandStringArgument());
                System.out.println(userRequest.getUser());

                ois.close();
                bais.close();

                // Processing request
                Callable<Response> handleRequestTask = new HandleRequestTask(userRequest, commandManager);
                responseToUser = fixedThreadPool.submit(handleRequestTask).get();
                RunServer.logger.info("Запрос '" + userRequest.getCommandName() + "' обработан.");

                // Sending response to client
                Response finalResponseToUser = responseToUser;
                System.out.println(finalResponseToUser.getResponseBody());
                System.out.println(finalResponseToUser.getResponseCode());
                InetAddress finalClientAddress = clientAddress;
                int finalClientPort = clientPort;
                Callable<Boolean> sendResponseTask = () -> {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(finalResponseToUser);
                        oos.flush();
                        sendData.set(baos.toByteArray());
                        DatagramPacket sendPacket = new DatagramPacket(sendData.get(), sendData.get().length, finalClientAddress, finalClientPort);
                        clientSocket.send(sendPacket);
                        oos.close();
                        baos.close();
                        return true;
                    } catch (IOException exception) {
                        RunServer.logger.error("Произошла ошибка при отправке данных на клиент!");
                    }
                    return false;
                };
                if (!fixedThreadPool.submit(sendResponseTask).get()) break;
                
            } while (responseToUser.getResponseCode() != ServerResponseCode.SERVER_EXIT &&
                    responseToUser.getResponseCode() != ServerResponseCode.CLIENT_EXIT);

            if (responseToUser.getResponseCode() == ServerResponseCode.SERVER_EXIT)
                stopFlag = true;

        } catch (ClassNotFoundException exception) {
            RunServer.logger.error("Произошла ошибка при чтении полученных данных!");
        } catch (CancellationException | ExecutionException | InterruptedException exception) {
            RunServer.logger.warn("При обработке запроса произошла ошибка многопоточности!");
        } catch (IOException exception) {
            RunServer.logger.warn("Непредвиденный разрыв соединения с клиентом!");
            exception.printStackTrace();
        } finally {
            fixedThreadPool.shutdown();
            clientSocket.close();
            RunServer.logger.info("Клиент отключен от сервера.");
            if (stopFlag) server.stop();
            server.releaseConnection();
        }
    }
}

