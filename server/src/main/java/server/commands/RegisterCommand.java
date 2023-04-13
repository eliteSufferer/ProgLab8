package server.commands;

import common.exceptions.DatabaseHandlingException;
import common.exceptions.UniversalException;
import common.exceptions.UserAlreadyExists;
import common.exceptions.WrongArgumentsException;
import common.functional.User;
import server.utils.DatabaseUser;
import server.utils.ResponseOutputer;

/**
 * Command 'register'. Allows the user to register.
 */
public class RegisterCommand extends AbstractCommand {
    private DatabaseUser databaseUser;

    public RegisterCommand(DatabaseUser databaseUser) {
        super("register", "внутренняя команда");
        this.databaseUser = databaseUser;
    }

    /**
     * Executes the command.
     *
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (!stringArgument.isEmpty() || objectArgument != null) throw new WrongArgumentsException();
            if (databaseUser.insertUser(user)) ResponseOutputer.appendln("Пользователь " +
                    user.getUsername() + " зарегистрирован.");
            else throw new UserAlreadyExists();
            return true;
        } catch (WrongArgumentsException exception) {
            ResponseOutputer.appendln("Использование: внутренняя команда...");
        } catch (ClassCastException exception) {
            ResponseOutputer.appenderror("Переданный клиентом объект неверен!");
        } catch (UserAlreadyExists exception) {
            ResponseOutputer.appenderror("Пользователь " + user.getUsername() + " уже существует!");
        } catch (UniversalException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}