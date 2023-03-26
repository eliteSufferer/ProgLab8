
package client.utils;
import common.exceptions.*;
import common.data.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The CommunicationControl class provides methods for communicating with the user through the console.
 * It allows setting and getting user's personal information, such as name, height and passport ID.
 */

public class CommunicationControl {
    public Scanner scanner;
    private boolean loop = true;
    public static boolean flagForScr;

    /**
     * Constructs a new CommunicationControl object with the given scanner.
     *
     * @param scanner the scanner object to be used for reading input from the console
     */

    public CommunicationControl(Scanner scanner) {
        this.scanner = scanner;
        flagForScr = false;
    }

    public void setFileMode() {
        flagForScr = true;
    }

    /**
     * Sets marine asker mode to 'User Mode'.
     */
    public void setUserMode() {
        flagForScr = false;
    }


    /**
     * Checks if the given string contains only digits or letters.
     *
     * @param str        the string to be checked
     * @param onlyDigits true if the string should contain only digits, false otherwise
     * @return true if the string contains only digits or letters, false otherwise
     */
    public static boolean containsOnlyDigitsOrLetters(String str, boolean onlyDigits) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String regex = onlyDigits ? "^\\d+$" : "^[a-zA-Z]+$";
        return str.matches(regex);
    }

    /**
     * Sets the value of the loop flag to the opposite of its current value.
     */
    public void setUnsetLoop() {
        this.loop = !this.loop;
    }

    /**
     * Changes the scanner object to read input from the given input stream.
     */
    public void changeScanner(Scanner scanner) throws FileNotFoundException {
        this.scanner = scanner;
    }


    /**
     * Prompts the user to enter their name and returns it.
     *
     * @return the user's name as a string
     * @throws InputException if the user enters an invalid name or if the loop flag is set to false
     */
    public String setName() throws InputException {
        String name;
        while (true) {
            try {
                System.out.println("введите имя");
                name = scanner.nextLine().trim();
                if (name.equals("")) throw new EmptyInputException("имя не может быть пустым");
                if (!containsOnlyDigitsOrLetters(name, false)) throw new InputException();
                System.out.println(name);
                flagForScr = true;
                return name;
            } catch (EmptyInputException | InputException e) {
                System.out.println("name is not correct!");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }

    }

    /**
     * Prompts the user to enter their height and returns it.
     *
     * @return the user's height as a long
     * @throws InputException if the user enters an invalid height or if the loop flag is set to false
     */

    private long setHeight() throws InputException {
        while (true) {
            try {
                System.out.print("Введите рост: ");
                String line = scanner.nextLine();
                long height = Long.parseLong(line);
                if ((height <= 0) || (height > 400)) {
                    throw new WrongArgumentsException("Высота не может быть меньше или равна нулю");
                }
                flagForScr = true;
                return height;
            } catch (Exception e) {
                System.out.println("Некорректный ввод. Попробуйте еще раз.");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }
    }

    /**
     * Private method for setting the passport ID of a Person object.
     *
     * @return String containing the passport ID of the person.
     * @throws InputException if the input is invalid or empty.
     */

    private String setPassportID() throws InputException {
        while (true) {
            try {
                System.out.print("Введите номер паспорта: ");
                String passportID = scanner.nextLine().trim();
                if (passportID.isEmpty()) {
                    throw new EmptyInputException("Номер паспорта не может быть пустым");
                }
                if ((!passportID.matches("\\d+") || (passportID.length() != 6))) {
                    throw new WrongArgumentsException("Номер паспорта должен содержать только цифры(6 цифр)");
                }
                flagForScr = true;
                return passportID;
            } catch (Exception e) {
                System.out.println("Некорректный ввод. Попробуйте еще раз.");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }
    }

    /**
     * Private method for setting the birthday of a Person object.
     *
     * @return LocalDateTime object containing the birth date of the person.
     * @throws InputException if the input is invalid or empty.
     */

    private LocalDateTime setBirthday() throws InputException {
        while (true) {
            try {
                System.out.print("Введите дату рождения в формате ГГГГ-ММ-ДД: ");
                String birthdayStr = scanner.nextLine().trim();
                if (birthdayStr.isEmpty()) throw new IllegalArgumentException();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", new Locale("ru", "Ru"));
                LocalDateTime bd = LocalDate.parse(birthdayStr, formatter).atStartOfDay();
                if (bd.isAfter(LocalDate.now().atStartOfDay())) throw new WrongArgumentsException();
                flagForScr = true;
                return bd;
            } catch (WrongArgumentsException e) {
                System.out.println(e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("неверный формат даты!");
            } catch (Exception e) {
                System.out.println("Некорректный ввод. Попробуйте еще раз.");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }
    }

    /**
     * Public method for setting the information of a Person object.
     *
     * @return Person object containing the information of a person.
     * @throws InputException if the input is invalid or empty.
     */

    public Person setPerson() throws InputException {
        try {
            LocalDateTime bDay = setBirthday();
            long height = setHeight();
            String passportID = setPassportID();
            return new Person(bDay, height, passportID, setLocation());
        } catch (Exception e) {
            throw new InputException();
        }
    }

    /**
     * Public method for setting the X-coordinate of a Location object.
     *
     * @return Integer containing the X-coordinate of the location.
     * @throws InputException if the input is invalid or empty.
     */
    public Integer setCoodrinateX() throws InputException {
        int coordX;
        String line;
        while (true) {
            try {
                System.out.println("Введите координату X: ");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException("не может быть пустым");
                coordX = Integer.parseInt(line);
                if (coordX > 468) throw new InputException();
                flagForScr = true;
                return coordX;
            } catch (EmptyInputException e) {
                System.out.println(e.getMessage());
            } catch (InputException e) {
                System.out.println("превышенно значение (max: 468)");
            } catch (NumberFormatException e) {
                System.out.println("должно быть числом а еще и целым !!!");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }

        }

    }

    /**
     * Reads and validates the user input for the Y-coordinate
     *
     * @return an Integer representing the Y-coordinate entered by the user
     * @throws InputException if the input is not valid or empty
     */
    public Integer setCoodrinateY() throws InputException {
        Integer coordY;
        String line;
        while (true) {
            try {
                System.out.println("Введите координату Y: ");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException();
                coordY = Integer.parseInt(line);
                if (coordY <= -922) throw new InputException();
                flagForScr = true;
                return coordY;
            } catch (EmptyInputException e) {
                System.out.println("некорректные данные, попробуйте еще раз");
            } catch (InputException e) {
                System.out.println("превышенно значение (max: 468)");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }

        }

    }

    /**
     * Reads and validates the user input for the salary
     *
     * @return a Double representing the salary entered by the user
     * @throws InputException if the input is not valid or empty
     */
    public Double setSalary() throws InputException {
        String line;
        double salary;
        while (true) {
            try {
                System.out.println("введите з/п: ");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException();
                salary = Double.parseDouble(line);
                if (salary <= 0) throw new InputException();
                flagForScr = true;
                return salary;
            } catch (EmptyInputException e) {
                System.out.println("вы ввели пустое значение!");
            } catch (InputException e) {
                System.out.println("зарплата должна быть больше 0");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }

        }

    }

    /**
     * Reads and validates the user input for the coordinates
     *
     * @return a Coordinates object representing the coordinates entered by the user
     * @throws InputException if the input is not valid or empty
     */
    public Coordinates setCoordinates() throws InputException {
        try {
            Integer x;
            int y;
            x = setCoodrinateX();
            y = setCoodrinateY();

            return new Coordinates(x, y);
        } catch (InputException e) {
            throw new InputException();

        }

    }

    /**
     * Reads and validates the user input for the location
     *
     * @return a Location object representing the location entered by the user
     * @throws InputException if the input is not valid or empty
     */

    public Location setLocation() throws InputException {
        String name;
        String line;
        float x;
        long y;
        int z;

        while (true) {
            try {

                System.out.println("Введите координаты x (Float)");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException();
                x = Float.parseFloat(line);

                System.out.println("Введите координаты y (Long)");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException();
                y = Long.parseLong(line);

                System.out.println("Введите координаты z (Integer)");
                line = scanner.nextLine().trim();
                if (line.equals("")) throw new EmptyInputException();
                z = Integer.parseInt(line);

                System.out.println("Название локации: ");
                name = scanner.nextLine().trim();
                if (name.equals("")) throw new EmptyInputException();
                flagForScr = true;
                return new Location(x, y, z, name);
            } catch (EmptyInputException e) {
                //err - ввели пустоту
            } catch (NumberFormatException e) {
                //Console.err должно быть числом !!1
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }
    }

    /**
     * Reads and validates the user input for the position
     *
     * @return a Position enum representing the position entered by the user
     * @throws InputException if the input is not valid or empty
     */
    public Position choosePosition() throws InputException {
        Position position;
        String setPos;
        while (true) {
            try {

                System.out.println(Position.nameList());
                System.out.println("выбирайте");
                setPos = scanner.nextLine().trim();
                position = Position.valueOf(setPos.toUpperCase());
                flagForScr = true;
                return position;
            } catch (Exception e) {
                System.out.println("неверные данные");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }


    }

    /**
     * Reads and validates the user input for the status
     *
     * @return a Status enum representing the status entered by the user
     * @throws InputException if the input is not valid or empty
     */

    public Status chooseStatus() throws InputException {
        Status status;
        String setStat;
        while (true) {
            try {

                System.out.println(Status.nameList());
                System.out.println("какую штуку выберите");
                setStat = scanner.nextLine().trim();
                status = Status.valueOf(setStat.toUpperCase());
                flagForScr = true;
                return status;
            } catch (NoSuchElementException e) {
                System.out.println("нет такого элемента");
            } catch (Exception e) {
                System.out.println("ошибка данных");
            } finally {
                if ((!loop) && (!flagForScr)) {
                    throw new InputException();

                }
                flagForScr = false;
            }
        }
    }

    /**
     * Asks the user to confirm a choice
     *
     * @return true if the user confirms, false otherwise
     */
    public boolean confirm() {
        if (loop) {
            String line;
            System.out.println("y/n");
            line = scanner.nextLine().trim();
            return line.equals("y");
        } else {
            return true;
        }
    }


    /**
     * Reads and returns additional information entered by the user
     *
     * @return a String representing the additional information entered by the user
     */
    public String setEnotherInfo() {
        String line;
        line = scanner.nextLine().trim();
        return line;
    }
}
