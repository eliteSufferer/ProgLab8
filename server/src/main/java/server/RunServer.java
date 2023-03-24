package server;

import server.utils.RequestHandler;

import java.io.IOException;
import java.net.*;

public class RunServer {
    public static void main(String[] args) throws IOException {
        RequestHandler requestHandler = new RequestHandler();
        Server server = new Server(7777, requestHandler);

    }
}

