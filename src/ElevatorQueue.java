import java.io.IOException;
import java.net.*;
import java.util.*;

public class ElevatorQueue {

    private DatagramPacket  receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int PORT = 69;
    private final HashMap<Integer, ArrayList<Integer>> queue;


    public ElevatorQueue(DatagramPacket receivePacket) {
        queue = new HashMap<>();
        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
    /**
     * Gets the queue.
     * @return Hashmap<Elevator, ArrayList<Integer>>, the queue.
     */
    public HashMap<Integer, ArrayList<Integer>> getQueue() {
        return queue;
    }
    public void addElevator(int elevatorNo) {
        queue.put(elevatorNo, new ArrayList<>());
    }

    public void respondToCall(){
        byte[] data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length, address, PORT);
        try {
            socket.receive(receivePacket);
            System.out.println("received packet"+receivePacket.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        queue.get((int) data[0]).add();
    }


}
