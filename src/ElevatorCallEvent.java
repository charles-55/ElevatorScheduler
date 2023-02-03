import java.time.LocalTime;

/**
 * The ElevatorCallEvent Class.
 * Creates an event to call an elevator to a particular floor and assign its destination.
 *
 * @author Osamudiamen Nwoko 101152520
 * @version 1.0
 */
public class ElevatorCallEvent {

    private final LocalTime time;
    private final int floorNumber;
    private final Direction direction;
    private final int destinationFloor;
    public enum Direction {UP, DOWN, STANDBY}

    /**
     * Initialize the event.
     * @param time LocalTime, the time the elevator is called.
     * @param floorNumber int, the floor number.
     * @param direction Direction, the direction the elevator is to go.
     * @param destinationFloor int, the destination floor.
     */
    public ElevatorCallEvent(LocalTime time, int floorNumber, Direction direction, int destinationFloor) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.destinationFloor = destinationFloor;
    }

    /**
     * Get the time the elevator was called.
     * @return LocalTime, the time the elevator was called.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Get the floor number the elevator was called from.
     * @return int, the floor number the elevator was called from.
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Get the direction the elevator is supposed to go in.
     * @return Direction, the direction the elevator is supposed to go in.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Get the number of the elevator that was called.
     * @return int, the destination floor of the elevator.
     */
    public int getDestinationFloor() {
        return destinationFloor;
    }
}
