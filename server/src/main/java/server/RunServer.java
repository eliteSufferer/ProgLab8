package server;

import server.utils.*;
import server.commands.*;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunServer {
    public static final Logger logger = LogManager.getLogger(RunServer.class);

    private static final int maxClients = 1000;

    private static String databaseUsername = "postgres";
    private static int port;

    public static void main(String[] args) {
        try {
            ResponseOutputer.appendln("main запущен");
            //String databaseHost = args[1];
            String databaseAddress = "jdbc:postgresql://localhost:5432/Lab7";
            String databasePassword = "228337";
            DatabaseHandler databaseHandler = new DatabaseHandler(databaseAddress, databaseUsername, databasePassword);
            DatabaseUser databaseUserManager = new DatabaseUser(databaseHandler);
            DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseHandler, databaseUserManager);
            CollectionControl collectionControl = new CollectionControl();
            CommandControl commandControl = new CommandControl(new AddElement(collectionControl, databaseCollectionManager),
                    new AddElementIfMin(collectionControl, databaseCollectionManager),
                    new Clear(collectionControl, databaseCollectionManager),
                    new ExecuteScript(collectionControl), new FilterGreaterStatus(collectionControl),
                    new GroupByStatus(collectionControl), new Help(collectionControl), new Info(collectionControl), new PrintFieldOfPerson(collectionControl),
                    new RemoveElementByID(collectionControl, databaseCollectionManager), new RemoveGreater(collectionControl, databaseCollectionManager),
                    new Show(collectionControl), new Sort(collectionControl), new UpdateByID(collectionControl, databaseCollectionManager), new LoginCommand(databaseUserManager), new RegisterCommand(databaseUserManager), collectionControl);

            Server server = new Server(Integer.parseInt(args[0]), maxClients, commandControl);
            server.run();
            databaseHandler.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Произошла неожиданная ошибка");
        }
    }


}
