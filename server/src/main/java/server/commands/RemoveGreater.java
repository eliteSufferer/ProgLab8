
package server.commands;

import common.data.*;
import common.exceptions.*;
import common.functional.WorkerPacket;
import server.utils.*;


/**
 * The RemoveGreater class represents a command to remove all elements from the collection
 * that are greater than the specified element.
 */
public class RemoveGreater extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructs a new RemoveGreater command with the specified CollectionControl and
     * CommunicationControl.
     *
     * @param collectionControl    The CollectionControl instance to use for command execution.
     */
    public RemoveGreater(CollectionControl collectionControl) {
        super("remove_greater", "Удалить из коллекции все элементы, превышающие заданный");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the RemoveGreater command.
     *
     * @param argument The command argument (not used).
     */

    @Override
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (!argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;
            collectionControl.removeGreater(new Worker(workerPacket.getName(),
                    workerPacket.getCoordinates(), workerPacket.getSalary(),
                    workerPacket.getPosition(), workerPacket.getStatus(),
                    workerPacket.getPerson()));
            collectionControl.updateAllIDs();
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }

    }
}
