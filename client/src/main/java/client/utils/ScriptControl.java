package client.utils;

import common.exceptions.IncorrectInputInScriptException;
import common.exceptions.InputException;
import common.exceptions.ScriptRecursionException;
import common.exceptions.WrongCommandException;
import common.functional.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class ScriptControl {
    private Scanner userScanner;
    private Stack<File> scriptStack = new Stack<>();
    private Stack<Scanner> scannerStack = new Stack<>();

    public ScriptControl(File script){
        try{
            userScanner = new Scanner(script);
            scannerStack.add(userScanner);
            scriptStack.add(script);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No file on such path found!");
        }
    }

    public Request handle(ServerResponseCode serverResponseCode, User user) {
        String userInput;
        String[] userCommand;
        CheckCode processingCode;
        try {
            do {
                try {
                    if (serverResponseCode == ServerResponseCode.ERROR)
                        throw new IncorrectInputInScriptException();
                    while (!scannerStack.isEmpty() && !userScanner.hasNextLine()) {
                        userScanner.close();
                        userScanner = scannerStack.pop();
                        if (!scannerStack.isEmpty()) scriptStack.pop();
                        else return null;
                    }
                    userInput = userScanner.nextLine();
                    if (!userInput.isEmpty()) {
                        Printer.println(userInput);
                    }
                    userCommand = (userInput.trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                } catch (NoSuchElementException | IllegalStateException exception) {
                    Printer.printerror("CommandErrorException");
                    userCommand = new String[]{"", ""};
                    System.exit(0);
                }
                processingCode = processCommand(userCommand[0], userCommand[1], user);
            } while (userCommand[0].isEmpty());
            try {
                if (serverResponseCode == ServerResponseCode.ERROR || processingCode == CheckCode.ERROR)
                    throw new IncorrectInputInScriptException();
                switch (processingCode) {
                    case OBJECT:
                    case UPDATE_OBJECT:
                        WorkerPacket workerPacket = generateWorkerAdd();
                        return new Request(userCommand[0], userCommand[1], workerPacket, user);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if (!scriptFile.exists()) throw new FileNotFoundException();
                        if (!scriptStack.isEmpty() && scriptStack.search(scriptFile) != -1)
                            throw new ScriptRecursionException();
                        scannerStack.push(userScanner);
                        scriptStack.push(scriptFile);
                        userScanner = new Scanner(scriptFile);
                        Printer.println("ScriptRunning " + scriptFile.getName());
                        break;
                }
            } catch (FileNotFoundException exception) {
                Printer.printerror("ScriptFileNotFoundException");
                throw new IncorrectInputInScriptException();
            } catch (ScriptRecursionException exception) {
                Printer.printerror("ScriptRecursionException");
                throw new IncorrectInputInScriptException();
            } catch (InputException e) {
                throw new RuntimeException(e);
            }
        } catch (IncorrectInputInScriptException exception) {
            JOptionPane.showMessageDialog(null, "Некорректные данные в скрипте!");
            while (!scannerStack.isEmpty()) {
                userScanner.close();
                userScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return null;
        }
        return new Request(userCommand[0], userCommand[1], null, user);
    }

    private CheckCode processCommand(String command, String commandArgument, User user) {
        try {
            switch (command) {
                case "":
                    return CheckCode.ERROR;
                case "addElement":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    return CheckCode.OBJECT;
                case "add_if_min":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    return CheckCode.OBJECT;
                case "clear":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new WrongCommandException();
                    return CheckCode.SCRIPT;
                case "filter_greater_than_status":
                    if (commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "group_counting_by_status":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "help":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "info":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "print_field_ascending_person":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "remove_element_by_id":
                    if (commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "remove_greater":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    return CheckCode.OBJECT;
                case "show":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "sort":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    break;
                case "update_by_id":
                    if (commandArgument.isEmpty()) throw new WrongCommandException();
                    return CheckCode.UPDATE_OBJECT;
                case "exit":
                    if (!commandArgument.isEmpty()) throw new WrongCommandException();
                    System.exit(0);
                    break;
                default:
                    Printer.println("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                    return CheckCode.ERROR;
            }
        } catch (WrongCommandException e) {
            System.out.println("Неправильное использование команды " + command);
            return CheckCode.ERROR;
        }
        return CheckCode.OK;
    }

    private WorkerPacket generateWorkerAdd() throws InputException {
        ServerCommunicationControl worker = new ServerCommunicationControl(userScanner);
        return new WorkerPacket(
                worker.setName(),
                worker.setCoordinates(),
                worker.setSalary(),
                worker.choosePosition(),
                worker.chooseStatus(),
                worker.setPerson()
        );
    }

}
