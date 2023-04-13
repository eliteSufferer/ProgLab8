package server.commands;

import common.exceptions.WrongArgumentsException;
import common.functional.User;
import server.utils.*;

import java.io.CharArrayReader;

/**
 * The Sort class represents a command to sort the collection in natural order.
 */
public class Sort extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructs the Sort object with the specified CollectionControl object.
     *
     * @param collectionControl the CollectionControl object to be used
     */
    public Sort(CollectionControl collectionControl) {
        super("sort", "Отсортировать коллекцию в естественном порядке");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the Sort command by invoking the sort() method on the CollectionControl object.
     *
     * @param argument the argument to be passed to the command
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.sort();
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }
        return false;
    }
}
