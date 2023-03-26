package common.functional;

import common.data.Coordinates;
import common.data.Person;
import common.data.Position;
import common.data.Status;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class WorkerPacket implements Serializable {
    private String name;
    private Coordinates coordinates;
    private Double salary;
    private Position position;
    private Status status;
    private Person person;

    public WorkerPacket(String name, Coordinates coordinates, Double salary, Position position, Status status, Person person) {
        this.name = name;
        this.coordinates = coordinates;
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
