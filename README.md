# ELEVATOR SCHEDULER PROJECT


## Introduction:
In Iteration 4 we added code for detecting and handling faults. To this end, we added timing events so that if the timer goes off before an elevator reaches a floor, then our system should assume a fault (either, the elevator is stuck between floors, or the arrival sensor at a floor has failed). We should detect whether a door opens or not, or is stuck open.


## Team Members:
#### Osamudiamen Nwoko - 101152520
#### Oyindamola Taiwo-Olupeka - 101155729
#### Sabah Samwatin - 101137966
#### Leslie Ejeh - 101161386
#### Nicholas Thibault - 101172413


## Responsibilities:
Floor Class - Sabah Samwatin

FloorSubsystem - Sabah Samwatin

Scheduler - Osamudiamen Nwoko, Leslie Ejeh, Nicholas Thibault

Elevator - Nicholas Thibault

ElevatorQueue - Osamudiamen Nwoko, Leslie Ejeh

Main - *

FloorTest -  Oyindamola Taiwo-Olupeka

ElevatorTest - Oyindamola Taiwo-Olupeka

SchedulerTest - Oyindamola Taiwo-Olupeka

UML Class & Sequence Diagrams - Sabah Samwatin, Oyindamola Taiwo-Olupeka

States Machine Diagrams - Sabah Samwatin

README.txt - Oyindamola Taiwo-Olupeka


## File Descriptions:
Floor class - This class represents the current status of the floor. It takes the information from the table of the text file and processes it.

FloorSubsystem class - This is the client class that reads events in the format: Time, floor, floor direction, and elevator button. Each line of input is to be sent to the Scheduler.

Scheduler class - This class connects the elevators to the floor. It calls an elevator to a floor and adds the elevator to a queue when there is work to be done. It is only being used as a communication channel from the Floor thread to the Elevator thread and back again.

Elevator class - This class moves elevator between floors based on the data gotten from the Scheduler class.

ElevatorQueue class - This class hooks the elevators up to the queue system.

Main class - This class is used to run the program.

FloorTest - This is a test class that tests the methods in the Floor and FloorSubsystem classes.

ElevatorTest - This is a test class that tests the methods in the Elevator class.

SchedulerTest - This is a test class that tests the methods in the Scheduler class.

InputTable.txt - A text file filled with random data entries to be parsed into the scheduler.


## Set-up Instructions:
- To run the program on a single device, simply run the Main class.
- To run the program on separate devices, divide into these 3 groups and modify their InetAddresses:
  1. Floor, FloorSubsystem, InputTable.txt, States
  2. Scheduler, States
  3. Elevator (would also need to access the number of floors from the floor class), ElevatorQueue, States


## Test Instructions:
Run the Test methods in the test classes all on a single device (ElevatorTest, FloorTest, SchedulerTest).


## Packet Byte[] Information:
- Direction values: 1 = UP, 2 = DOWN
- elevatorState values: 0 = IDLE, 1 = UP, 2 = DOWN, 503 = OUT OF SERVICE, 404 = UNKNOWN STATE, 504 = FORCE DOOR CLOSE
- stillTasking/makingStop values: 1 = Yes, 0 = No
- handlingDoor values: 1 = opening, 2 = holding, 3 = closing, 4 = moving, 0 = closed
- Floor Subsystem Sent Message: [floorNumber, direction, destinationFloor]
- Floor Received Message: [floorNumber, elevatorNum, direction]
- Elevator Sent Message: [elevatorState, handlingDoor, floorNumber, elevatorNum, direction]
- Elevator Queue Received Message: [floorNumber, direction, destinationFloor, elevatorNum]

## Port Information:
- Floor Subsystem Sending: 2000
- Scheduler Floor Receiving: 2000
- Scheduler Elevator Queue Sending: 2100
- Elevator Queue Receiving: 2100
- Elevator Sending: 2200
- Scheduler Elevator Receiving: 2200
- Scheduler Floor Sending: 2300 + floorNumber
- Floor Receiving: 2300 + floorNumber
