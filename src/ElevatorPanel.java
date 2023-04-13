import javax.swing.*;
import java.awt.*;

public class ElevatorPanel extends JPanel {

    private final JLabel floorDisplay, stateDisplay;
    private final int elevatorNum;
    private int currentFloor;
    private States elevatorState;
    private final JButton[] buttons;
    private final ElevatorButtonController  elevatorButtonController;
    public static final int WIDTH = 200, HEIGHT = 200;


    public ElevatorPanel(int elevatorNum, int currentFloor, States elevatorState, FloorSubsystem floorSubsystem) {
        this.elevatorNum = elevatorNum;
        this.currentFloor = currentFloor;
        this.elevatorState = elevatorState;
        elevatorButtonController=new ElevatorButtonController(elevatorNum, floorSubsystem);

        floorDisplay = new JLabel(String.valueOf(currentFloor));
        stateDisplay = new JLabel(elevatorState.toString().replace('_', ' '));
        buttons = new JButton[Floor.NUM_OF_FLOORS];

        initializeButtons();

        JPanel buttonPanel = new JPanel(new GridLayout(Floor.NUM_OF_FLOORS / 3, 3));
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
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    public int getElevatorNum() {
        return elevatorNum;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setElevatorState(States elevatorState) {
        this.elevatorState = elevatorState;
    }

    private void initializeButtons() {
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            buttons[i] = new JButton(String.valueOf(i + 1));
            buttons[i].setActionCommand(String.valueOf(i+1));
            buttons[i].addActionListener(elevatorButtonController);
            buttons[i].setBackground(Frame.OFF);
            //buttons[i].setEnabled(false);
        }
    }

//    public void enableButtons(){
//        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
//            buttons[i].setEnabled(true);
//        }
//    }
//
//    public void disableButtons(){
//        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
//            buttons[i].setEnabled(false);
//        }
//    }

    public void updateElevatorInfo(int currentFloor, States elevatorState) {
        this.currentFloor = currentFloor;
        this.elevatorState = elevatorState;
    }

    public void updateButton(int floorNum, boolean pressed) {
        if(pressed)
            buttons[floorNum - 1].setBackground(Frame.ON);
        else
            buttons[floorNum - 1].setBackground(Frame.OFF);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(elevatorNum == 0)
            return;

        floorDisplay.setText(String.valueOf(currentFloor));
        stateDisplay.setText(elevatorState.toString().replace('_', ' '));
    }
}
