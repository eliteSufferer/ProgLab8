package server.utils;
import common.exceptions.WrongArgumentsException;
import common.functional.Request;
import common.functional.Response;
import common.functional.ServerResponseCode;
import server.commands.Command;
import server.utils.CommandControl;

import java.io.FileNotFoundException;
import java.net.*;
import java.util.HashMap;

public class RequestHandler {
    private String commandName;
    private String commandArgument;
    private CommandControl commandControl;
    public RequestHandler(CommandControl commandControl){
        this.commandControl = commandControl;
    }
    public Response handle(Request request) {
        commandName = request.getCommandName().trim();
        commandArgument = request.getCommandStringArgument();
        try {
            HashMap<String, Command> commandHashMap = commandControl.getMapping();
            if (!commandHashMap.containsKey(commandName)) {
                throw new WrongArgumentsException();
            }
            for (String key : commandHashMap.keySet()) {
                if (key.equalsIgnoreCase(commandName)) {
                    commandHashMap.get(key).execute(commandArgument, request.getCommandObjectArgument());
                }
            }
        }catch (WrongArgumentsException e){
            System.out.println("d");
            return null;
        }catch ( FileNotFoundException e){
            System.out.println("dd");
            return null;
        }
        return new Response(ServerResponseCode.OK, "OK");
    }
}
