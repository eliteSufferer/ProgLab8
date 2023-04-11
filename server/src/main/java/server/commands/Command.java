package server.commands;

import common.functional.User;

import java.io.FileNotFoundException;

public interface Command {
    String getDescription();

    String getName();

    void execute(String argument, Object commandObjectArgument, User user) throws FileNotFoundException;
}
