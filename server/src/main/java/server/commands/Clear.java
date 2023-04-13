
package server.commands;

import common.data.*;
import common.exceptions.*;
import common.functional.User;
import server.RunServer;
import server.utils.*;
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
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            for (Worker worker : collectionControl.getCollection()) {
                if (!worker.getOwner().equals(user)) throw new PermissionsDeniedException();
                if (!databaseCollectionManager.checkWorkerUserId(worker.getId(), user)) throw new ManualDatabaseEditException();
            }
            databaseCollectionManager.clearCollection();
            collectionControl.clear();
            ResponseOutputer.appendln("Коллекция очищена!");

        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln("Превышенно кол-во аргументов");
        } catch (PermissionsDeniedException e) {
            RunServer.logger.error("Недостаточно прав для выполнения данной команды!");
        } catch (ManualDatabaseEditException | DatabaseHandlingException e) {
            RunServer.logger.error("Произошло прямое изменение базы данных!");
        }
        return false;
    }
}
