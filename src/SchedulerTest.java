import org.junit.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * The Scheduler Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class SchedulerTest {

    private Scheduler scheduler;
    private FloorSubsystem floorSubsystem;
    private Floor floor;
    private ElevatorQueue elevatorQueue;
    private Elevator elevator;
    private static int NUM_OF_FLOORS = 5;
    private HashMap<Elevator, ArrayList<Integer>> queue;

    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floor = new Floor(1);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, NUM_OF_FLOORS, elevatorQueue);
        queue = new HashMap<>();
    }

    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();
        floor.closeSocket();
        elevatorQueue.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevatorQueue = null;
        elevator = null;
    }

    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

    @Test
    public void testAddToQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertFalse(elevatorQueue.getQueue().isEmpty());
    }

    @Test
    public void testGetFromQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertEquals(1,elevatorQueue.getQueue().size());
        elevatorQueue.getQueue().remove(elevator,new ArrayList<>());
        assertTrue(elevatorQueue.getQueue().isEmpty());
    }

    @Test
    public void testRun() {
        floorSubsystem.start();
        floor.start();
        scheduler.start();
        elevatorQueue.start();
        assertEquals(0, elevatorQueue.getQueue().get(elevator).size());
        floorSubsystem.stop();
        floor.stop();
    }
}
