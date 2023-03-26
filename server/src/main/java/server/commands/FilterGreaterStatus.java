
package server.commands;

import common.data.*;
import common.exceptions.*;
import server.utils.*;
/**
 * The {@code FilterGreaterStatus} class represents a command that filters the elements in the collection by the status field
 * and displays those whose value is greater than the specified one.
 */
public class FilterGreaterStatus extends AbstractCommand {
    CollectionControl collectionControl;

    /**
     * Constructs a new {@code FilterGreaterStatus} object with the specified {@code CollectionControl} and {@code CommunicationControl}.
     * @param collectionControl the {@code CollectionControl} object to control the collection
     */
    public FilterGreaterStatus(CollectionControl collectionControl) {
        super("filter_greater_than_status", "вывести элементы, значение поля status которых больше заданного");
        this.collectionControl = collectionControl;


    }

    /**
     * Executes the command to filter the elements in the collection by the status field and displays those whose value is greater than the specified one.
     * @param argument the command argument (not used)
     */
    @Override
    public void execute(String argument, Object commandObjectArgument) {
        String line;

        try {
            if (argument.isEmpty() || commandObjectArgument != null ) throw new WrongArgumentsException();
            line = argument.trim();

            for (Worker worker : collectionControl.filterGreaterThanStatus(line)) {
                System.out.println(worker.toString());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Не является элементом Status");
        } catch (WrongArgumentsException e) {
            System.out.println("Неверное кол-во аргементов...");
        }
    }
}
