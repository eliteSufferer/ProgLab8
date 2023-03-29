package server;

import common.data.Worker;
import common.functional.Request;
import server.utils.*;
import server.commands.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunServer {
    private static final Logger logger = LogManager.getLogger(RunServer.class);
//    public static boolean connectionClosed = false;
//    private static int port = 23332;
    public static void main(String[] args) {
        while (true) {
            try {
                ResponseOutputer.appendln("main запущен");
                DatagramSocket datagramSocket = new DatagramSocket(23332);
                byte[] BUFFER = new byte[4096];
                Request userRequest = null;
                InetAddress localHost = InetAddress.getLocalHost();
                //System.out.println("IP-адрес сервера: " + localHost.getHostAddress());
                logger.info("Сервер запущен на порту {}", localHost.getHostAddress());
                DatagramPacket receivedPacket = new DatagramPacket(BUFFER, BUFFER.length);
                logger.info("Ожидание пакета...");
                datagramSocket.receive(receivedPacket);
                logger.info("Пакет получен");
                byte[] receivedData = receivedPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(receivedData);
                ObjectInputStream ois = new ObjectInputStream(in);
                userRequest = (Request) ois.readObject();
                datagramSocket.close();


                File file = (File) userRequest.getCommandObjectArgument();
                FileControl fileControl = new FileControl(file);
                CollectionControl collectionControl = new CollectionControl(fileControl);
                CommandControl commandControl = new CommandControl(new AddElement(collectionControl),
                        new AddElementIfMin(collectionControl),
                        new Clear(collectionControl),
                        new ExecuteScript(collectionControl), /*new Exit(),*/ new FilterGreaterStatus(collectionControl),
                        new GroupByStatus(collectionControl), new Help(collectionControl), new Info(collectionControl), new PrintFieldOfPerson(collectionControl),
                        new RemoveElementByID(collectionControl), new RemoveGreater(collectionControl), new SaveCollection(fileControl, collectionControl),
                        new Show(collectionControl), new Sort(collectionControl), new UpdateByID(collectionControl), collectionControl);
                RequestHandler requestHandler = new RequestHandler(commandControl);
                List<Worker> setWorkers;
                setWorkers = fileControl.readXmlFile();
                System.out.println(setWorkers.size());
                if (setWorkers != null) {
                    for (Worker worker : setWorkers) {
                        collectionControl.addToCollection(worker);
                    }
                }

                Server server = new Server(23332, requestHandler);
                server.connection();
//                if (RunServer.connectionClosed){
//                    RunServer.connectionClosed = false;
//                    port += 1;
//                    continue;
//                }
            } catch (IOException e) {
                System.out.println("Ошибка при работе с сокетом");
                logger.error("Ошибка при работе с сокетом");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("не получается сериализовать объект");
                logger.error("не удалось сериализовать объект");
                //e.printStackTrace();
            } catch (Exception e) {
                System.out.println("ошибка");
                logger.error("Произошла неожиданная ошибка");
                //e.printStackTrace();
            }

        }
    }
}
