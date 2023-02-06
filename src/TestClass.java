import org.junit.Test;
import java.time.LocalTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka
 * @version 1.0
 */
public class TestClass {

    private Scheduler scheduler;
    private Elevator elevator;
    private ElevatorCallEvent event;

    @Test
    public void setUp(){

    }

    @Test
    public void testDown(){

    }

    @Test
    public void testElevator(){
        scheduler = new Scheduler();
        elevator = new Elevator(7,1,scheduler);
        event = new ElevatorCallEvent(LocalTime.now(),1, ElevatorCallEvent.Direction.UP,3);
        assertEquals(1,event.getFloorNumber());
        scheduler.addToQueue(event);

        if(event.getFloorNumber() == 3) {
        }
    }

    @Test
    public void testMoveFloor() {
        scheduler = new Scheduler();
        elevator = new Elevator(5, scheduler);

        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, ElevatorCallEvent.Direction.UP);
        assertEquals(3, elevator.getCurrentFloor());
    }
}
