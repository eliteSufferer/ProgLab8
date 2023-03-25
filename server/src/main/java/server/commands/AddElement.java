
package server.commands;

import common.data.Worker;
import server.utils.CollectionControl;

/**
 * The {@code AddElement} class represents a command that adds a new worker element to the collection.
 * The command requires instances of {@link CommunicationControl} and {@link CollectionControl} to communicate with the user
 * and modify the collection, respectively.
 * This class extends the {@link AbstractCommand} abstract class.
 */
public class AddElement extends AbstractCommand {


    private CollectionControl collectionControl;

    /**
     * @param collectionControl the {@link CollectionControl} instance to be used for modifying the collection
     */
    public AddElement(CollectionControl collectionControl) {
        super("addElement", "Добавить элемент в коллекцию");
        this.collectionControl = collectionControl;
    }

    /**
     * Executes the command by adding a new worker element to the collection.
     *
     * @param argument the command argument
     */
    public void execute(String argument) {
        try {
            if (!argument.isEmpty()) throw new WrongArgumentsException();
            collectionControl.addToCollection(new Worker(communicationControl.setName(),
                    communicationControl.setCoordinates(),
                    communicationControl.setSalary(), communicationControl.choosePosition(),
                    communicationControl.chooseStatus(), communicationControl.setPerson()));
        } catch (WrongArgumentsException e) {
            Console.err(e.getMessage());
        } catch (InputException e) {
            Console.err("Некорректный данные в скрипте!");
        }
    }

}
