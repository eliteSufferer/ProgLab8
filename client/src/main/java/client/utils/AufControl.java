package client.utils;

import common.exceptions.UniversalException;
import common.functional.Printer;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class AufControl {
    private Scanner userScanner;

    public AufControl(Scanner userScanner) {

        this.userScanner = userScanner;
    }

    public String askLogin() {
        String login;
        while (true) {
            try {
                Printer.println("Введите логин:");
                login = userScanner.nextLine().trim();
                if (login.equals("")) throw new UniversalException();
                break;
            } catch (NoSuchElementException e) {
                Printer.printerror("Данного логина не существует!");
            } catch (UniversalException e) {
                Printer.printerror("Имя не может быть пустым!");
            } catch (IllegalStateException e) {
                Printer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return login;
    }

    public String askPassword() {
        String password;
        while (true) {
            try {
                Printer.println("Введите пароль:");
                password = userScanner.nextLine().trim();
                break;
            } catch (NoSuchElementException e) {
                Printer.printerror("Неверный логин или пароль!");
            } catch (IllegalStateException e) {
                Printer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return password;
    }

    public boolean askQuestion(String question) {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                Printer.println(finalQuestion);
                answer = userScanner.nextLine().trim();
                if (!answer.equals("+") && !answer.equals("-")) throw new UniversalException();
                break;
            } catch (NoSuchElementException exception) {
                Printer.printerror("Ответ не распознан!");
            } catch (UniversalException e) {
                Printer.printerror("Ответ должен быть представлен знаками '+' или '-'!");
            } catch (IllegalStateException exception) {
                Printer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }

}
