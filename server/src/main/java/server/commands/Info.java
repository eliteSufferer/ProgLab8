package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.*;

/**
 * Command to display information about the collection.
 */
public class Info extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Creates a new instance of Info command.
     *
     * @param collectionControl the collection control object
     */
    public Info(CollectionControl collectionControl) {
        super("info", "выводит информацию о коллекции (тип, дата инициализации, кол-во элементов и т.д.)");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the Info command.
     *
     * @param argument the command argument (not used in this command)
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty()) throw new WrongArgumentsException();

            collectionControl.getInfo();
            return true;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }
        return false;
    }
}
