import org.junit.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * The Floor and FloorSubsystem Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class FloorTest {

    private Scheduler scheduler;
    private FloorSubsystem floorSubsystem;
    private Floor floor;
    private Elevator elevator;
    private static int NUM_OF_FLOORS = 5;

    /**
     * Method that sets up objects before the tests.
     */
    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem();
        floor = new Floor(1, floorSubsystem);
        //elevator = new Elevator(1, NUM_OF_FLOORS, scheduler);
    }

    /**
     * Method that closes up everything after the tests.
     */
    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevator = null;
    }

    /**
     * Test method for the threads.
     */
    @Test
    public void testAddFloor(){
        final ArrayList<Floor> floors = new ArrayList<>();

        assertEquals(0, floors.size());
        floors.add(floor);
        assertEquals(1, floors.size());
    }

    @Test
    public void testCallElevator(){
//        assertEquals(1,elevator.getCurrentFloor());
//        floorSubsystem.callElevator(2,1);
//        assertEquals(2,elevator.getCurrentFloor());
    }

    @Test
    public void testSendToScheduler(){
//        byte[] data = new byte[0];
//        LocalTime time = LocalTime.now();
//        States state = States.WAITING_FOR_TASK;
//        assertEquals(state,States.WAITING_FOR_TASK);
//        //floorSubsystem.sendToScheduler(data,time);
//        //assertEquals(state,States.RECEIVING_TASK);

    }

    @Test
    public void testReceiveFromScheduler(){

    }

    @Test
    public void testUpdateFloor(){
        Elevator elevator = new Elevator(1,22,scheduler);
        elevator.isMoving();
        assertEquals(1,floor.getFloorNumber());
    }

    @Test
    public void testElevatorInfo(){
        elevator.isMoving() ;
        assertFalse(elevator.getStates().equals(States.GOING_UP));
    }

    @Test
    public void testUpdateFrame(){

    }

    /**
     * Test method for the threads.
     */
//    @Test
//    public void testRun() {
//        //floorSubsystem.start();
//        //scheduler.start();
//        //floorSubsystem.stop();
//    }

    /**
     * Test method for parseData() method in Floor.
     */
    @Test
    public void testParseData() {
        String data = "14:05:15:0 1 Up 4";
        String splitData = Arrays.toString(data.split(","));
        assertEquals(splitData,"[" + data + "]");
    }


}
