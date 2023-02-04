import java.util.HashMap;

/**
 * The Elevator Class.
 */
public class Elevator extends Thread {

    //private int passengers;
    private int currentFloor;
    private boolean doorOpen;
    private boolean isMoving;
    private ElevatorCallEvent.Direction direction;
    //private Buttons button;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private enum Buttons {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, CLOSE, OPEN}

    public Elevator(int numOfFloors, Scheduler scheduler) {
        this(numOfFloors, 1, scheduler);
    }

    /**
     *
     * @param numOfFloors
     * @param currentFloor
     * @param scheduler
     */
    public Elevator(int numOfFloors, int currentFloor, Scheduler scheduler) {
        this.currentFloor = currentFloor;
        scheduler.addElevator(this);

        doorOpen = false;
        isMoving = false;
        direction = ElevatorCallEvent.Direction.STANDBY;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public boolean setDoorOpen(boolean doorOpen) {
        if(isMoving)
            return false;
        this.doorOpen = doorOpen;
        return true;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean setMoving(boolean moving) {
        if(doorOpen)
            return false;
        isMoving = moving;
        return true;
    }

    public ElevatorCallEvent.Direction getDirection() {
        return direction;
    }

    public void setDirection(ElevatorCallEvent.Direction direction) {
        this.direction = direction;
    }

    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     *
     * @param passengersIn
     * @param passengersOut
     */
    public void openCloseDoors(int passengersIn, int passengersOut) {
        if (!this.isMoving) {
            try {
                Thread.sleep(3000); //Arbitrary time for doors to open
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorOpen = true;

            try {
                Thread.sleep((long)(passengersIn + passengersOut) * 3000); //Arbitrary time for people to enter and get out of the car (3 seconds per person)
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorOpen = false;
        }
    }

    /**
     *
     * @param buttonPressed
     */
    public void pressButton(Buttons buttonPressed) {
        switch(buttonPressed) {
            case ONE:
                this.moveToFloor(1);
                break;
            case TWO:
                this.moveToFloor(2);
                break;
            case THREE:
                this.moveToFloor(3);
                break;
            case FOUR:
                this.moveToFloor(4);
                break;
            case FIVE:
                this.moveToFloor(5);
                break;
            case SIX:
                this.moveToFloor(6);
                break;
            case SEVEN:
                this.moveToFloor(7);
                break;
            case OPEN:
                if (!this.isMoving) {
                    try {
                        Thread.sleep(3000); //Arbitrary time for doors to open
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.doorOpen = true;
                }
                    break;
            case CLOSE:
                if (!this.isMoving) {
                    try {
                        Thread.sleep(3000); //Arbitrary time for doors to open
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.doorOpen = false;
                }
                break;
        }
    }

    public void moveToFloor(int targetFloor) {
        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); //Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.isMoving = false;
        this.currentFloor = targetFloor;
        // open door
    }
}
