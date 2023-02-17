import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
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

public class FloorSubsystem extends Thread {

    private final Floor floor;
    private final Scheduler scheduler;
    private DatagramPacket sendPacket;
    private DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public FloorSubsystem(Floor floor, Scheduler scheduler, InetAddress address, int port) {
        this.floor = floor;
        this.scheduler = scheduler;
        this.address = address;
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
                System.out.println("Line scanned: " + data);
                String[] splitData = data.split(" ");

                LocalTime time;
                int floorNumber, destinationFloor;
                ElevatorCallEvent.Direction direction = ElevatorCallEvent.Direction.STANDBY;

                String[] timeInfo = splitData[0].split(":");
                time = LocalTime.of(Integer.parseInt(timeInfo[0]), Integer.parseInt(timeInfo[1]), Integer.parseInt(timeInfo[2].split("\\.")[0]), Integer.parseInt(timeInfo[2].split("\\.")[1]) * 1000000);

                floorNumber = Integer.parseInt(splitData[1]);
                destinationFloor = Integer.parseInt(splitData[3]);

                for(ElevatorCallEvent.Direction d : ElevatorCallEvent.Direction.values()) {
                    if(splitData[2].equalsIgnoreCase(d.toString())) {
                        direction = d;
                        break;
                    }
                }

                byte[] info = new byte[3];
                info[0] = (byte) floorNumber;
                if(direction == ElevatorCallEvent.Direction.UP)
                    info[1] = 1;
                else if(direction == ElevatorCallEvent.Direction.DOWN)
                    info[1] = 2;
                info[2] = (byte) destinationFloor;
                sendPacket = new DatagramPacket(info, info.length, address, port);
                socket.send(sendPacket);

//                ElevatorCallEvent event = new ElevatorCallEvent(time, floorNumber, direction, destinationFloor);
//                if(LocalTime.now().equals(time))
//                    scheduler.addToQueue(event);
//                else if (time.isAfter(LocalTime.now()))
//                    Thread.sleep((time.toNanoOfDay() - LocalTime.now().toNanoOfDay()) / 1000000);
            }
        } catch (/*InterruptedException | */IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
