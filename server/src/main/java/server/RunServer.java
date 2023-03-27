package server;

import common.functional.Request;
import server.utils.CollectionControl;
import server.utils.CommandControl;
import server.utils.FileControl;
import server.utils.RequestHandler;
import server.commands.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class RunServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(7777);
        byte[] BUFFER = new byte[1024];
        Request userRequest = null;

        try {
            DatagramPacket receivedPacket = new DatagramPacket(BUFFER, BUFFER.length);
            datagramSocket.receive(receivedPacket);
            byte[] receivedData = receivedPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(receivedData);
            ObjectInputStream ois = new ObjectInputStream(in);
            userRequest = (Request) ois.readObject();

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        File file = (File) userRequest.getCommandObjectArgument();








        FileControl fileControl = new FileControl(file);
        CollectionControl collectionControl = new CollectionControl(fileControl);
        CommandControl commandControl = new CommandControl(new AddElement(collectionControl),
                new AddElementIfMin(collectionControl),
                new Clear(collectionControl),
                new ExecuteScript(collectionControl), new Exit(), new FilterGreaterStatus(collectionControl),
                new GroupByStatus(collectionControl), new Help(collectionControl), new Info(collectionControl), new PrintFieldOfPerson(collectionControl),
                new RemoveElementByID(collectionControl), new RemoveGreater(collectionControl), new SaveCollection(fileControl, collectionControl),
                new Show(collectionControl), new Sort(collectionControl), new UpdateByID(collectionControl));
        RequestHandler requestHandler = new RequestHandler(commandControl);
        Server server = new Server(7777, requestHandler);
        server.connection();
    }
}

