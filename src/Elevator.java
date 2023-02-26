import java.util.HashMap;

/**
 * The Elevator Class.
 * Moves between floors based on the data sent from the floor to the scheduler.
 *
 * @author Nicholas Thibault 101172413
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
     * Constructor that sets the current floor to 1.
     * @param numOfFloors int number of floors contained in the building
     * @param scheduler Scheduler used for the elevator
     */
    public Elevator(int numOfFloors, Scheduler scheduler) {
        this(numOfFloors, 1, scheduler);
    }

    /**
     * Constructor that sets the number of floors in the building, the initial location of the elevator and its scheduler.
     * @param numOfFloors int number of floors contained in the building
     * @param currentFloor int current floor number of the elevator
     * @param scheduler Scheduler used for the elevator
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
     * Getter method for the currentFloor attribute.
     * @return int currentFloor the current floor
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    /**
     * Setter method for the currentFloor attribute.
     * @param currentFloor int the current floor
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * method for the current state of the doors
     * @return boolean doorOpen (open if true, closed if false)
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Setter method to close the doors
     * @param doorOpen boolean value for state of the doors
     */
    public void setDoorOpen(boolean doorOpen) {
        if(!isMoving) {
            this.doorOpen = doorOpen;
        }
    }

    /**
     * method for the current state of the movement of the elevator
     * @return boolean isMoving (moving if true, stopped if false)
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
     * Getter method for the current direction of the elevator.
     * @return ElevatorCallEvent.Direction direction of the elevator (up, down, standby)
     */
    public ElevatorCallEvent.Direction getDirection() {
        return direction;
    }

    /**
     * Setter method for the current direction of the elevator.
     * @param direction ElevatorCallEvent.Direction direction of the elevator
     */
    public void setDirection(ElevatorCallEvent.Direction direction) {
        this.direction = direction;
    }

    /**
     * Getter method for the HashMap of buttons and lamps related to each button.
     * @return HashMap<Integer, Boolean> buttonsAndLamps hashmap of the floor number buttons and state of the lamps (on if true, off if false)
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     * method to open, disembark / embark passengers, then close the doors.
     */
    public void openDoors() {
        if (!this.isMoving && !doorOpen) {
            try {
                Thread.sleep(3000); // Arbitrary time for doors to open // implement open door using motors
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.doorOpen = true;
        }
    }

    /**
     * Close the door of the elevator.
     */
    public void closeDoors() {
        if(!this.isMoving && doorOpen) {
            try {
                Thread.sleep(3000); // Arbitrary time for doors to close // implement close door using motors
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorOpen = false;
        }
    }

    /**
     * Moves the current elevator to target floor.
     * @param targetFloor int target floor number
     * @param direction ElevatorCallEvent.Direction direction of target floor
     */
    public synchronized void moveToFloor(int targetFloor, ElevatorCallEvent.Direction direction) {
        this.direction = direction;
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); // Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Moved from " + currentFloor + " to " + targetFloor);
        this.isMoving = false;
        this.setCurrentFloor(targetFloor);
        this.openDoors();
        notifyAll();
    }

    public void put(int i, boolean b) {
        buttonsAndLamps.put(i, b);
    }

    /**
     * method that prints the state of movement of the elevator and the state of its doors.
     */
    public void printState() throws Exception {
        //TODO: Once we do multiple instances of elevators, identify which elevator it is in the print.
        // Also, send error when doors are open but elevator is moving.
        if (this.isDoorOpen()) {
            System.out.println("The elevator's doors are open.");
        } else {
            System.out.println("The elevator's doors are closed.");
        }

        if (this.isMoving()) {
            if (this.getDirection() == ElevatorCallEvent.Direction.UP) {
                System.out.println("The elevator is moving up.");
            } else if (this.getDirection() == ElevatorCallEvent.Direction.DOWN) {
                System.out.println("The elevator is moving down.");
            } else {
                throw new Exception("Error: The elevator is moving but its direction is STANDBY.");
            }
        } else {
            if (this.getDirection() == ElevatorCallEvent.Direction.STANDBY) {
                System.out.println("The elevator is stopped.");
            } else {
                throw new Exception("Error: The elevator is not moving but its direction is not STANDBY.");
            }

        }
    }

    /**
     * This is the section for running with threads.
     */
    @Override
    public void run() {
        while(true) {
            scheduler.getFromQueue(this);
            for(int destinationFloor : buttonsAndLamps.keySet()) {
                if(buttonsAndLamps.get(destinationFloor)) {
                    moveToFloor(destinationFloor, direction);
                    buttonsAndLamps.put(destinationFloor, false);
                }
            }
        }
    }
}
