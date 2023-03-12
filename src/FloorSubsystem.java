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

    private States state;
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
        state = States.IDLE;
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

                String[] timeInfo = splitData[0].split(":");
                time = LocalTime.of(Integer.parseInt(timeInfo[0]), Integer.parseInt(timeInfo[1]), Integer.parseInt(timeInfo[2].split("\\.")[0]), Integer.parseInt(timeInfo[2].split("\\.")[1]) * 1000000);

                floorNumber = Integer.parseInt(splitData[1]);
                destinationFloor = Integer.parseInt(splitData[3]);

                byte[] data = new byte[3];
                data[0] = (byte) floorNumber;
                if(splitData[2].equalsIgnoreCase("UP"))
                    data[1] = 1;
                else if (splitData[2].equalsIgnoreCase("DOWN"))
                    data[1] = 2;
                data[2] = (byte) destinationFloor;
                sendPacket = new DatagramPacket(data, data.length, address, PORT);

                time = LocalTime.now(); // for testing purposes only!
                if(LocalTime.now().equals(time)) {
                    state = States.SENDING_TASK;
                    System.out.println("FLOOR SUBSYSTEM: Sending Packet: " + Arrays.toString(data) + ".");
                    socket.send(sendPacket);
                    System.out.println("FLOOR SUBSYSTEM: Packet Sent!\n");
                }
                else if (time.isAfter(LocalTime.now())) {
                    Thread.sleep((time.toNanoOfDay() - LocalTime.now().toNanoOfDay()) / 1000000);
                    socket.send(sendPacket);
                }
                state = States.IDLE;
                Thread.sleep(2500);
            }
            closeSocket();
        } catch (InterruptedException | IOException e) {
            state = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        parseData();
    }

    /**
     * Close the Datagram Sockets.
     */
    public void closeSocket() {
        socket.close();
    }
}
