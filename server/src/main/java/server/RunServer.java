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
        try {
            ResponseOutputer.appendln("main запущен");
            File file = new File(args[1]);
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

            Server server = new Server(Integer.parseInt(args[0]), requestHandler, fileControl, collectionControl);
            server.connection();
//                if (RunServer.connectionClosed){
//                    RunServer.connectionClosed = false;
//                    port += 1;
//                    continue;
//                }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Ошибка при работе с сокетом");
            //e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Произошла неожиданная ошибка");
            //e.printStackTrace();
        }

    }
}
