package client.utils;

import common.data.Coordinates;
import common.exceptions.InputException;
import common.exceptions.ScriptRecursionException;
import common.exceptions.ServerCodeErrorException;
import common.functional.Printer;
import common.functional.Request;
import common.functional.ServerResponseCode;
import common.functional.WorkerPacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class UserHandler {

    private Scanner chosenScanner;
    private Stack<File> scriptStack = new Stack<>();
    private Stack<Scanner> scannerStack = new Stack<>();

    public UserHandler(Scanner userScanner){
        this.chosenScanner = userScanner;
    }

    private boolean fileMode() {
        return !scannerStack.isEmpty();
    }

    private CheckCode processCommand(String command, String commandArgument) {
        try {
            switch (command) {
                case "":
                    return CheckCode.ERROR;
                case "help":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "info":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "show":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "addElement":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "update":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.UPDATE_OBJECT;
                case "remove_by_id":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "clear":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "save":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.SCRIPT;
                case "exit":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "add_if_min":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "remove_greater":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "history":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "sum_of_health":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "max_by_melee_weapon":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "filter_by_weapon_type":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "server_exit":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                default:
                    Printer.println("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                    return CheckCode.ERROR;
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            return CheckCode.ERROR;
        }
        return CheckCode.OK;
    }

    private WorkerPacket generateWorkerAdd() throws InputException {
        CommunicationControl worker = new CommunicationControl(chosenScanner);
        if (fileMode()) worker.setFileMode();
        return new WorkerPacket(
                worker.setName(),
                worker.setCoordinates(),
                worker.setSalary(),
                worker.choosePosition(),
                worker.chooseStatus(),
                worker.setPerson()
        );
    }

    public Request handle(ServerResponseCode responseCode){
        String userInput;
        String[] userCommand;
        CheckCode processingCode;
        try{
            do {
                try {
                    if (fileMode() && (responseCode == ServerResponseCode.ERROR)){
                        throw new ServerCodeErrorException();}

                        while (fileMode() && !chosenScanner.hasNextLine()) {
                            chosenScanner.close();
                            chosenScanner = scannerStack.pop();
                            Printer.println("Возвращаюсь к скрипту '" + scriptStack.pop().getName() + "'...");
                        }
                        if (fileMode()){
                            userInput = chosenScanner.nextLine();
                            if (!userInput.isEmpty()){
                                Printer.println(userInput);
                            }
                        } else{
                            userInput = chosenScanner.nextLine();
                        }
                        userCommand = (userInput.trim() + " ").split(" ", 2);
                        userCommand[1] = userCommand[1].trim();
                } catch (NoSuchElementException | IllegalStateException exception) {
                    Printer.println();
                    Printer.printerror("Произошла ошибка при вводе команды!");
                    userCommand = new String[]{"", ""};
                }
                processingCode = processCommand(userCommand[0], userCommand[1]);

            } while (processingCode == CheckCode.ERROR && !fileMode() || userCommand[0].isEmpty());
            try {
                if (fileMode() && (responseCode == ServerResponseCode.ERROR || processingCode == CheckCode.ERROR))
                    throw new ServerCodeErrorException();
                switch (processingCode) {
                    case OBJECT:
                    case UPDATE_OBJECT:
                        WorkerPacket addWorker = generateWorkerAdd();
                        return new Request(userCommand[0], userCommand[1], addWorker);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if (!scriptFile.exists()) throw new FileNotFoundException();
                        if (!scriptStack.isEmpty() && scriptStack.search(scriptFile) != -1)
                            throw new ScriptRecursionException();
                        scannerStack.push(chosenScanner);
                        scriptStack.push(scriptFile);
                        chosenScanner = new Scanner(scriptFile);
                        Printer.println("Выполняю скрипт '" + scriptFile.getName() + "'...");
                        break;
                }
            } catch (FileNotFoundException e) {
                Printer.printerror("Файл со скриптом не найден!");
            } catch (ScriptRecursionException e) {
                Printer.printerror("Скрипты не могут вызываться рекурсивно!");
                throw new ServerCodeErrorException();
            }
        } catch (InputException | ServerCodeErrorException e) {
            Printer.printerror("Выполнение скрипта прервано!");
            while (!scannerStack.isEmpty()) {
                chosenScanner.close();
                chosenScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return new Request();
        }
        return new Request(userCommand[0], userCommand[1]);
    }


}
