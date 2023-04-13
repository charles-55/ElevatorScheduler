import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ElevatorButtonController implements ActionListener {

    private FloorSubsystem floorSubsystem;

    public ElevatorButtonController(FloorSubsystem floorSubsystem){
        this.floorSubsystem=floorSubsystem;

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("")){

        }

    }
}
