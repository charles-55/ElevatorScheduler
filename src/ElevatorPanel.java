import javax.swing.*;
import java.awt.*;

public class ElevatorPanel extends JPanel {

    private final JLabel display;
    private final Elevator elevator;
    private final JButton[] buttons;
    public static final int WIDTH = 150, HEIGHT = 200;

    public ElevatorPanel(Elevator elevator) {
        display = new JLabel(String.valueOf(elevator.getCurrentFloor()));
        this.elevator = elevator;
        buttons = new JButton[ProjectModel.NUM_OF_FLOORS];

        initializeButtons();

        JPanel panel = new JPanel(new GridLayout(ProjectModel.NUM_OF_FLOORS / 2, ProjectModel.NUM_OF_FLOORS / 2));
        for(int i = 0; i < ProjectModel.NUM_OF_FLOORS; i++)
            panel.add(buttons[i]);
        panel.setMaximumSize(new Dimension(WIDTH - 50, HEIGHT - 50));

        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    public Elevator getElevator() {
        return elevator;
    }

    private void initializeButtons() {
        for(int i = 0; i < ProjectModel.NUM_OF_FLOORS; i++) {
            buttons[i] = new JButton(String.valueOf(i + 1));
            buttons[i].setBackground(ProjectFrame.OFF);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(elevator == null)
            return;

        display.setText(String.valueOf(elevator.getCurrentFloor()));
        for(int i = 0; i < ProjectModel.NUM_OF_FLOORS; i++) {
            if(elevator.getButtonsAndLamps().get(i + 1))
                buttons[i].setBackground(ProjectFrame.ON);
            else
                buttons[i].setBackground(ProjectFrame.OFF);
        }
    }
}
