/**
 * The Floor Class.
 * Represents current status of floor.
 * Takes the information in table and processes it.
 *
 * @author Sabah Samwatin
 * @version 1.0
 */
public class Floor extends Thread {
    private ElevatorCallEvent.Direction buttonDirection;
    private final int floorNumber;
    private boolean lampOn; // checks if floor is ready to receive an elevator
    private final Scheduler scheduler;
    public static final int NUM_OF_FLOORS = 5; // edit this to change the number of floors in the building.

    /**
     * Constructor for the floor class.
     */
    public Floor(int FloorNumber, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.floorNumber = FloorNumber;
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
        floorSubsystem.parseData(""); // edit this to specify the file to read
//        while(true) {
//            if (this.scheduler.getFloorQueue().size() < 100000) {
//                System.out.println("Floor Queue is Empty");
//
//                try {
//                    scheduler.getFloorQueue().wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            this.recentPress = scheduler.getFloorQueue().get(0);
//            System.out.println("Floor has received something!");
//            scheduler.getFloorQueue().notifyAll();
//        }
    }
}
