package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.*;

/**
 * The {@code Help} command displays the list of available commands and their descriptions.
 */
public class Help extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructs a new {@code Help} object with the specified {@code CollectionControl} object.
     *
     * @param collectionControl the {@code CollectionControl} object that this command will operate on
     */
    public Help(CollectionControl collectionControl) {
        super("help", "вывести справку по доступным командам");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes this command with the specified argument.
     * If the argument is not empty, throws a {@code WrongArgumentsException}.
     * Otherwise, displays the list of available commands and their descriptions.
     *
     * @param argument the argument to be passed to this command
     */
    @Override
    public void execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.sendCommandMap().forEach((key, value) -> {
                ResponseOutputer.appendln(key + " : " + value.getDescription());
            });
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("у данной команды не должно быть аргементов");
        }
    }
}