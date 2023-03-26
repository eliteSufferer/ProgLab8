
package server.commands;

import common.data.*;
import common.exceptions.*;
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


    public AddElementIfMin(CollectionControl collectionControl) {
        super("add_if_min", "Добавить новым элемент в коллекцю, если меньше" +
                "минимального в коллекции");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the command to add a new worker element to the collection if its salary is less than the salary
     * of all the elements in the collection.
     *
     * @param argument the arguments passed to the command, not used in this case.
     */
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (!argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;
            Worker newWorker = new Worker(workerPacket.getName(),
                    workerPacket.getCoordinates(),
                    workerPacket.getSalary(), workerPacket.getPosition(),
                    workerPacket.getStatus(), workerPacket.getPerson());
            if (!collectionControl.addIfSmallerSalary(newWorker)) newWorker = null;
        } catch (WrongArgumentsException e) {
            System.out.println("Превышенно кол-во аргементов");
        }
    }
}
