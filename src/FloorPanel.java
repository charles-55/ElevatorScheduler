import javax.swing.*;
import java.awt.*;

public class FloorPanel extends JPanel {

    private final Floor floor;
    private final JButton[] buttons;
    private final FloorButtonController floorButtonController;
    public static final int WIDTH = 500, HEIGHT = 200;

    public FloorPanel(Floor floor, FloorSubsystem floorSubsystem) {
        this.floor = floor;
        floorButtonController = new FloorButtonController(floor.getFloorNumber(), floorSubsystem);

        JPanel panel = new JPanel();
        if(floor.getFloorNumber() == 1) {
            buttons = new JButton[] {generateOffButton("UP")};
            panel.add(buttons[0]);
        }
        else if(floor.getFloorNumber() == Floor.NUM_OF_FLOORS) {
            buttons = new JButton[] {generateOffButton("DOWN")};
            panel.add(buttons[0]);
        }
        else {
            buttons = new JButton[] {generateOffButton("UP"), generateOffButton("DOWN")};
            panel.setLayout(new GridLayout(2, 1));
            panel.add(buttons[0]);
            panel.add(buttons[1]);
        }

        panel.setMaximumSize(new Dimension(50, 50));

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.LINE_START);
        this.add(new JLabel(String.valueOf(floor.getFloorNumber())), BorderLayout.LINE_END);
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    private JButton generateOffButton(String name) {
        JButton button = new JButton(name);

        if(name.equals("UP"))
            button.setActionCommand("1");
        else if(name.equals("DOWN"))
            button.setActionCommand("2");

        button.addActionListener(floorButtonController);
        button.setBackground(Frame.OFF);
        return button;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(floor == null)
            return;

        if(floor.getFloorNumber() == 1) {
            if(floor.getButtonsAndLamps().get(1))
                buttons[0].setBackground(Frame.ON);
            else
                buttons[0].setBackground(Frame.OFF);
        }
        else if(floor.getFloorNumber() == Floor.NUM_OF_FLOORS) {
            if(floor.getButtonsAndLamps().get(2))
                buttons[0].setBackground(Frame.ON);
            else
                buttons[0].setBackground(Frame.OFF);
        }
        else {
            if (floor.getButtonsAndLamps().get(1))
                buttons[0].setBackground(Frame.ON);
            else
                buttons[0].setBackground(Frame.OFF);
            if (floor.getButtonsAndLamps().get(2))
                buttons[1].setBackground(Frame.ON);
            else
                buttons[1].setBackground(Frame.OFF);
        }
    }
}
