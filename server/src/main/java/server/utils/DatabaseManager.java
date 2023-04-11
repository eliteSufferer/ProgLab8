package server.utils;

import common.data.*;
import common.functional.User;
import common.functional.WorkerPacket;
import server.RunServer;
import server.Server;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatabaseManager {
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
            DatabaseHandler.WORKER_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?::position," +
            "?::status, ?::person, ?)";
    private final String DELETE_WORKER_BY_ID = "DELETE FROM " + DatabaseHandler.WORKER_TABLE +
            " WHERE " + DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_NAME_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_SALARY_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_SALARY_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_POSITION_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_POSITION_COLUMN + " = ?::position" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_STATUS_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_STATUS_COLUMN + " = ?::status" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_WORKER_PERSON_BY_ID = "UPDATE " + DatabaseHandler.WORKER_TABLE + " SET " +
            DatabaseHandler.WORKER_TABLE_PERSON_ID_COLUMN + " = ?::person_id" + " WHERE " +
            DatabaseHandler.WORKER_TABLE_ID_COLUMN + " = ?";

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
            DatabaseHandler.PERSON_TABLE_LOCATION_ID_COLUMN + ") VALUES (?, ?, ?, ?::location)";
    private final String UPDATE_PERSON_BY_ID = "UPDATE " + DatabaseHandler.PERSON_TABLE + " SET " +
            DatabaseHandler.PERSON_TABLE_BIRTHDAY_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_HEIGHT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_PASSPORT_COLUMN + " = ?, " +
            DatabaseHandler.PERSON_TABLE_LOCATION_ID_COLUMN + " = ?::location_id" + " WHERE " + //????
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

    public DatabaseManager(DatabaseHandler databaseHandler, DatabaseUser databaseUser) {
        this.databaseHandler = databaseHandler;
        this.databaseUser = databaseUser;
    }
    private Worker createWorker(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(DatabaseHandler.WORKER_TABLE_ID_COLUMN);
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
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_MARINE_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectCoordinatesByWorkerIdStatement);
        }
        return coordinates;
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
            } else throw new SQLException();
        } catch (SQLException exception) {
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_MARINE_ID!");
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
            RunServer.logger.error("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_MARINE_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectLocationByWorkerIdStatement);
        }
        return location;
    }
    public Worker insertMarine(WorkerPacket marineRaw, User user) throws DatabaseHandlingException {
        // TODO: Если делаем орден уникальным, тут че-то много всего менять
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

            preparedInsertChapterStatement.setString(1, marineRaw.getChapter().getName());
            preparedInsertChapterStatement.setLong(2, marineRaw.getChapter().getMarinesCount());
            if (preparedInsertChapterStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedChapterKeys = preparedInsertChapterStatement.getGeneratedKeys();
            long chapterId;
            if (generatedChapterKeys.next()) {
                chapterId = generatedChapterKeys.getLong(1);
            } else throw new SQLException();
            App.logger.info("Выполнен запрос INSERT_CHAPTER.");

            preparedInsertMarineStatement.setString(1, marineRaw.getName());
            preparedInsertMarineStatement.setTimestamp(2, Timestamp.valueOf(creationTime));
            preparedInsertMarineStatement.setDouble(3, marineRaw.getHealth());
            preparedInsertMarineStatement.setString(4, marineRaw.getCategory().toString());
            preparedInsertMarineStatement.setString(5, marineRaw.getWeaponType().toString());
            preparedInsertMarineStatement.setString(6, marineRaw.getMeleeWeapon().toString());
            preparedInsertMarineStatement.setLong(7, chapterId);
            preparedInsertMarineStatement.setLong(8, databaseUserManager.getUserIdByUsername(user));
            if (preparedInsertMarineStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedMarineKeys = preparedInsertMarineStatement.getGeneratedKeys();
            long spaceMarineId;
            if (generatedMarineKeys.next()) {
                spaceMarineId = generatedMarineKeys.getLong(1);
            } else throw new SQLException();
            App.logger.info("Выполнен запрос INSERT_MARINE.");

            preparedInsertCoordinatesStatement.setLong(1, spaceMarineId);
            preparedInsertCoordinatesStatement.setDouble(2, marineRaw.getCoordinates().getX());
            preparedInsertCoordinatesStatement.setFloat(3, marineRaw.getCoordinates().getY());
            if (preparedInsertCoordinatesStatement.executeUpdate() == 0) throw new SQLException();
            App.logger.info("Выполнен запрос INSERT_COORDINATES.");

            marine = new SpaceMarine(
                    spaceMarineId,
                    marineRaw.getName(),
                    marineRaw.getCoordinates(),
                    creationTime,
                    marineRaw.getHealth(),
                    marineRaw.getCategory(),
                    marineRaw.getWeaponType(),
                    marineRaw.getMeleeWeapon(),
                    marineRaw.getChapter(),
                    user
            );

            databaseHandler.commit();
            return marine;
        } catch (SQLException exception) {
            App.logger.error("Произошла ошибка при выполнении группы запросов на добавление нового объекта!");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedInsertMarineStatement);
            databaseHandler.closePreparedStatement(preparedInsertCoordinatesStatement);
            databaseHandler.closePreparedStatement(preparedInsertChapterStatement);
            databaseHandler.setNormalMode();
        }
    }


}