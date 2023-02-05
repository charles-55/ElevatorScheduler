import java.util.HashMap;

/**
 * The Elevator Class.
 * Moves between floors based on the data sent from the floor to the scheduler.
 *
 * @author Nicholas Thibault
 * @version 1.0
 */
public class Elevator extends Thread {

    private int currentFloor;
    private boolean doorOpen;
    private boolean isMoving;
    private final Scheduler scheduler;
    private ElevatorCallEvent.Direction direction;
    private final HashMap<Integer, Boolean> buttonsAndLamps;

    /**
     * Initialize the elevator.
     * @param numOfFloors int, the number of floors to move between.
     * @param scheduler Scheduler, the scheduler to get the data from.
     */
    public Elevator(int numOfFloors, Scheduler scheduler) {
        this(numOfFloors, 1, scheduler);
    }

    /**
     * Initialize the elevator.
     * @param numOfFloors int, the number of floors to move between.
     * @param currentFloor int, the current floor the elevator is on.
     * @param scheduler Scheduler, the scheduler to get the data from.
     */
    public Elevator(int numOfFloors, int currentFloor, Scheduler scheduler) {

        this.currentFloor = currentFloor;
        this.scheduler = scheduler;

        scheduler.addElevator(this);

        doorOpen = false;
        isMoving = false;
        direction = ElevatorCallEvent.Direction.STANDBY;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);
    }

    /**
     * Get the current floor.
     * @return int, the current floor.
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    /**
     * Set the current floor.
     * @param currentFloor int, the current floor.
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * Get the state of the elevator door.
     * @return boolean, true if open, false otherwise.
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Set the state of the elevator door.
     * @param doorOpen boolean, true if open, false otherwise.
     */
    public void setDoorOpen(boolean doorOpen) {
        if(!isMoving) {
            this.doorOpen = doorOpen;
        }
    }

    /**
     * Get the state of the elevator.
     * @return boolean, true if moving, false otherwise.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Set the state of the elevator.
     * @param moving boolean, true if moving, false otherwise.
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    /**
     * Get the direction of the elevator.
     * @return ElevatorCallEvent.Direction, direction of the elevator.
     */
    public ElevatorCallEvent.Direction getDirection() {
        return direction;
    }

    /**
     * Set the direction of the elevator.
     * @param direction ElevatorCallEvent.Direction, direction of the elevator.
     */
    public void setDirection(ElevatorCallEvent.Direction direction) {
        this.direction = direction;
    }

    /**
     * Get the buttons and the lamps mapped to them.
     * @return HashMap<Integer, Boolean>, the buttons and the lamps mapped to them.
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     *
     */
    public void openCloseDoors() {
        if (!this.isMoving) {
            try {
                Thread.sleep(3000); //Arbitrary time for doors to open
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorOpen = true;

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorOpen = false;
        }
    }

    /**
     * Move the elevator to a particular floor.
     * @param targetFloor int, the floor to move to.
     */
    public synchronized void moveToFloor(int targetFloor, ElevatorCallEvent.Direction direction) {
        if (!(this.direction == direction)) {
            this.direction = direction;
        }

        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); //Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.isMoving = false;
        this.setCurrentFloor(targetFloor);
        this.openCloseDoors();
    }

    public void updateQueueDirection(ElevatorCallEvent.Direction direction) {
        //int nextFloor = scheduler.getFromQueue(this);
        //this.moveToFloor(nextFloor)

    }
        /*
        //Open Door Button
        if (**open button is pressed**) {
            if (!this.isMoving) {
                try {
                    Thread.sleep(3000); //Arbitrary time for doors to open
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.doorOpen = true;
            }
            break;
        }
        */

        /*
        //Close Door Button
        if (**closed button is pressed**) {
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
        */

    /**
     *
     */
    @Override
    public void run() {
        while(true) {
        }
    }
}
