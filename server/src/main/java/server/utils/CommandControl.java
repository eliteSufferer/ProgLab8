
package server.utils;

import common.functional.User;
import server.commands.*;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CommandControl {
    private final HashMap<String, Command> commandMapping = new HashMap<>();
    private final ArrayList<String> newCommands = new ArrayList<>();

    private ReentrantLock locker = new ReentrantLock();

    private Command addElement;
    private Command addElementIfMin;
    private Command clear;

    private Command executeScript;
    private Command filterGreaterStatus;

    private Command groupByStatus;
    private Command help;
    private Command info;
    private Command printFieldOfPerson;

    private Command removeElementByID;
    private Command removeGreater;

    private Command show;
    private Command sort;
    private Command updateByID;
    private SendNewList sendNewList;
    CollectionControl collectionControl;

    private Command login;

    private Command register;


    public CommandControl(Command addElement, Command addElementIfMin, Command clear,
                          Command executeScript, /*Command exit,*/ Command filterGreaterStatus,
                          Command groupByStatus, Command help, Command info, Command printFieldOfPerson,
                          Command removeElementByID, Command removeGreater,
                          Command show, Command sort, Command updateByID, Command login, Command register, CollectionControl collectionControl, SendNewList sendNewList ) {
        this.addElement = addElement;
        this.addElementIfMin = addElementIfMin;
        this.clear = clear;
        this.executeScript = executeScript;
        this.filterGreaterStatus = filterGreaterStatus;
        this.groupByStatus = groupByStatus;
        this.help = help;
        this.info = info;
        this.printFieldOfPerson = printFieldOfPerson;
        this.removeElementByID = removeElementByID;
        this.removeGreater = removeGreater;
        this.show = show;
        this.sort = sort;
        this.updateByID = updateByID;
        this.login = login;
        this.register = register;
        this.collectionControl = collectionControl;
        this.sendNewList = sendNewList;


        newCommands.add(sendNewList.getName());


        commandMapping.put(addElement.getName(), addElement);
        commandMapping.put(addElementIfMin.getName(), addElementIfMin);
        commandMapping.put(clear.getName(), clear);
        commandMapping.put(executeScript.getName(), executeScript);
        /*commandMapping.put(exit.getName(), exit);*/
        commandMapping.put(filterGreaterStatus.getName(), filterGreaterStatus);
        commandMapping.put(groupByStatus.getName(), groupByStatus);
        commandMapping.put(help.getName(), help);
        commandMapping.put(info.getName(), info);
        commandMapping.put(printFieldOfPerson.getName(), printFieldOfPerson);
        commandMapping.put(removeElementByID.getName(), removeElementByID);
        commandMapping.put(removeGreater.getName(), removeGreater);
        commandMapping.put(show.getName(), show);
        commandMapping.put(sort.getName(), sort);
        commandMapping.put(updateByID.getName(), updateByID);
        commandMapping.put(sendNewList.getName(), sendNewList);
        collectionControl.getMappingOfCommands(commandMapping);
    }


    public HashMap<String, Command> getMapping() {
        return commandMapping;
    }

    public ArrayList<String> getNewCommands(){
        return newCommands;
    }


    public boolean add(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return addElement.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean addElementIfMin(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return addElementIfMin.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean clear(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return clear.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean executeScript(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return executeScript.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }


    public boolean filterGreaterStatus(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return filterGreaterStatus.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean groupByStatus(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return groupByStatus.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean help(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return help.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean info(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return info.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean printField(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return printFieldOfPerson.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean removeById(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return removeElementByID.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean removeGreater(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return removeGreater.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean show(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return show.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }
    public boolean sendNewListt(String stringArgument, Object objectArgument, User user){
        locker.lock();
        try {
            return sendNewList.execute(stringArgument, objectArgument, user);
        } finally {
            locker.unlock();
        }
    }
    public SendNewList getSendNewList(){
        return sendNewList;
    }

    public boolean sort(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return sort.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean updateById(String stringArgument, Object objectArgument, User user) {
        locker.lock();
        try {
            return updateByID.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public boolean login(String stringArgument, Object objectArgument, User user) {
        try {
            return login.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register(String stringArgument, Object objectArgument, User user) {
        try {
            return register.execute(stringArgument, objectArgument, user);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
