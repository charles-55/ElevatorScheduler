import java.io.IOException;
import java.net.*;
import java.util.*;

public class ElevatorQueue extends Thread {

    private boolean waiting;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int PORT = 21;
    private final HashMap<Elevator, ArrayList<Integer>> queue;

    public ElevatorQueue() {
        waiting = false;
        queue = new HashMap<>();

        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public HashMap<Elevator, ArrayList<Integer>> getQueue() {
        return queue;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void addElevator(Elevator elevator) {
        queue.put(elevator, new ArrayList<>());
    }

    public void addToQueue(byte[] data) {
        Elevator.Direction direction = Elevator.Direction.STANDBY;
        if(data[1] == 1)
            direction = Elevator.Direction.UP;
        else if(data[1] == 2)
            direction = Elevator.Direction.DOWN;

        Elevator elevator = (Elevator) queue.keySet().toArray()[0];
        for(Elevator e : queue.keySet()) {
            if ((Math.abs(e.getCurrentFloor() - ((int) data[0]))
                    < Math.abs(elevator.getCurrentFloor() - ((int) data[0])))
                    && ((e.getDirection().equals(direction))
                    || (e.getDirection().equals(Elevator.Direction.STANDBY))))
                elevator = e;
        }

        if(elevator.getDirection().equals(Elevator.Direction.STANDBY)) {
            elevator.moveToFloor(data[0], (elevator.getCurrentFloor() - data[0] > 0) ? Elevator.Direction.DOWN : Elevator.Direction.UP);
            elevator.moveToFloor(data[2], direction);
            return;
        }
        else if(elevator.getDirection().equals(Elevator.Direction.DOWN) && (elevator.getCurrentFloor() - data[0] >= 0)) {
            elevator.put(data[0], true);
            elevator.addToDelayedQueue(data[0], data[2]);
        }
        else if(elevator.getDirection().equals(Elevator.Direction.UP) && (elevator.getCurrentFloor() - data[0] <= 0)) {
            elevator.put(data[0], true);
            elevator.addToDelayedQueue(data[0], data[2]);
        }
        else {
            try {
                System.out.println("ELEVATOR QUEUE: A Delay Occurred!\n");
                Elevator.alertDelay();
                waiting = true;
                this.wait();
                waiting = false;
                Elevator.alertDelayResolved();
                elevator.moveToFloor(data[0], (elevator.getCurrentFloor() - data[0] > 0) ? Elevator.Direction.DOWN : Elevator.Direction.UP);
                elevator.moveToFloor(data[2], direction);
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        queue.get(elevator).add((int) data[2]);
        Collections.sort(queue.get(elevator));
        System.out.println("ELEVATOR QUEUE: Added to queue.");

        notifyAll();
    }

    public synchronized void getFromQueue(Elevator elevator) {
        while(queue.get(elevator).size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < queue.get(elevator).size();) {
            elevator.put(queue.get(elevator).get(i), true);
            queue.get(elevator).remove(i);
        }
        System.out.println("ELEVATOR " + elevator.getElevatorNum() + ": Got from queue.\n");
    }

    public void respondToCall() {
        byte[] data = new byte[3];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR QUEUE: Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR QUEUE: Packet Received: " + Arrays.toString(data) + ".\n");

        addToQueue(data);
    }

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

    public void closeSocket() {
        socket.close();
    }
}
