package server.utils;
import common.exceptions.WrongArgumentsException;
import common.functional.Request;
import common.functional.Response;
import common.functional.ServerResponseCode;
import server.commands.Command;

import java.io.FileNotFoundException;
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
//        System.out.println("clooo: "+ commandArgument);
//        if (Objects.equals(commandName, "close")){
//            return new Response(ServerResponseCode.CLOSE, null);
//        }
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

        }catch (WrongArgumentsException | FileNotFoundException e){
            return null;
        }
        return new Response(ServerResponseCode.OK, ResponseOutputer.getAndClear());
    }
}
