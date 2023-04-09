
package server.utils;

import common.data.*;
import common.exceptions.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileControl class provides methods to read and write files related to worker information
 */
public class FileControl {
    private final File file;

    /**
     * Constructor for FileControl
     *
     * @param file an array of strings representing the file path
     */

    public FileControl(File file) {
        this.file = file;
        CollectionControl.timeInitialization = LocalDateTime.now();
    }

    /**
     * Writes a list of workers to an XML file specified by the given path. The XML file will contain the name, coordinates,
     * salary, position, status, and personal information (birthday, height, passportID, and location) of each worker.
     *
     * @param workers the list of workers to be written to the fil
     * @throws IOException        if an I/O error occurs while writing the file
     * @throws XMLStreamException if an error occurs while writing the XML document
     */
    public void writeToFile(ArrayList<Worker> workers) throws IOException, XMLStreamException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(file), "UTF-8");
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
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            ResponseOutputer.appendln("сюда сохранить невозможно!");
        } catch (Exception e){
            ResponseOutputer.appendln("неверные данные записи");
        }
    }

    /**
     * Reads worker information from an XML file and returns a list of workers
     *
     * @return a List of workers read from the XML file
     */
    public List<Worker> readXmlFile() {
        ParserXml parserXml = new ParserXml(file);
        return parserXml.parseWorkersFromXML();
    }


}
