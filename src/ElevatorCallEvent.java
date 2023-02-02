import java.time.LocalTime;

public class ElevatorCallEvent {

    private final LocalTime time;
    private final int floorNumber;
    private final Direction direction;
    private final int elevatorNumber;
    public enum Direction {UP, DOWN, STANDBY}

    public ElevatorCallEvent(LocalTime time, int floorNumber, Direction direction, int elevatorNumber) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.elevatorNumber = elevatorNumber;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getElevatorNumber() {
        return elevatorNumber;
    }
}
