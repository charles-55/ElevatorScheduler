import javax.swing.*;
import java.awt.*;

public class ElevatorPanel extends JPanel {

    private final JLabel floorDisplay, stateDisplay;
    private final Elevator elevator;
    private final JButton[] buttons;
    public static final int WIDTH = 150, HEIGHT = 200;

    public ElevatorPanel(Elevator elevator) {
        floorDisplay = new JLabel(String.valueOf(elevator.getCurrentFloor()));
        stateDisplay = new JLabel(elevator.getStates().toString().replace('_', ' '));
        this.elevator = elevator;
        buttons = new JButton[Floor.NUM_OF_FLOORS];

        initializeButtons();

        JPanel buttonPanel = new JPanel(new GridLayout(Floor.NUM_OF_FLOORS / 2, Floor.NUM_OF_FLOORS / 2));
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            buttonPanel.add(buttons[i]);
        buttonPanel.setMaximumSize(new Dimension(WIDTH - 50, HEIGHT - 50));

        JPanel labelPanel = new JPanel(new GridLayout(1, 2));
        labelPanel.add(floorDisplay);
        labelPanel.add(stateDisplay);

        this.setLayout(new BorderLayout());
        this.add(labelPanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    public Elevator getElevator() {
        return elevator;
    }

    private void initializeButtons() {
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            buttons[i] = new JButton(String.valueOf(i + 1));
            buttons[i].setBackground(ProjectFrame.OFF);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(elevator == null)
            return;

        floorDisplay.setText(String.valueOf(elevator.getCurrentFloor()));
        stateDisplay.setText(elevator.getStates().toString().replace('_', ' '));
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            if(elevator.getButtonsAndLamps().get(i + 1))
                buttons[i].setBackground(ProjectFrame.ON);
            else
                buttons[i].setBackground(ProjectFrame.OFF);
        }
    }
}
