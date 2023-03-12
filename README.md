# SYSC 3303 PROJECT


## Introduction:
In Iteration 3 we split your system up into three (or more) separate programs that can run on three separate computers and communicate with each other
using UDP. The Scheduler will now be used to coordinate the movement of cars such that each car carries roughly the same number of passengers as all of the others and so that the waiting time for passengers at floors is minimized. The state machines for each car should execute independently of each other, but they will all have to share their position with the scheduler. The scheduler will choose which elevator will be used to service a given request.


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
Run the Main class.


## Test Instructions:
Run the Test methods in the test classes (ElevatorTest, FloorTest, SchedulerTest).


## Packet Byte[] Information:
- Direction values: 1 = UP, 2 = DOWN
- Wait values: 1 = Nothing, 2 = Wait, 3 = Notify
- Floor Subsystem Sent Message: [floorNumber, direction, destinationFloor]
- Floor Received Message: [wait, floorNumber, elevatorNum, direction]
- Elevator Sent Message: [wait, floorNumber, elevatorNum, direction]
- Elevator Queue Received Message: [floorNumber, direction, destinationFloor]

## Port Information:
- Floor Subsystem Sending: 2000
- Scheduler Floor Receiving: 2000
- Scheduler Elevator Queue Sending: 2100
- Elevator Queue Receiving: 2100
- Elevator Sending: 2200
- Scheduler Elevator Receiving: 2200
- Scheduler Floor Sending: 2300 + floorNumber
- Floor Receiving: 2300 + floorNumber
