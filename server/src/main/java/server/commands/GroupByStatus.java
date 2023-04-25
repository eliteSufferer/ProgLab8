
package server.commands;

import common.exceptions.*;
import common.functional.User;
import server.utils.*;
/**
 * This command groups the elements of the collection by the value of the status field and displays the number of elements in each group.
 */
public class GroupByStatus extends AbstractCommand{
    CollectionControl collectionControl;

    public GroupByStatus(CollectionControl collectionControl) {
        super("group_counting_by_status", "сгруппировать элементы коллекции по назначению поля status, вывести кол-во элементов в каждой группе");
        this.collectionControl = collectionControl;
    }

    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.gropByStatus();
            return true;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("Неверное кол-во аргементов");
        }
        return false;
    }
}