package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.*;

/**
 * Command to print the Person data in ascending order by their name.
 */
public class PrintFieldOfPerson extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructor to create a new PrintFieldOfPerson object.
     *
     * @param collectionControl the CollectionControl object to be used
     */
    public PrintFieldOfPerson(CollectionControl collectionControl) {
        super("print_field_ascending_person", "выводит данные о человеке в порядке возрастания");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the PrintFieldOfPerson command to print the Person data in ascending order by name.
     *
     * @param argument the arguments passed to the command
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.sortPerson();
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }
        return false;
    }
}

