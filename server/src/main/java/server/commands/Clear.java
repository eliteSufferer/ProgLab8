
package server.commands;

import common.data.*;
import common.exceptions.*;
import common.functional.User;
import server.RunServer;
import server.utils.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Clear class represents a command that clears the collection.
 */
public class Clear extends AbstractCommand {
    private CollectionControl collectionControl;
    DatabaseCollectionManager databaseCollectionManager;

    /**
     * Constructs a Clear object with the given CollectionControl and CommunicationControl objects.
     *
     * @param collectionControl    the CollectionControl object that manages the collection
     */
    public Clear(CollectionControl collectionControl, DatabaseCollectionManager databaseCollectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionControl = collectionControl;
        this.databaseCollectionManager = databaseCollectionManager;


    }

    /**
     * Executes the command to clear the collection.
     *
     * @param argument the arguments for the command (not used in this command)
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        int i = 0;
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) {
                throw new WrongArgumentsException();
            }
            ResponseOutputer.appendln("good");
            Iterator<Worker> iterator = collectionControl.getCollection().iterator();
            while (iterator.hasNext()) {
                Worker worker = iterator.next();
                ResponseOutputer.appendln(collectionControl.getCollection().size());
                i++;
                ResponseOutputer.appendln(i);
                if (!worker.getOwner().equals(user)) {
                    RunServer.logger.info("Нельзя удалить данного worker (Permission denied)");
                    continue;
                }
                ResponseOutputer.appendln(worker);
                iterator.remove(); // Удаляем элемент из коллекции
                databaseCollectionManager.clearCollection(worker);
                ResponseOutputer.appendln("Завершили цикл");
            }
            ResponseOutputer.appendln("Коллекция очищена!");
            return true;
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("Превышенно кол-во аргументов");
        } catch (DatabaseHandlingException e) {
            e.printStackTrace();
            ResponseOutputer.appendln("ошибка");
            RunServer.logger.error("Произошло прямое изменение базы данных!");
        }
        return false;
    }
}
