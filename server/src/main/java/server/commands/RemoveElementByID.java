package server.commands;

import common.data.Worker;
import common.exceptions.*;
import common.functional.User;
import server.RunServer;
import server.utils.*;

/**
 * The RemoveElementByID class represents a command to remove an element from the collection by its ID.
 */
public class RemoveElementByID extends AbstractCommand {
    CollectionControl collectionControl;
    DatabaseCollectionManager databaseCollectionManager;

    /**
     * Constructs the RemoveElementByID object with the specified CollectionControl and CommunicationControl objects.
     *
     * @param collectionControl    the CollectionControl object to be used
     */
    public RemoveElementByID(CollectionControl collectionControl, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_element_by_id", "Remove an element from the collection by its ID");
        this.collectionControl = collectionControl;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the RemoveElementByID command by invoking the removeElementByID() method on the CollectionControl object.
     *
     * @param argument the argument to be passed to the command
     */
    @Override
    public boolean execute(String argument, Object commandObjectArgument, User user) {
        try {
            if (argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            if (collectionControl.collectionSize() == 0) throw new CollectionIsEmptyException();
            int id = Integer.parseInt(argument.trim());
            Worker workerToRemove = collectionControl.getById(id);
            if (workerToRemove == null) throw new WorkerNotFoundException();
            if (!workerToRemove.getOwner().equals(user)) throw new PermissionsDeniedException();
            if (!databaseCollectionManager.checkWorkerUserId(workerToRemove.getId(), user)) throw new ManualDatabaseEditException();
            databaseCollectionManager.deleteWorkerById(id);
            collectionControl.removeElementByID(id);
            ResponseOutputer.appendln("Солдат успешно удален!");
            collectionControl.updateAllIDs();



        } catch (NumberFormatException e) {
            ResponseOutputer.appendln("Incorrect ID format");
        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        } catch (CollectionIsEmptyException e) {
            RunServer.logger.error("CollectionIsEmptyException");
        } catch (PermissionsDeniedException e) {
            RunServer.logger.error("Недостаточно прав для выполнения данной команды!");
        } catch (ManualDatabaseEditException e) {
            RunServer.logger.error("ManualDatabaseEditException");
        } catch (DatabaseHandlingException e) {
            RunServer.logger.error("Произошла ошибка при обращении к базе данных!");
        }

        return false;
    }
}
