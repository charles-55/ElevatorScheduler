import java.time.LocalTime;

/**
 * The ElevatorCallEvent Class.
 * Creates an event to call an elevator to a particular floor.
 *
 * @author Osamudiamen Nwoko 101152520
 * @version 1.0
 */
public class ElevatorCallEvent {

    private final LocalTime time;
    private final int floorNumber;
    private final Direction direction;
    private final int elevatorNumber;
    public enum Direction {UP, DOWN, STANDBY}

    /**
     * Initialize the event.
     * @param time LocalTime, the time the elevator is called.
     * @param floorNumber int, the floor number.
     * @param direction Direction, the direction the elevator is to go.
     * @param elevatorNumber int, the elevator number.
     */
    public ElevatorCallEvent(LocalTime time, int floorNumber, Direction direction, int elevatorNumber) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.elevatorNumber = elevatorNumber;
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
     * @return int, the number of the elevator that was called.
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }
}
