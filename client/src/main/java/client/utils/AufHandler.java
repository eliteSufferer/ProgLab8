package client.utils;

import common.functional.Request;
import common.functional.User;

import java.util.Scanner;

public class AufHandler {
    private final String loginCommand = "login";
    private final String registerCommand = "register";

    private Scanner userScanner;

    public AufHandler(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    /**
     * Handle user authentication.
     *
     * @return Request of user.
     */
    public Request handle() {
        AufControl authAsker = new AufControl(userScanner);
        String command = authAsker.askQuestion("У вас уже есть учетная запись?") ? loginCommand : registerCommand;
        User user = new User(authAsker.askLogin(), authAsker.hashPassword(authAsker.askPassword()));
        return new Request(command, "", user);
    }
}
