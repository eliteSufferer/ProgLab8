package server.commands;

import java.io.FileNotFoundException;

public interface Command {
    String getDescription();

    String getName();

    void execute(String argument, Object commandObjectArgument) throws FileNotFoundException;
}
