
package server.commands;

import common.data.*;
import common.exceptions.*;
import common.functional.User;
import common.functional.WorkerPacket;
import server.utils.*;
/**
 * The {@code AddElementIfMin} class represents a command that adds a new worker element to the collection if its salary
 * is less than the salary of all the elements in the collection.
 * The command requires the communicationControl and collectionControl instances to communicate with the user
 * and modify the collection.
 * Extends {@code AbstractCommand} abstract class.
 */
public class AddElementIfMin extends AbstractCommand {

    CollectionControl collectionControl;
    DatabaseCollectionManager databaseCollectionManager;


    public AddElementIfMin(CollectionControl collectionControl, DatabaseCollectionManager databaseCollectionManager) {
        super("add_if_min", "Добавить новым элемент в коллекцю, если меньше" +
                "минимального в коллекции");
        this.collectionControl = collectionControl;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command to add a new worker element to the collection if its salary is less than the salary
     * of all the elements in the collection.
     *
     * @param argument the arguments passed to the command, not used in this case.
     */
    public void execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;
            Worker newWorker = databaseCollectionManager.insertWorker(workerPacket, user);
            if (!collectionControl.addIfSmallerSalary(newWorker)) newWorker = null;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("Превышенно кол-во аргементов");
        } catch (DatabaseHandlingException e) {
            throw new RuntimeException(e);
        }
    }
}
