import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * The FloorSubsystem Class.
 * Client in system
 * To read events in the format: Time, floor, floor direction, and elevator button,
 * Each line of input is to be sent to the Scheduler
 *
 * @author Sabah Samwatin
 * @version 1.0
 * */

public class FloorSubsystem {

    private final Scheduler scheduler;

    public FloorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Parse the data received in the input file
     * @param fileName - the name of the input file preferably a .txt file
     */
    public void parseData(String fileName) {
        File file = new File(fileName);

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                String[] splitData = data.split(" ");

                LocalTime time;
                int floorNumber, elevatorNumber;
                ElevatorCallEvent.Direction direction = ElevatorCallEvent.Direction.STANDBY;

                String[] timeInfo = splitData[0].split(":");
                time = LocalTime.of(Integer.parseInt(timeInfo[0]), Integer.parseInt(timeInfo[1]), Integer.parseInt(timeInfo[2].split("\\.")[0]), Integer.parseInt(timeInfo[2].split("\\.")[1]) * 1000000);

                floorNumber= Integer.parseInt(splitData[1]);
                elevatorNumber= Integer.parseInt(splitData[3]);

                for(ElevatorCallEvent.Direction d : ElevatorCallEvent.Direction.values()) {
                    if(splitData[2].equalsIgnoreCase(d.toString())){
                        direction = d;
                        break;
                    }
                }

                ElevatorCallEvent event = new ElevatorCallEvent(time, floorNumber, direction, elevatorNumber);
                if(LocalTime.now().equals(time))
                    scheduler.callElevator(event);
                else if (time.isAfter(LocalTime.now()))
                    Thread.sleep((time.toNanoOfDay() - LocalTime.now().toNanoOfDay()) / 1000000); //
            }

        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
