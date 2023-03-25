package server.utils;
import common.functional.Request;
import server.commands.Command;

import java.net.*;
import java.util.HashMap;

public class RequestHandler {
    private String commandName;
    private String commandArgument;
    public RequestHandler(){

    }
    public void handle(Request request){
        commandName = request.getCommandName();
        commandArgument = request.getCommandStringArgument();
        HashMap<String, Command> =
        if (.containsKey("key1")) {
    }
}
