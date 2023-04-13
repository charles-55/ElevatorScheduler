import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FloorButtonController implements ActionListener {
    private FloorSubsystem floorSubsystem;

    public FloorButtonController(FloorSubsystem floorSubsystem){
        this.floorSubsystem=floorSubsystem;

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("1")){

        }else if(e.getActionCommand().equals("2")){

        }

    }
}
