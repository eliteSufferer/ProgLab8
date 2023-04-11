
package server.commands;

import common.data.Worker;
import common.functional.User;
import common.functional.WorkerPacket;
import server.utils.CollectionControl;
import common.exceptions.*;
import server.utils.DatabaseCollectionManager;
import server.utils.ResponseOutputer;

/**
 * The {@code AddElement} class represents a command that adds a new worker element to the collection.
 * and modify the collection, respectively.
 * This class extends the {@link AbstractCommand} abstract class.
 */
public class AddElement extends AbstractCommand {


    private CollectionControl collectionControl;
    private DatabaseCollectionManager databaseCollectionManager;

    /**
     * @param collectionControl the {@link CollectionControl} instance to be used for modifying the collection
     */
    public AddElement(CollectionControl collectionControl, DatabaseCollectionManager databaseCollectionManager) {
        super("addElement", "Добавить элемент в коллекцию");
        this.collectionControl = collectionControl;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command by adding a new worker element to the collection.
     *
     * @param argument the command argument
     */
    public void execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;

            collectionControl.addToCollection(databaseCollectionManager.insertWorker(workerPacket, user));
            ResponseOutputer.appendln("Рабочий успешно добавлен!");
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
            e.printStackTrace();
        } catch (DatabaseHandlingException e) {
            ResponseOutputer.appendln(e.getMessage());
        }
    }

}
