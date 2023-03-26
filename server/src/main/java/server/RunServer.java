package server;

import server.utils.CollectionControl;
import server.utils.CommandControl;
import server.utils.FileControl;
import server.utils.RequestHandler;
import server.commands.*;

import java.io.IOException;
import java.net.*;

public class RunServer {
    public static void main(String[] args) throws IOException {
        String[] file = null;
        RequestHandler requestHandler = new RequestHandler();
        FileControl fileControl = new FileControl(file);
        CollectionControl collectionControl = new CollectionControl(fileControl);
        Server server = new Server(7777, requestHandler);
        CommandControl commandControl = new CommandControl(new AddElement(collectionControl),
                new AddElementIfMin(collectionControl),
                new Clear(collectionControl),
                new ExecuteScript(collectionControl), new Exit(), new FilterGreaterStatus(collectionControl),
                new GroupByStatus(collectionControl), new Help(collectionControl), new Info(collectionControl), new PrintFieldOfPerson(collectionControl),
                new RemoveElementByID(collectionControl), new RemoveGreater(collectionControl), new SaveCollection(fileControl, collectionControl),
                new Show(collectionControl), new Sort(collectionControl), new UpdateByID(collectionControl));

        server.connection();
    }
}

