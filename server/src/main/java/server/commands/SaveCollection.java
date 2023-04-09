
package server.commands;

import common.exceptions.*;
import server.utils.*;
/**
 * The {@code Show} class represents a command that outputs all elements of the collection in string representation.
 * Implements {@link Command} interface.
 */
public class SaveCollection extends AbstractCommand {
    FileControl fileControl;
    CollectionControl collectionControl;

    /**
     * Constructs a new {@code Show} object with the specified {@link CollectionControl} object.
     *
     * @param collectionControl the {@link CollectionControl} object for controlling the collection
     */
    public SaveCollection(FileControl fileControl, CollectionControl collectionControl) {
        super("save", "сохранить коллекцию в файл");
        this.fileControl = fileControl;
        this.collectionControl = collectionControl;
    }

    @Override
    public void execute(String argument, Object commandObjectArgument) {
        try {
            if (!argument.isEmpty() || commandObjectArgument != null) throw new WrongArgumentsException();
            collectionControl.saveCollection();
            System.out.println("Сохранено!");

        } catch (WrongArgumentsException e) {
            ResponseOutputer.appendln(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
