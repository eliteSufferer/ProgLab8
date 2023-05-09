package client.GUI.sort;

import common.data.Position;
import common.data.Status;
import common.data.Worker;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

public class WorkerUtils {
    public static SortingAndFilteringParameters parameters;
    public static ArrayList<Worker> sortAndFilterWorkers(ArrayList<Worker> workers) {

        // Отфильтровать список работников
        ArrayList<Worker> filteredWorkers = workers.stream()
                .filter(worker -> applyFilter(worker, parameters))
                .collect(Collectors.toCollection(ArrayList::new));

        // Отсортировать список работников
        Comparator<Worker> comparator = getComparator(parameters);
        if (comparator != null) {
            filteredWorkers.sort(comparator);
        }

        return filteredWorkers;
    }

    private static boolean applyFilter(Worker worker, SortingAndFilteringParameters parameters) {
        String filteringColumn = parameters.getFilteringColumn();
        String filteringOperation = parameters.getFilteringOperation();
        String filteringValue = parameters.getFilteringValue();

        if (filteringColumn == null || filteringOperation == null || filteringValue == null) {
            return true;
        }

        switch (filteringColumn) {
            case "id":
                int id = worker.getId();
                int filterId = Integer.parseInt(filteringValue);
                return compareValues(id, filterId, filteringOperation);

            case "name":
                String name = worker.getName();
                return compareValues(name, filteringValue, filteringOperation);

            case "coordX":
                int x = worker.getCoordinates().getX();
                int filterX = Integer.parseInt(filteringValue);
                return compareValues(x, filterX, filteringOperation);


            case "coordY":
                int y = worker.getCoordinates().getY();
                int filterY = Integer.parseInt(filteringValue);
                return compareValues(y, filterY, filteringOperation);

            case "salary":
                double salary = worker.getSalary();
                Double filterSalary = Double.parseDouble(filteringValue);
                return compareValues(salary, filterSalary, filteringOperation);

            case "position":
                Position position = worker.getPosition();
                Position filterPosition = Position.valueOf(filteringValue);
                return compareValues(position, filterPosition, filteringOperation);

            case "status":
                Status status = worker.getStatus();
                Status filterStatus = Status.valueOf(filteringValue);
                return compareValues(status, filterStatus, filteringOperation);

            case "birthday":
                LocalDateTime birthday = worker.getPerson().getBirthday();
                LocalDateTime filterBirthday = LocalDateTime.parse(filteringValue+"T00:00:00");
                return compareValues(birthday, filterBirthday, filteringOperation);

            case "height":
                Long height = worker.getPerson().getHeight();
                Long filterHeight = Long.parseLong(filteringValue);
                return compareValues(height, filterHeight, filteringOperation);

            case "passport":
                String passportID = worker.getPerson().getPassportID();
                return compareValues(passportID, filteringValue, filteringOperation);

            case "locX":
                float locX = worker.getPerson().getLocation().getX();
                float filterLocX = Float.parseFloat(filteringValue);
                return compareValues(locX, filterLocX, filteringOperation);

            case "locY":
                long locY = worker.getPerson().getLocation().getY();
                long filterLocY = Long.parseLong(filteringValue);
                return compareValues(locY, filterLocY, filteringOperation);

            case "locZ":
                int locZ = worker.getPerson().getLocation().getZ();
                int filterLocZ = Integer.parseInt(filteringValue);
                return compareValues(locZ, filterLocZ, filteringOperation);

            case "locName":
                String locName = worker.getPerson().getLocation().getName();
                return compareValues(locName, filteringValue, filteringOperation);




            default:
                return true;
        }
    }

    private static Comparator<Worker> getComparator(SortingAndFilteringParameters parameters) {
        String sortingColumn = parameters.getSortingColumn();
        boolean ascending = parameters.getAscending();

        if (sortingColumn == null) {
            return null;
        }

        Comparator<Worker> comparator;
        switch (sortingColumn) {
            case "id":
                comparator = Comparator.comparing(Worker::getId);
                break;

            case "name":
                comparator = Comparator.comparing(Worker::getName);
                break;

            case "coordX":
                comparator = Comparator.comparing(w -> w.getCoordinates().getX());
                break;

            case "coordY":
                comparator = Comparator.comparingInt(w -> w.getCoordinates().getY());
                break;

            case "creationDate":
                comparator = Comparator.comparing(Worker::getCreationDate);
                break;

            case "salary":
                comparator = Comparator.comparing(Worker::getSalary);
                break;

            case "position":
                comparator = Comparator.comparing(Worker::getPosition);
                break;

            case "status":
                comparator = Comparator.comparing(Worker::getStatus);
                break;

            case "birthday":
                comparator = Comparator.comparing(w -> w.getPerson().getBirthday());
                break;

            case "height":
                comparator = Comparator.comparingLong(w -> w.getPerson().getHeight());
                break;

            case "passport":
                comparator = Comparator.comparing(w -> w.getPerson().getPassportID());
                break;

            case "locX":
                comparator = Comparator.comparing(w -> w.getPerson().getLocation().getX());
                break;

            case "locY":
                comparator = Comparator.comparingLong(w -> w.getPerson().getLocation().getY());
                break;

            case "locZ":
                comparator = Comparator.comparingInt(w -> w.getPerson().getLocation().getZ());
                break;

            case "loc_name":
                comparator = Comparator.comparing(w -> w.getPerson().getLocation().getName());
                break;

            default:
                comparator = null;
        }

        return ascending ? comparator : comparator.reversed();
    }
    private static boolean compareValues(int value1, int value2, String operation) {
        switch (operation) {
            case "=":
                return value1 == value2;
            case "<":
                return value1 < value2;
            case ">":
                return value1 > value2;
            case ">=":
                return value1 >= value2;
            case "<=":
                return value1 <= value2;
            default:
                return false;
        }
    }
    private static <T extends Comparable<T>> boolean compareValues(T value1, T value2, String operation) {
        int comparisonResult = value1.compareTo(value2);
        switch (operation) {
            case "=":
                return comparisonResult == 0;
            case "<":
                return comparisonResult < 0;
            case ">":
                return comparisonResult > 0;
            case ">=":
                return comparisonResult >= 0;
            case "<=":
                return comparisonResult <= 0;
            default:
                return false;
        }
    }
}
