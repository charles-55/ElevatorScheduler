import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Floor floor = new Floor(1, scheduler);
        Elevator elevator = new Elevator(5, scheduler);

        floor.start();
        elevator.start();
    }
}
