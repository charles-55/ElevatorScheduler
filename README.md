# SYSC 3303 PROJECT

## Introduction:
Iteration 2 builds on the concept of concurrency between two or more threads, it also introduces state machines.
The goal of this iteration is to add the state machines for the scheduler and elevator subsystems assuming that
there is only one elevator. The elevator subsystem is used to notify the scheduler that an elevator has
reached a floor, so that once an elevator has been told to move, the elevator subsystem also has to be informed so that it can send out messages back to the scheduler to denote the arrival by an elevator.


## Team Members:
#### Osamudiamen Nwoko - 101152520
#### Sabah Samwatin - 101137966
#### Leslie Ejeh - 101161386
#### Oyindamola Taiwo-Olupeka - 101155729
#### Nicholas Thibault - 101172413


## Responsibilities:

Floor Class - Sabah Samwatin

FloorSubsystem - Sabah Samwatin

Scheduler - Osamudiamen Nwoko, Leslie Ejeh

Elevator - Nicholas Thibault


Main - *

FloorTest -  Oyindamola Taiwo-Olupeka

ElevatorTest - Oyindamola Taiwo-Olupeka

SchedulerTest - Oyindamola Taiwo-Olupeka

UML Class & Sequence Diagrams - Sabah Samwatin, Oyindamola Taiwo-Olupeka

State Machine Diagram - Sabah Samwatin

README.txt - Oyindamola Taiwo-Olupeka


## File Descriptions:

Floor class - This class represents the current status of the floor. It takes the information from the table of the text file and processes it.

FloorSubsystem class - This is the client class that reads events in the format: Time, floor, floor direction, and elevator button. Each line of input is to be sent to the Scheduler.

Scheduler class - This class connects the elevators to the floor. It calls an elevator to a floor and adds the elevator to a queue when there is work to be done. It is only being used as a communication channel from the Floor thread to the Elevator thread and back again.

Elevator class - This class moves elevator between floors based on the data gotten from the Scheduler class.

Main class - This class is used to run the program.

FloorTest - This is a test class that tests the methods in the Floor and FloorSubsytem classes.

ElevatorTest - This is a test class that tests the methods in the Elevator class.

SchedulerTest - This is a test class that tests the methods in the Scheduler class.

InputTable.txt - A text file filled with random data entries to be parsed into the scheduler.


## Set-up Instructions:
Run the Main class.

## Test Instructions:
Run the Test methods in the test classes (ElevatorTest, FloorTest, SchedulerTest).

