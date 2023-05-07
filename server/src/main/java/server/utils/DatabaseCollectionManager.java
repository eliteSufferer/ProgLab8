package server.utils;

import common.data.*;
import common.exceptions.DatabaseHandlingException;
import common.exceptions.UniversalException;
import common.functional.User;
import common.functional.WorkerPacket;
import server.RunServer;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DatabaseCollectionManager {
    private final String SELECT_ALL_WORKERS = "SELECT * FROM " + DatabaseHandler.WORKER_TABLE;
    private final String SELECT_WORKERS_BY_ID = SELECT_ALL_WORKERS + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";

    private final String SELECT_WORKERS_BY_ID_AND_USER_ID = SELECT_WORKERS_BY_ID + " AND " +
            DatabaseHandler.WORKER_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_WORKER = "INSERT INTO " +
            DatabaseHandler.WORKER_TABLE + " (" +
            DatabaseHandler.WORKER_TABLE_NAME_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_CREATION_DATE_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_SALARY_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_POSITION_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_STATUS_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_PERSON_ID_COLUMN + ", " +
            DatabaseHandler.WORKER_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?::text," +
            "?::text, ?::integer, ?)";


    private final String DELETE_WORKER_BY_ID = "DELETE FROM " + DatabaseHandler.WORKER_TABLE +
            " WHERE " + DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";

    private final String UPDATE_WORKER_NAME_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_SALARY_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_SALARY_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_POSITION_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_POSITION_COLUMN + " = ?::text" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_STATUS_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_STATUS_COLUMN + " = ?::text" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";

//    private final String UPDATE_WORKER_PERSON_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
//            DatabaseHandler.WORKER_TABLE_PERSON_ID_COLUMN + " = ?::person_id" + " WHERE " +
//            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";

    // COORDINATES_TABLE
    private final String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseHandler.COORDINATES_TABLE;
    private final String SELECT_COORDINATES_BY_WORKER_ID = SELECT_ALL_COORDINATES +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_WORKER_ID_COLUMN + " = ?";
    private final String INSERT_COORDINATES = "INSERT INTO " +
            DatabaseHandler.COORDINATES_TABLE + " (" +
            DatabaseHandler.COORDINATES_TABLE_WORKER_ID_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + ") VALUES (?, ?, ?)";

    private final String UPDATE_COORDINATES_BY_WORKER_ID = "UPDATE " + DatabaseHandler.COORDINATES_TABLE + " SET " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + " = ?, " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.COORDINATES_TABLE_WORKER_ID_COLUMN + " = ?";

    //PERSON_TABLE
    private final String SELECT_ALL_PERSON = "SELECT * FROM " + DatabaseHandler.PERSON_TABLE;
    private final String SELECT_PERSON_BY_ID = SELECT_ALL_PERSON +
            " WHERE " + DatabaseHandler.PERSON_TABLE_ID_COLUMN + " = ?";
    private final String INSERT_PERSON = "INSERT INTO " +
            DatabaseHandler.PERSON_TABLE + " (" +
            DatabaseHandler.PERSON_TABLE_BIRTHDAY_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_PASSPORT_COLUMN + ", " +
            DatabaseHandler.PERSON_TABLE_LOCATION_ID_COLUMN + ") VALUES (?, ?, ?, ?::integer)";
    private final String UPDATE_PERSON_BY_ID = "UPDATE " + DatabaseHandler.PERSON_TABLE + " SET " +
            DatabaseHandler.PERSON_TABLE_BIRTHDAY_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_PASSPORT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_LOCATION_ID_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.PERSON_TABLE_ID_COLUMN + " = ?";
    //LOCATION_TABLE
    private final String SELECT_ALL_LOCATION = "SELECT * FROM " + DatabaseHandler.LOCATION_TABLE;
    private final String SELECT_LOCATION_BY_ID = SELECT_ALL_LOCATION +
            " WHERE " + DatabaseHandler.LOCATION_TABLE_ID_COLUMN + " = ?";
    private final String INSERT_LOCATION = "INSERT INTO " +
            DatabaseHandler.LOCATION_TABLE + " (" +
            DatabaseHandler.LOCATION_TABLE_X_COLUMN + ", " +
            DatabaseHandler.LOCATION_TABLE_Y_COLUMN + ", " +
            DatabaseHandler.LOCATION_TABLE_Z_COLUMN + ", " +
            DatabaseHandler.LOCATION_TABLE_NAME_COLUMN + ") VALUES (?, ?, ?, ?)";
    private final String UPDATE_LOCATION_BY_ID = "UPDATE " + DatabaseHandler.LOCATION_TABLE + " SET " +
            DatabaseHandler.LOCATION_TABLE_X_COLUMN + " = ?, " +
            DatabaseHandler.LOCATION_TABLE_Y_COLUMN + " = ?, " +
            DatabaseHandler.LOCATION_TABLE_Z_COLUMN + " = ?, " +
            DatabaseHandler.LOCATION_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.LOCATION_TABLE_ID_COLUMN + " = ?";
    private DatabaseHandler databaseHandler;
    private DatabaseUser databaseUser;

    public DatabaseCollectionManager(DatabaseHandler databaseHandler, DatabaseUser databaseUser) {
        this.databaseHandler = databaseHandler;
        this.databaseUser = databaseUser;
    }
    private Worker createWorker(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(DatabaseHandler.WORKER_TABLE_ID_COLUMN);
        RunServer.logger.info(id);
        String name = resultSet.getString(DatabaseHandler.WORKER_TABLE_NAME_COLUMN);
        Coordinates coordinates = getCoordinates(id);
        ZonedDateTime creationDate = ((resultSet.getTimestamp(DatabaseHandler.WORKER_TABLE_CREATION_DATE_COLUMN)).toLocalDateTime().atZone(ZoneId.systemDefault()));
        Double salary = resultSet.getDouble(DatabaseHandler.WORKER_TABLE_SALARY_COLUMN);
        Position position = Position.valueOf(resultSet.getString(DatabaseHandler.WORKER_TABLE_POSITION_COLUMN));
        Status status = Status.valueOf(resultSet.getString(DatabaseHandler.WORKER_TABLE_STATUS_COLUMN));
        Person person = getPerson(id);
        User owner = databaseUser.getUserById(resultSet.getLong(DatabaseHandler.WORKER_TABLE_USER_ID_COLUMN));
        return new Worker(id,
                name,
                coordinates,
                creationDate,
                salary,
                position,
                status,
                person,
                owner
        );
    }
    private Coordinates getCoordinates(long workerId) throws SQLException{
        Coordinates coordinates;
        PreparedStatement preparedSelectCoordinatesByWorkerIdStatement = null;
        try {
            preparedSelectCoordinatesByWorkerIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_COORDINATES_BY_WORKER_ID, false);
            preparedSelectCoordinatesByWorkerIdStatement.setLong(1, workerId);
            ResultSet resultSet = preparedSelectCoordinatesByWorkerIdStatement.executeQuery();
            RunServer.logger.info("Выполнен запрос SELECT_COORDINATES_BY_WORKER_ID.");
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getInt(DatabaseHandler.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getInt(DatabaseHandler.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_Worker_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectCoordinatesByWorkerIdStatement);
        }
        return coordinates;
    }
    private int getPersonIdByWorkerId(int workerId) throws SQLException{
        int personId;
        PreparedStatement preparedSelectWorkerByIdStatement = null;
        try {
            preparedSelectWorkerByIdStatement = databaseHandler.getPreparedStatement(SELECT_WORKERS_BY_ID, false);
            preparedSelectWorkerByIdStatement.setLong(1, workerId);
            ResultSet resultSet = preparedSelectWorkerByIdStatement.executeQuery();
            RunServer.logger.info("Выполнен запрос SELECT_WORKER_BY_ID.");
            if (resultSet.next()) {
                personId = resultSet.getInt(DatabaseHandler.WORKER_TABLE_PERSON_ID_COLUMN);
            } else throw new SQLException();
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_WORKER_BY_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectWorkerByIdStatement);
        }
        return personId;
    }
    private Person getPerson(long workerId) throws SQLException{
        Person person;
        PreparedStatement preparedSelectPersonByWorkerIdStatement = null;
        try {
            preparedSelectPersonByWorkerIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_PERSON_BY_ID, false);
            preparedSelectPersonByWorkerIdStatement.setLong(1, workerId);
            ResultSet resultSet = preparedSelectPersonByWorkerIdStatement.executeQuery();
            RunServer.logger.info("Выполнен запрос SELECT_PERSON_BY_ID.");
            if (resultSet.next()) {
                person = new Person(
                        resultSet.getTimestamp(DatabaseHandler.PERSON_TABLE_BIRTHDAY_COLUMN).toLocalDateTime(),
                        resultSet.getLong(DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN),
                        resultSet.getString(DatabaseHandler.PERSON_TABLE_PASSPORT_COLUMN),
                        getLocation(workerId)
                );
            } else{
                throw new SQLException();
            }
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_PERSON_BY_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectPersonByWorkerIdStatement);
        }
        return person;
    }
    private Location getLocation(long workerId) throws SQLException{
        Location location;
        PreparedStatement preparedSelectLocationByWorkerIdStatement = null;
        try {
            preparedSelectLocationByWorkerIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_LOCATION_BY_ID, false);
            preparedSelectLocationByWorkerIdStatement.setLong(1, workerId);
            ResultSet resultSet = preparedSelectLocationByWorkerIdStatement.executeQuery();
            RunServer.logger.info("Выполнен запрос SELECT_PERSON_BY_ID.");
            if (resultSet.next()) {
                location = new Location(
                        resultSet.getFloat(DatabaseHandler.LOCATION_TABLE_X_COLUMN),
                        resultSet.getLong(DatabaseHandler.LOCATION_TABLE_Y_COLUMN),
                        resultSet.getInt(DatabaseHandler.LOCATION_TABLE_Z_COLUMN),
                        resultSet.getString(DatabaseHandler.LOCATION_TABLE_NAME_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса getLocation!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectLocationByWorkerIdStatement);
        }
        return location;
    }
    public Worker insertWorker(WorkerPacket workerPacket, User user) throws DatabaseHandlingException {
        Worker worker;
        PreparedStatement preparedInsertWorkerStatement = null;
        PreparedStatement preparedInsertCoordinatesStatement = null;
        PreparedStatement preparedInsertPersonStatement = null;
        PreparedStatement preparedInsertLocationStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            ZonedDateTime creationTime = ZonedDateTime.now();

            preparedInsertWorkerStatement = databaseHandler.getPreparedStatement(INSERT_WORKER, true);
            preparedInsertCoordinatesStatement = databaseHandler.getPreparedStatement(INSERT_COORDINATES, true);
            preparedInsertPersonStatement = databaseHandler.getPreparedStatement(INSERT_PERSON, true);
            preparedInsertLocationStatement = databaseHandler.getPreparedStatement(INSERT_LOCATION, true);

            preparedInsertLocationStatement.setFloat(1, workerPacket.getPerson().getLocation().getX());
            preparedInsertLocationStatement.setLong(2, workerPacket.getPerson().getLocation().getY());
            preparedInsertLocationStatement.setInt(3, workerPacket.getPerson().getLocation().getZ());
            preparedInsertLocationStatement.setString(4, workerPacket.getPerson().getLocation().getName());
            RunServer.logger.info(preparedInsertLocationStatement.getGeneratedKeys());
            if (preparedInsertLocationStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedLocationKeys = preparedInsertLocationStatement.getGeneratedKeys();
            int locationId;
            if (generatedLocationKeys.next()) {
                locationId = generatedLocationKeys.getInt(1);
            } else throw new SQLException();
            RunServer.logger.info("Выполнен запрос INSERT_LOCATION.");



            preparedInsertPersonStatement.setTimestamp(1, Timestamp.valueOf(workerPacket.getPerson().getBirthday()));
            preparedInsertPersonStatement.setLong(2, workerPacket.getPerson().getHeight());
            preparedInsertPersonStatement.setString(3, workerPacket.getPerson().getPassportID());
            preparedInsertPersonStatement.setInt(4, locationId);


            if (preparedInsertPersonStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedPersonKeys = preparedInsertPersonStatement.getGeneratedKeys();
            int personId;
            if (generatedPersonKeys.next()) {
                personId = generatedPersonKeys.getInt(1);
            } else throw new SQLException();
            RunServer.logger.info("Выполнен запрос INSERT_PERSON.");

            preparedInsertWorkerStatement.setString(1, workerPacket.getName());
            preparedInsertWorkerStatement.setTimestamp(2, Timestamp.valueOf(creationTime.toLocalDateTime()));
            preparedInsertWorkerStatement.setDouble(3, workerPacket.getSalary());
            preparedInsertWorkerStatement.setString(4, workerPacket.getPosition().toString());
            RunServer.logger.info(workerPacket.getPosition().toString());
            preparedInsertWorkerStatement.setString(5, workerPacket.getStatus().toString());
            preparedInsertWorkerStatement.setLong(6, personId);
            preparedInsertWorkerStatement.setLong(7, databaseUser.getUserIdByUsername(user));
            if (preparedInsertWorkerStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedWorkerKeys = preparedInsertWorkerStatement.getGeneratedKeys();
            int workerId;
            if (generatedWorkerKeys.next()) {
                workerId = generatedWorkerKeys.getInt(1);
            } else throw new SQLException();
            RunServer.logger.info("Выполнен запрос INSERT_WORKER.");

            preparedInsertCoordinatesStatement.setInt(1, workerId);
            preparedInsertCoordinatesStatement.setDouble(2, workerPacket.getCoordinates().getX());
            preparedInsertCoordinatesStatement.setFloat(3, workerPacket.getCoordinates().getY());
            if (preparedInsertCoordinatesStatement.executeUpdate() == 0) throw new SQLException();
            RunServer.logger.info("Выполнен запрос INSERT_COORDINATES.");

            worker = new Worker(
                    workerId,
                    workerPacket.getName(),
                    workerPacket.getCoordinates(),
                    creationTime,
                    workerPacket.getSalary(),
                    workerPacket.getPosition(),
                    workerPacket.getStatus(),
                    workerPacket.getPerson(),
                    user
            );

            databaseHandler.commit();
            return worker;
        } catch (SQLException | UniversalException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении группы запросов на добавление нового объекта!");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedInsertWorkerStatement);
            databaseHandler.closePreparedStatement(preparedInsertCoordinatesStatement);
            databaseHandler.closePreparedStatement(preparedInsertPersonStatement);
            databaseHandler.closePreparedStatement(preparedInsertLocationStatement);
            databaseHandler.setNormalMode();
        }
    }
    public void updateWorkerById(int workerId,  WorkerPacket workerPacket) throws DatabaseHandlingException {
        PreparedStatement preparedUpdateWorkerNameByIdStatement = null;
        PreparedStatement preparedUpdateWorkerSalaryByIdStatement = null;
        PreparedStatement preparedUpdateWorkerPositionByIdStatement = null;
        PreparedStatement preparedUpdateWorkerStatusByIdStatement = null;
        PreparedStatement preparedUpdateCoordinatesByWorkerIdStatement = null;
        PreparedStatement preparedUpdatePersonByIdStatement = null;
        PreparedStatement preparedUpdateLocationBYIDStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedUpdateWorkerNameByIdStatement = databaseHandler.getPreparedStatement(UPDATE_WORKER_NAME_BY_ID, false);
            preparedUpdateCoordinatesByWorkerIdStatement = databaseHandler.getPreparedStatement(UPDATE_COORDINATES_BY_WORKER_ID, false);
            preparedUpdateWorkerSalaryByIdStatement = databaseHandler.getPreparedStatement(UPDATE_WORKER_SALARY_BY_ID, false);
            preparedUpdateWorkerPositionByIdStatement = databaseHandler.getPreparedStatement(UPDATE_WORKER_POSITION_BY_ID, false);
            preparedUpdateWorkerStatusByIdStatement = databaseHandler.getPreparedStatement(UPDATE_WORKER_STATUS_BY_ID, false);
            preparedUpdatePersonByIdStatement = databaseHandler.getPreparedStatement(UPDATE_PERSON_BY_ID, false);
            preparedUpdateLocationBYIDStatement = databaseHandler.getPreparedStatement(UPDATE_LOCATION_BY_ID, false);
            if (workerPacket.getName() != null) {
                preparedUpdateWorkerNameByIdStatement.setString(1, workerPacket.getName());
                preparedUpdateWorkerNameByIdStatement.setInt(2, workerId);
                if (preparedUpdateWorkerNameByIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_WORKER_NAME_BY_ID.");
            }
            if (workerPacket.getCoordinates() != null) {
                preparedUpdateCoordinatesByWorkerIdStatement.setDouble(1, workerPacket.getCoordinates().getX());
                preparedUpdateCoordinatesByWorkerIdStatement.setFloat(2, workerPacket.getCoordinates().getY());
                preparedUpdateCoordinatesByWorkerIdStatement.setLong(3, workerId);
                if (preparedUpdateCoordinatesByWorkerIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_COORDINATES_BY_Worker_ID.");
            }
            if (workerPacket.getSalary() != null) {
                preparedUpdateWorkerSalaryByIdStatement.setDouble(1, workerPacket.getSalary());
                preparedUpdateWorkerSalaryByIdStatement.setInt(2, workerId);
                if (preparedUpdateWorkerSalaryByIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_WORKER_SALARY_BY_ID.");
            }
            if (workerPacket.getPosition() != null) {
                preparedUpdateWorkerPositionByIdStatement.setString(1, workerPacket.getPosition().toString());
                preparedUpdateWorkerPositionByIdStatement.setLong(2, workerId);
                if (preparedUpdateWorkerPositionByIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_WORKER_POSITION_BY_ID.");
            }
            if (workerPacket.getStatus() != null) {
                preparedUpdateWorkerStatusByIdStatement.setString(1, workerPacket.getStatus().toString());
                preparedUpdateWorkerStatusByIdStatement.setLong(2, workerId);
                if (preparedUpdateWorkerStatusByIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_WORKER_STATUS_BY_ID.");
            }
            if (workerPacket.getPerson() != null) {
                preparedUpdatePersonByIdStatement.setTimestamp(1, Timestamp.valueOf(workerPacket.getPerson().getBirthday()));
                preparedUpdatePersonByIdStatement.setLong(2, workerPacket.getPerson().getHeight());
                preparedUpdatePersonByIdStatement.setString(3, workerPacket.getPerson().getPassportID());
                preparedUpdatePersonByIdStatement.setInt(4, workerId);
                preparedUpdatePersonByIdStatement.setInt(5, workerId);
                if (preparedUpdatePersonByIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_PERSON_BY_ID.");
            }
            if (workerPacket.getPerson().getLocation() != null) {
                preparedUpdateLocationBYIDStatement.setFloat(1, workerPacket.getPerson().getLocation().getX());
                preparedUpdateLocationBYIDStatement.setLong(2, workerPacket.getPerson().getLocation().getY());
                preparedUpdateLocationBYIDStatement.setInt(3, workerPacket.getPerson().getLocation().getZ());
                preparedUpdateLocationBYIDStatement.setString(4, workerPacket.getPerson().getLocation().getName());
                preparedUpdateLocationBYIDStatement.setLong(5, workerId);
                if (preparedUpdateCoordinatesByWorkerIdStatement.executeUpdate() == 0) throw new SQLException();
                RunServer.logger.info("Выполнен запрос UPDATE_COORDINATES_BY_Worker_ID.");
            }

            databaseHandler.commit();
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении группы запросов на обновление объекта!");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedUpdateWorkerNameByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateWorkerSalaryByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateWorkerPositionByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateWorkerStatusByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateCoordinatesByWorkerIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdatePersonByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateLocationBYIDStatement);
            databaseHandler.setNormalMode();
        }
    }
    public void deleteWorkerById(int workerId) throws DatabaseHandlingException {
        PreparedStatement preparedDeleteWorkerByIdStatement = null;
        try {
            preparedDeleteWorkerByIdStatement = databaseHandler.getPreparedStatement(DELETE_WORKER_BY_ID, false);
            preparedDeleteWorkerByIdStatement.setLong(1, workerId);
            if (preparedDeleteWorkerByIdStatement.executeUpdate() == 0) throw  new DatabaseHandlingException();
            RunServer.logger.info("Выполнен запрос DELETE_WORKER_BY_ID.");

        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса DELETE_WORKER_BY_ID!");
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedDeleteWorkerByIdStatement);
        }
    }

    public boolean checkWorkerUserId(int workerId, User user) throws DatabaseHandlingException {
        PreparedStatement preparedSelectWorkerByIdAndUserIdStatement = null;
        try {
            preparedSelectWorkerByIdAndUserIdStatement = databaseHandler.getPreparedStatement(SELECT_WORKERS_BY_ID_AND_USER_ID, false);
            preparedSelectWorkerByIdAndUserIdStatement.setLong(1, workerId);
            preparedSelectWorkerByIdAndUserIdStatement.setLong(2, databaseUser.getUserIdByUsername(user));
            ResultSet resultSet = preparedSelectWorkerByIdAndUserIdStatement.executeQuery();
            RunServer.logger.info("Выполнен запрос SELECT_WORKER_BY_ID_AND_USER_ID.");
            return resultSet.next();
        } catch (SQLException exception) {

            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_WORKER_BY_ID_AND_USER_ID!");
            throw new DatabaseHandlingException();
        } catch (UniversalException e) {

            throw new RuntimeException(e);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectWorkerByIdAndUserIdStatement);
        }
    }
    public ArrayList<Worker> getCollection() throws DatabaseHandlingException {
        ArrayList<Worker> workersList = new ArrayList<>();
        PreparedStatement preparedSelectAllStatement = null;
        try {
            preparedSelectAllStatement = databaseHandler.getPreparedStatement(SELECT_ALL_WORKERS, false);
            ResultSet resultSet = preparedSelectAllStatement.executeQuery();
            RunServer.logger.info(resultSet.toString());
            while (resultSet.next()) {
                Worker worker = createWorker(resultSet);
                System.out.println(worker);
                workersList.add(worker);
            }
        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectAllStatement);
        }
        return workersList;
    }

    public void clearCollection(Worker worker) throws DatabaseHandlingException {
        deleteWorkerById(worker.getId());
    }


}