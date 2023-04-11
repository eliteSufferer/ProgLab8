
package server.commands;

import common.data.*;
import common.exceptions.*;
import common.functional.User;
import common.functional.WorkerPacket;
import server.RunServer;
import server.utils.*;


/**
 * The RemoveGreater class represents a command to remove all elements from the collection
 * that are greater than the specified element.
 */
public class RemoveGreater extends AbstractCommand {
    CollectionControl collectionControl;
    DatabaseCollectionManager databaseCollectionManager;

    /**
     * Constructs a new RemoveGreater command with the specified CollectionControl and
     * CommunicationControl.
     *
     * @param collectionControl    The CollectionControl instance to use for command execution.
     */
    public RemoveGreater(CollectionControl collectionControl, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_greater", "Удалить из коллекции все элементы, превышающие заданный");
        this.collectionControl = collectionControl;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the RemoveGreater command.
     *
     * @param argument The command argument (not used).
     */

    @Override
    public void execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;
            collectionControl.removeGreater(databaseCollectionManager.insertWorker(workerPacket, user));
            collectionControl.updateAllIDs();
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        } catch (DatabaseHandlingException e) {
            RunServer.logger.error("DatabaseHandlingException ----");
        }

    }
}
