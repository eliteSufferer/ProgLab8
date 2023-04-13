package server.commands;

import common.exceptions.DatabaseHandlingException;
import common.exceptions.UniversalException;
import common.exceptions.UserIsNotFoundException;
import common.exceptions.WrongArgumentsException;
import common.functional.User;
import server.utils.DatabaseUser;
import server.utils.ResponseOutputer;

/**
 * Command 'login'. Allows the user to login.
 */
public class LoginCommand extends AbstractCommand {
    private DatabaseUser databaseUser;

    public LoginCommand(DatabaseUser databaseUserManager) {
        super("login", "внутренняя команда");
        this.databaseUser = databaseUserManager;
    }


    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (!stringArgument.isEmpty() || objectArgument != null) throw new WrongArgumentsException();
            if (databaseUser.checkUserByUsernameAndPassword(user)) ResponseOutputer.appendln("Пользователь " +
                    user.getUsername() + " авторизован.");
            else throw new UserIsNotFoundException();
            return true;
        } catch (WrongArgumentsException exception) {
            ResponseOutputer.appendln("И");
        } catch (ClassCastException exception) {
            ResponseOutputer.appenderror("Переданный клиентом объект неверен!");
        } catch (UserIsNotFoundException exception) {
            ResponseOutputer.appenderror("Неправильные имя пользователя или пароль!");
        } catch (UniversalException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

