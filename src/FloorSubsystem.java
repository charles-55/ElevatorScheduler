import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.Arrays;
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

    private DatagramPacket sendPacket;
    private DatagramSocket socket;
    private InetAddress address;
    private final int PORT = 2000;
    private final String fileName;

    /**
     * Initialize the FloorSubsystem.
     * @param fileName - the name of the input file preferably a .txt file.
     */
    public FloorSubsystem(String fileName) {
        this.fileName = fileName;
        try {
            socket = new DatagramSocket();
            this.address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Parse the data received in the input file
     */
    private void parseData() {
        File file = new File(fileName);

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] splitData = line.split(" ");

                LocalTime time;
                int floorNumber, destinationFloor;
                States state = States.IDLE;

                String[] timeInfo = splitData[0].split(":");
                time = LocalTime.of(Integer.parseInt(timeInfo[0]), Integer.parseInt(timeInfo[1]), Integer.parseInt(timeInfo[2].split("\\.")[0]), Integer.parseInt(timeInfo[2].split("\\.")[1]) * 1000000);

                floorNumber = Integer.parseInt(splitData[1]);
                destinationFloor = Integer.parseInt(splitData[3]);

                for (States s : States.values()) {
                    if (splitData[2].equalsIgnoreCase(s.toString())) {
                        state = s;
                        break;
                    }
                }

                byte[] data = new byte[3];
                data[0] = (byte) floorNumber;
                if (state == States.GOING_UP)
                    data[1] = 1;
                else if (state == States.GOING_DOWN)
                    data[1] = 2;
                data[2] = (byte) destinationFloor;
                sendPacket = new DatagramPacket(data, data.length, address, PORT);

                time = LocalTime.now(); // for testing purposes only!
                if(LocalTime.now().equals(time)) {
                    System.out.println("FLOOR SUBSYSTEM: Sending Packet: " + Arrays.toString(data) + ".");
                    socket.send(sendPacket);
                    System.out.println("FLOOR SUBSYSTEM: Packet Sent!\n");
                }
                else if (time.isAfter(LocalTime.now())) {
                    Thread.sleep((time.toNanoOfDay() - LocalTime.now().toNanoOfDay()) / 1000000);
                    socket.send(sendPacket);
                }
                Thread.sleep(2500);
            }
            closeSocket();
        } catch (InterruptedException | IOException e) {
            System.out.println("FLOOR SUBSYSTEM Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        parseData();
    }

    public void closeSocket() {
        socket.close();
    }
}
