package server.commands;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import common.data.Worker;
import common.functional.User;
import server.utils.CollectionControl;
import server.utils.DatabaseCollectionManager;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SendNewList extends AbstractCommand{
    private CollectionControl collectionControl;
    public SendNewList(CollectionControl collectionControl) {
        super("sendNewList", "взять коллекцию");
        this.collectionControl = collectionControl;
    }
    public boolean execute(String argument, Object objectArgument, User user){
        return true;
    }

    public ArrayList<Worker> execute2(){
        ArrayList<Worker> collection = collectionControl.getCollection();
        try {
            String answer = writeToString(collection);
        }catch (Exception e){
            e.printStackTrace();
        }
            System.out.println(collection);
        return collection;
    }
    public String writeToString(ArrayList<Worker> workers) {
        StringWriter stringWriter = new StringWriter();
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("workers");
            for (Worker worker : workers) {
                writer.writeStartElement("worker");
                writer.writeStartElement("name");
                writer.writeCharacters(worker.getName());
                writer.writeEndElement();
                writer.writeStartElement("coordinates");
                writer.writeStartElement("x");
                writer.writeCharacters(String.valueOf(worker.getCoordinates().getX()));
                writer.writeEndElement();
                writer.writeStartElement("y");
                writer.writeCharacters(String.valueOf(worker.getCoordinates().getY()));
                writer.writeEndElement();
                writer.writeEndElement();
                writer.writeStartElement("salary");
                writer.writeCharacters(String.valueOf(worker.getSalary()));
                writer.writeEndElement();
                writer.writeStartElement("position");
                writer.writeCharacters(String.valueOf(worker.getPosition()));
                writer.writeEndElement();
                writer.writeStartElement("status");
                writer.writeCharacters(String.valueOf(worker.getStatus()));
                writer.writeEndElement();
                writer.writeStartElement("person");
                writer.writeStartElement("birthday");
                writer.writeCharacters(String.valueOf(worker.getPerson().getBirthday()).substring(0, String.valueOf(worker.getPerson().getBirthday()).length() - 6));
                writer.writeEndElement();
                writer.writeStartElement("height");
                writer.writeCharacters(String.valueOf(worker.getPerson().getHeight()));
                writer.writeEndElement();
                writer.writeStartElement("passportID");
                writer.writeCharacters(worker.getPerson().getPassportID());
                writer.writeEndElement();
                writer.writeStartElement("location");
                writer.writeStartElement("x");
                writer.writeCharacters(String.valueOf(worker.getPerson().getLocation().getX()));
                writer.writeEndElement();
                writer.writeStartElement("y");
                writer.writeCharacters(String.valueOf(worker.getPerson().getLocation().getY()));
                writer.writeEndElement();
                writer.writeStartElement("z");
                writer.writeCharacters(String.valueOf(worker.getPerson().getLocation().getZ()));
                writer.writeEndElement();
                writer.writeStartElement("name");
                writer.writeCharacters(worker.getPerson().getLocation().getName());
                writer.writeEndElement();
                writer.writeEndElement();
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.err.println("неверные данные записи");
        }

        return stringWriter.toString();
    }
}
