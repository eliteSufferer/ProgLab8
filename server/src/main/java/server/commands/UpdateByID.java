
package server.commands;

import common.data.Worker;
import common.exceptions.*;
import common.functional.WorkerPacket;
import server.utils.*;
/**
 * This command is used to update the value of a collection element whose id matches the specified one.
 */

public class UpdateByID extends AbstractCommand {
    CollectionControl collectionControl;

    public UpdateByID(CollectionControl collectionControl) {
        super("update_by_id", "Обновить значение элемента коллекции, id  которого равен заданному");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the command with the specified argument
     *
     * @param argument the id of the element to be updated
     */
    @Override
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (argument.isEmpty() || commandObjectArgument == null) throw new WrongArgumentsException();
            WorkerPacket workerPacket = (WorkerPacket) commandObjectArgument;
            Worker worker = new Worker(workerPacket.getName(),
                    workerPacket.getCoordinates(),
                    workerPacket.getSalary(), workerPacket.getPosition(),
                    workerPacket.getStatus(), workerPacket.getPerson());

            int id = Integer.parseInt(argument.trim());
            collectionControl.updateByID(id, worker);
            System.out.println("Замена успешно завершена!");
        } catch (WrongArgumentsException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("неправильный тип данных. Должен быть целочисленным");
        }
    }
}
