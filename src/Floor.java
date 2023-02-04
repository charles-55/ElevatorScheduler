/**
 * The Floor Class.
 * Represents current status of floor.
 * Takes the information in table and processes it.
 *
 * @author Sabah Samwatin
 * @version 1.0
 */
public class Floor extends Thread {
    // private ElevatorCallEvent.Direction buttonDirection;
    private final int floorNumber;
    private Hashmap<ElevatorCallEvent.Direction, Boolean> buttonsAndLamps;
    private boolean lampOn; // checks if floor is ready to receive an elevator
    private final Scheduler scheduler;

    /**
     * Constructor for the floor class.
     */
    public Floor(int floorNumber, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.floorNumber = floorNumber;
        buttonsAndLamps = new HashMap<>();
        buttonsAndLamps.put(ElevatorCallEvent.Direction.UP, false);
        buttonsAndLamps.put(ElevatorCallEvent.Direction.DOWN, false);
    }

    public ElevatorCallEvent.Direction getButtonDirection() {
        return buttonDirection;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isLampOn() {
        return lampOn;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setButtonDirection(ElevatorCallEvent.Direction buttonDirection) {
        this.buttonDirection = buttonDirection;
    }

    public void setLampOn(boolean lampOn) {
        this.lampOn = lampOn;
    }

    /**
     * This is the section for running with threads.
     */
    public void run() {
        FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler);
        floorSubsystem.parseData("InputTable.txt"); // edit this to specify the file to read
    }
}
