package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.*;
/**
 * Command
 */

public class Show extends AbstractCommand {
    CollectionControl collectionControl;

    public Show(CollectionControl collectionControl) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлению");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the command.
     *
     * @param argument a string argument for the command
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            System.out.println("USER " + user.getUsername());
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            this.collectionControl.show();
            return true;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }

        return false;
    }
}
