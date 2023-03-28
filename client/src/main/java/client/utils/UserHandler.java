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
                case "addElement":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "add_if_min":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "clear":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.SCRIPT;
                case "exit":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "filter_greater_than_status":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "group_counting_by_status":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "help":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "info":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "print_field_ascending_person":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "remove_element_by_id":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "remove_greater":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.OBJECT;
                case "save":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "show":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "sort":
                    if (!commandArgument.isEmpty()) throw new RuntimeException();
                    break;
                case "update_by_id":
                    if (commandArgument.isEmpty()) throw new RuntimeException();
                    return CheckCode.UPDATE_OBJECT;
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
                        System.out.println(userCommand[1]);
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
                System.out.println(userCommand[1]);
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
