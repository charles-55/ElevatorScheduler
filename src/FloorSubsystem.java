import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.ArrayList;
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

    private final ArrayList<Floor> floors;
    private States state, parseState, receiveState;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int SEND_PORT = 2000, RECEIVE_PORT = 2300;
    private final String fileName;

    /**
     * Initialize the FloorSubsystem.
     * @param fileName - the name of the input file preferably a .txt file.
     */
    public FloorSubsystem(String fileName) {
        this.fileName = fileName;
        state = States.IDLE;
        parseState = States.IDLE;
        receiveState = States.IDLE;
        try {
            socket = new DatagramSocket();
            this.address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        floors = new ArrayList<>();
    }
    public void addFloor(Floor floor){
        floors.add(floor);
    }

    private void handleParseState() {
        switch (parseState) {
            case IDLE ->  {
                parseData();
            }
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

                sendToScheduler(data, time);
                Thread.sleep(2500); //testing purpose only
            }
        } catch (InterruptedException | IOException e) {
            state = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendToScheduler(byte[] data, LocalTime time) {
        updateFloor(data[0], data[2], true);
        sendPacket = new DatagramPacket(data, data.length, address, SEND_PORT);

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Send Packet Error: " + Arrays.toString(data) + "\n");
            throw new RuntimeException(e);
        }
        System.out.println("FLOOR SUBSYSTEM: Sent Packet: " + Arrays.toString(data) + "\n");

//        time = LocalTime.now(); // for testing purposes only!
//        if(LocalTime.now().equals(time)) {
//            try {
//                socket.send(sendPacket);
//            } catch (IOException e) {
//        System.out.println("FLOOR SUBSYSTEM: Send Packet Error: " + Arrays.toString(data) + "\n");
//                throw new RuntimeException(e);
//            }
//        System.out.println("FLOOR SUBSYSTEM: Sent Packet: " + Arrays.toString(data) + "\n");
//        }
//        else if (time.isAfter(LocalTime.now())) {
//            try {
//                Thread.sleep((time.toNanoOfDay() - LocalTime.now().toNanoOfDay()) / 1000000);
//                socket.send(sendPacket);
//            } catch (InterruptedException | IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        state = States.IDLE;
        receiveFromScheduler();
    }

    private void receiveFromScheduler(){
        byte[] data = new byte[3];
        receivePacket = new DatagramPacket(data, data.length, address, RECEIVE_PORT);

        try {
            System.out.println("FLOOR SUBSYSTEM: Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Error Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        if(data[1] == 0) {
            String direction = "";
            if(data[2] == 1)
                direction = "up";
            else if(data[2] == 2)
                direction = "down";
            System.out.println("FLOOR SUBSYSTEM: Reply received to floor " + data[0] + " going " + direction + ".\n");
            return;
        }

        System.out.println("FLOOR SUBSYSTEM: Packet received: " + Arrays.toString(data) + "\n");

        System.out.println("FLOOR " + data[0] + ": Elevator " + data[1] + " arrived.\n");
        if (data[2] == 1)
            updateFloor(data[0], 1, false);
        else if (data[2] == 2)
            updateFloor(data[0], 2, false);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void updateFloor(int floorNumber, int direction, boolean state) {
        for(Floor floor:floors){
            if (floor.getFloorNumber()==floorNumber){
                floor.setButtonDirection(direction, state);
                break;
            }
        }
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        Thread parseDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                parseData();
            }
        });

        Thread receiveMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    receiveFromScheduler();
            }
        });

        parseDataThread.start();
        receiveMessageThread.start();
    }

    /**
     * Close the Datagram Sockets.
     */
    public void closeSocket() {
        socket.close();
    }
}
