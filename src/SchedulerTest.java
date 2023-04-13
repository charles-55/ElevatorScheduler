import org.junit.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Elevator elevator;
    private static int NUM_OF_FLOORS = 5;
    private HashMap<Elevator, ArrayList<Integer>> queue;

    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem();
        floor = new Floor(1, floorSubsystem);
        elevator = new Elevator(1, NUM_OF_FLOORS,scheduler);
        queue = new HashMap<>();
    }

    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevator = null;
    }

    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

//    @Test
//    public void testRun() {
//        floorSubsystem.start();
//        scheduler.start();
//        elevator.start();
//        //assertEquals(0, elevator.getQueue().get(elevator).size());
//        floorSubsystem.stop();
//    }

}
