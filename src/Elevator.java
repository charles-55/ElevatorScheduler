/**
 * The Elevator Class.
 */
public class Elevator extends Thread {
    private int passengers;
    private int currentFloor;

    private Boolean doorsOpen;
    private Boolean isMoving;
    private Direction directionLamp;
    private enum Direction {UP, DOWN, STANDBY}
    private Buttons button;
    private enum Buttons {ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, CLOSE, OPEN}

    /**
     *
     * @param currentFloor
     */
    public Elevator(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getCurrentFloor() {
        return this.currentFloor;
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
            this.doorsOpen = true;

            try {
                Thread.sleep((long)(passengersIn + passengersOut) * 3000); //Arbitrary time for people to enter and get out of the car (3 seconds per person)
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.doorsOpen = false;
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
                    this.doorsOpen = true;
                }
                    break;
            case CLOSE:
                if (!this.isMoving) {
                    try {
                        Thread.sleep(3000); //Arbitrary time for doors to open
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.doorsOpen = false;
                }
                break;
        }
    }

    public void moveToFloor(int targetFloor) {
        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); //Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.isMoving = false;
        this.currentFloor = targetFloor;
    }
}
