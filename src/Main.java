public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        FloorSubsystem floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        Floor floor = new Floor(1);
        ElevatorQueue elevatorQueue = new ElevatorQueue();
        Elevator elevator = new Elevator(1, 5, elevatorQueue);

        floorSubsystem.start();
        floor.start();
        scheduler.start();
        elevatorQueue.start();
        elevator.start();
    }
}
