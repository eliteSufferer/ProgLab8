package common.functional;

import common.data.Coordinates;
import common.data.Person;
import common.data.Position;
import common.data.Status;

import java.time.ZonedDateTime;

public class WorkerPacket {
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Double salary;
    private Position position;
    private Status status;
    private Person person;

    public WorkerPacket(String name, Coordinates coordinates, ZonedDateTime creationDate, Double salary, Position position, Status status, Person person) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.position = position;
        this.status = status;
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Double getSalary() {
        return salary;
    }

    public Position getPosition() {
        return position;
    }

    public Status getStatus() {
        return status;
    }

    public Person getPerson() {
        return person;
    }
}
