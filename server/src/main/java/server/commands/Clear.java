
package server.commands;

import common.data.*;
import common.exceptions.*;
import server.utils.*;
/**
 * The Clear class represents a command that clears the collection.
 */
public class Clear extends AbstractCommand {
    private CollectionControl collectionControl;

    /**
     * Constructs a Clear object with the given CollectionControl and CommunicationControl objects.
     *
     * @param collectionControl    the CollectionControl object that manages the collection
     */
    public Clear(CollectionControl collectionControl) {
        super("clear", "очистить коллекцию");
        this.collectionControl = collectionControl;


    }

    /**
     * Executes the command to clear the collection.
     *
     * @param argument the arguments for the command (not used in this command)
     */
    @Override
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.clear();

        } catch (WrongArgumentsException e) {
            System.out.println("Превышенно кол-во аргументов");
        }
    }
}
