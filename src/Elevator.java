import java.util.HashMap;

/**
 * The Elevator Class.
 */
public class Elevator extends Thread {

    //private int passengers;
    private int currentFloor;
    private boolean doorOpen;
    private boolean isMoving;
    private final Scheduler scheduler;
    private ElevatorCallEvent.Direction direction;
    //private Buttons button;
    private final HashMap<Integer, Boolean> buttonsAndLamps;

    /**
     *
     * @param numOfFloors
     * @param scheduler
     */
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
        this.scheduler = scheduler;

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

    public void setDoorOpen(boolean doorOpen) {
        if(!isMoving) {
            this.doorOpen = doorOpen;
        }
    }

    public boolean isMoving() {
        return isMoving;
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
     *
     * @param targetFloor
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


            stop();
        }
    }
}
