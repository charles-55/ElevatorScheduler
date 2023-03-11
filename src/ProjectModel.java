import java.util.ArrayList;

public class ProjectModel {

    private final Scheduler scheduler;
    private final ElevatorQueue elevatorQueue;
    private final FloorSubsystem floorSubsystem;
    private final ArrayList<Floor> floors;
    private final ArrayList<Elevator> elevators;
    private final ProjectFrame projectFrame;
    private final Thread thread;

    public ProjectModel() {
        scheduler = new Scheduler();
        elevatorQueue = new ElevatorQueue();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floors = new ArrayList<>();
        elevators = new ArrayList<>();

        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            floors.add(new Floor(i + 1, scheduler, floorSubsystem));
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS - 1; i++)
            elevators.add(new Elevator(i + 1, Floor.NUM_OF_FLOORS, elevatorQueue));
        elevators.add(new Elevator(Elevator.NUM_OF_ELEVATORS, Floor.NUM_OF_FLOORS, 2, elevatorQueue));


        projectFrame = new ProjectFrame(this, floors);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    projectFrame.updateUI();
                }
            }
        });
        start();
    }

    public ArrayList<Elevator> getElevators() {
        return elevators;
    }

    private void start() {
        floorSubsystem.start();
        scheduler.start();
        elevatorQueue.start();

        for(Floor floor : floors)
            floor.start();
        for(Elevator elevator : elevators)
            elevator.start();

        thread.start();
    }

    public void end() {
        floorSubsystem.closeSocket();
        scheduler.closeSocket();
        elevatorQueue.closeSocket();

        for(Floor floor : floors)
            floor.closeSocket();
    }
}
