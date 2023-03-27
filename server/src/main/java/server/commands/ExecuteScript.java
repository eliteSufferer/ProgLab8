

package server.commands;

import common.exceptions.*;
import server.utils.CollectionControl;
import server.utils.FileControl;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void execute(String argument, Object commandObjectArgument) throws FileNotFoundException {
        argument = argument.trim();
        try {
            if (argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            System.out.println("Скрипт выполняется");
        } catch (WrongArgumentsException e) {
            System.out.println("неверные аргументы");
        }

    }
}

