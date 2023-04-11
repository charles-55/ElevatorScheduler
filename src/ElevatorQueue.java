import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * ElevatorQueue class implementing packets.
 */
public class ElevatorQueue extends Thread {

    private DatagramSocket socket;
    private InetAddress address;
    private States state;
    private static final int RECEIVING_PORT = 100;
    private final HashMap<Elevator, ArrayList<int[]>> queue;

    /**
     * Constructor method for ElevatorQueue.
     */
    public ElevatorQueue() {
        queue = new HashMap<>();
        state = States.WAITING_FOR_TASK;

        try {
            socket = new DatagramSocket(RECEIVING_PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            state = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Getter method for the elevator's queue.
     * @return HashMap<Elevator, ArrayList<Integer>> queue Elevator's queue.
     */
    public HashMap<Elevator, ArrayList<int[]>> getQueue() {
        return queue;
    }

    /**
     * Add an Elevator object in the queue HashMap.
     * @param elevator Elevator object.
     */
    public void addElevator(Elevator elevator) {
        queue.put(elevator, new ArrayList<>());
    }

    public States getStates() {
        return state;
    }

    /**
     * Responds when a packet is received.
     */
    public void respondToCall() {
        byte[] data = new byte[4];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length, address, RECEIVING_PORT);

        try {
            System.out.println("ELEVATOR QUEUE: Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            state = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR QUEUE: Packet Received: " + Arrays.toString(data) + ".\n");

        state = States.ADDING_TO_QUEUE;
        addToQueue(data);
    }

    /**
     * Add a call to an elevator's queue.
     * @param data byte[] An elevator call at floor X for the elevator to go to.
     */
    public synchronized void addToQueue(byte[] data) {
        Elevator elevator = (Elevator) queue.keySet().toArray()[0];

        for(Elevator e : queue.keySet()) {
            if(e.getElevatorNum() == (int) data[3]) {
                elevator = e;
                break;
            }
        }

        States state = States.IDLE;

        if(data[1] == (byte) 503) {
            elevator.setState(States.OUT_OF_SERVICE);
            return;
        }
        else if(data[1] == (byte) 504) {
            elevator.closeDoors();
            return;
        }
        else if(data[1] == (byte) 1)
            state = States.GOING_UP;
        else if(data[1] == (byte) 2)
            state = States.GOING_DOWN;

        if(elevator.getStates().equals(States.IDLE)) {
            elevator.callElevator(data[0], state);
        }

        queue.get(elevator).add(new int[] {(int) data[0], (int) data[2]});
        queue.get(elevator).sort(Comparator.comparingInt(array -> Arrays.stream(array).sum()));
//        System.out.println("ELEVATOR QUEUE: Added to queue.");

        notifyAll();
        this.state = States.WAITING_FOR_TASK;
    }

    /**
     * Gets the queue and lights up the elevator buttons respectively.
     * @param elevator Elevator object.
     */
    public synchronized void getFromQueue(Elevator elevator) {
        while(queue.get(elevator).size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < queue.get(elevator).size();) {
            elevator.addToDelayedQueue(queue.get(elevator).get(i)[0], queue.get(elevator).get(i)[1]);
            queue.get(elevator).remove(i);
        }
        System.out.println("ELEVATOR " + elevator.getElevatorNum() + ": Got from queue.\n");
    }

    /**
     * Closes a socket.
     */
    public void closeSocket() {
        socket.close();
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true)
            respondToCall();
    }
}
