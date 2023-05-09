package server.utils;

import client.GUI.UserSettings;
import common.data.*;
import common.exceptions.*;
import common.functional.Printer;
import server.RunServer;
import server.commands.Command;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * The CollectionControl class manages a collection of Worker objects and provides methods for manipulating
 * and accessing the data. It implements various operations such as sorting, filtering, grouping and updating
 * the elements of the collection. It also interacts with the FileControl and CommunicationControl classes to
 * read/write data from/to files and receive input from the user, respectively. The class has a HashMap to store
 * the mapping of available commands, and a LocalDateTime variable to keep track of the time of initialization
 * of the collection. The class has public methods for adding, removing and updating elements of the collection,
 * as well as for displaying information about the collection.
 */
public class CollectionControl {
    private ArrayList<Worker> workersCollection = new ArrayList<>();
    HashMap<String, Command> BufferOfCommandMap;
    protected static LocalDateTime timeInitialization = null;
    private DatabaseCollectionManager databaseCollectionManager;


    public CollectionControl(DatabaseCollectionManager databaseCollectionManager){
        this.databaseCollectionManager = databaseCollectionManager;
        loadCollection();

    }

    public void getMappingOfCommands(HashMap<String, Command> map) {
        this.BufferOfCommandMap = map;
    }
    public ArrayList<Worker> getCollection(){
        return workersCollection;
    }


    /**
     * Returns the current BufferOfCommandMap HashMap.
     *
     * @return the current BufferOfCommandMap HashMap
     */
    public HashMap<String, Command> sendCommandMap() {
        return this.BufferOfCommandMap;
    }

    /**
     * Clears the workersCollection ArrayList.
     */

    public void clear(Worker worker) {
        workersCollection.remove(worker);
        ResponseOutputer.appendln("Коллекция очистилась...");

    }
    public int collectionSize(){
        return workersCollection.size();
    }
    public Worker getById(int id){
        try {
            for (Worker worker : workersCollection){
                RunServer.logger.info(worker.getId());
                if (worker.getId() == id){
                    return worker;
                }
            }
        }catch (IndexOutOfBoundsException e){
            RunServer.logger.info("ошибка с ID!!!!!");
        }
        return null;
    }
    public void removeFromCollection(Worker worker){
        workersCollection.remove(worker);
    }

    /**
     * Sorts the workersCollection ArrayList in ascending order based on the natural ordering of the elements.
     */
    public void sort() {
        Collections.sort(workersCollection);
    }



    /**
     * Removes all elements from the workersCollection ArrayList that are greater than the specified Worker object
     * based on the natural ordering of the elements.
     *
     * @param enotherWorker the Worker object to compare the elements to
     */
    public void removeGreater(Worker enotherWorker) {
        workersCollection.removeIf(worker -> enotherWorker.compareTo(worker) > 0);
    }

    /**
     * Saves the current workersCollection ArrayList to a file with the specified name.
     *
     */

    /**
     * Filters workers with a status greater than the given string and returns an ArrayList of the filtered workers.
     *
     * @param line the status to compare against.
     * @return an ArrayList of the filtered workers.
     * @throws IllegalArgumentException if the given string is not a valid status.
     */

    public List<Worker> filterGreaterThanStatus(String line) throws IllegalArgumentException {
        try {
            Status status = Status.valueOf(line.toUpperCase());
            return workersCollection.stream()
                    .filter(worker -> worker.getStatus().compareTo(status) > 0)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Groups the workers in the collection by their status and outputs the number of workers for each status.
     */
    public void gropByStatus() {

        Map<Status, List<Worker>> workersByStatus = workersCollection.stream().collect(Collectors.groupingBy(Worker::getStatus));
        for (Status status : workersByStatus.keySet()) {
            ResponseOutputer.appendln("Кол-во работников со статусом '" + status + "': " + workersByStatus.get(status).size());
        }
    }

    /**
     * Outputs information about the worker collection, including the type, time of initialization, and number of elements.
     */
    public void getInfo() {
        ResponseOutputer.appendln("Тип: Worker" + "\n" + "Время инициализации: " + timeInitialization + "\n" + "количество элементов: " + workersCollection.size());

    }

    /**
     * Sorts the workers in the collection by their person object and outputs the sorted list.
     */
    public void sortPerson() {
        workersCollection.stream()
                .map(Worker::getPerson)
                .sorted()
                .forEach(person -> ResponseOutputer.appendln(person.toString()));
    }


    /**
     * Removes the worker from the collection with the given ID.
     *
     * @param id the ID of the worker to remove.
     */
    public void removeElementByID(int id) {
        try {
            for (Worker worker : workersCollection){
                RunServer.logger.info(worker.getId());
                if (worker.getId() == id){
                    workersCollection.remove(workersCollection.indexOf(worker));
                    break;
                }
            }
        }catch (IndexOutOfBoundsException e){
            RunServer.logger.info("ошибка с ID!!!!!");
        }
    }

    /**
     * Outputs each worker in the collection using their toString method.
     */

    public void show() {
        for (Worker worker : workersCollection) {
            ResponseOutputer.appendln(worker.toString());
        }
    }

    /**
     * Adds the given worker to the collection.
     *
     * @param worker the worker to add to the collection.
     */
    public void addToCollection(Worker worker) {
        workersCollection.add(worker);
    }

    /**
     * Adds the given worker to the collection if their salary is smaller than the minimum salary in the collection.
     *
     * @param newWorker the worker to add to the collection.
     * @return true if the worker was added, false otherwise.
     */

    public boolean addIfSmallerSalary(Worker newWorker) {
        if (workersCollection.isEmpty() || newWorker.getSalary() < Collections.min(workersCollection, Comparator.comparing(Worker::getSalary)).getSalary()) {
            workersCollection.add(newWorker);
            return true;
        }
        return false;
    }

    /**
     * Updates the worker in the collection with the given ID.
     *
     * @param id the ID of the worker to update.
     */
    public void updateByID(int id, Worker worker) {
        try {
            if (id > workersCollection.size()) throw new InputException();
            workersCollection.set(id - 1, worker);

        } catch (InputException e) {
            ResponseOutputer.appendln("такого рабочего нет");
        }
    }

    private void loadCollection() {
        try {
            workersCollection = databaseCollectionManager.getCollection();
            timeInitialization = LocalDateTime.now();
            Printer.println("Коллекция загружена.");
            RunServer.logger.info("Коллекция загружена.");
        } catch (DatabaseHandlingException exception) {
            exception.printStackTrace();
            Printer.printerror("Коллекция не может быть загружена!");
            RunServer.logger.error("Коллекция не может быть загружена!");
        }
    }

}