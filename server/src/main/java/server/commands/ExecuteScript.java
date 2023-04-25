

package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.CollectionControl;
import server.utils.ResponseOutputer;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * The ExecuteScript class represents a command to execute a script file.
 */
public class ExecuteScript extends AbstractCommand {
    CollectionControl collectionControl;
    boolean flag = true;
    static Stack<String> stackWithFiles = new Stack<>();
    static Stack<Scanner> stackWithScanners = new Stack<>();

    /**
     * Constructs a new ExecuteScript instance with the specified collection and communication controls.
     *
     * @param collectionControl the collection control instance
     */
    public ExecuteScript(CollectionControl collectionControl) {
        super("execute_script", "выполняет скрипт");
        this.collectionControl = collectionControl;
    }


    /**
     * Executes the command with the specified argument.
     *
     * @param argument the argument for the command
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) throws FileNotFoundException {
        argument = argument.trim();
        try {
            if (argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            ResponseOutputer.appendln("Скрипт выполняется");
            return true;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("неверные аргументы");
        }

        return false;
    }
}

