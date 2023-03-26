package server.commands;

import common.exceptions.*;
import server.utils.*;

/**
 * The RemoveElementByID class represents a command to remove an element from the collection by its ID.
 */
public class RemoveElementByID extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructs the RemoveElementByID object with the specified CollectionControl and CommunicationControl objects.
     *
     * @param collectionControl    the CollectionControl object to be used
     */
    public RemoveElementByID(CollectionControl collectionControl) {
        super("remove_element_by_id", "Remove an element from the collection by its ID");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the RemoveElementByID command by invoking the removeElementByID() method on the CollectionControl object.
     *
     * @param argument the argument to be passed to the command
     */
    @Override
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            int id = Integer.parseInt(argument.trim());
            collectionControl.removeElementByID(id);
            collectionControl.updateAllIDs();
            System.out.println("Successfully removed");
        } catch (NumberFormatException e) {
            System.out.println("Incorrect ID format");
        } catch (WrongArgumentsException e) {
            System.out.println(e.getMessage());
        }

    }
}
