# SYSC3303Project


# SYSC 3303 PROJECT ITERATION 1

## Introduction:
Iteration 1 introduces the concept of concurrency between two or more threads. 
The Floor subsystem and the Elevators are the clients in the system; The Scheduler is the server. The Floor subsystem is to read in events(Time, floor or elevator number, and button). Each line of input is to be sent to the Scheduler. The elevators will make calls to the Scheduler which will then reply when there is work to be
done. The Elevator will then send the data back to the Scheduler who will then send it back to the Floor. For this iteration the Scheduler is only being used as a communication channel from the Floor thread to the Elevator thread and back again.


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
ElevatorCallEvent - Osamudiamen Nwoko
Main - *
TestClass - Oyindamola Taiwo-Olupeka
UML Diagrams - Oyindamola Taiwo-Olupeka
README.txt - Oyindamola Taiwo-Olupeka


## File Descriptions:

Floor - This class represents the current status of the floor. It takes the information from the table of the text file and processes it.

FloorSubsystem - This is the client class that reads events in the format: Time, floor, floor direction, and elevator button. Each line of input is to be sent to the Scheduler.

Scheduler - This class connects the elevators to the floor. It calls an elevator to a floor and adds the elevator to a queue when there is work to be done. It is only being used as a communication channel from the Floor thread to the Elevator thread and back again.

## Elevator - 

ElevatorCallEvent - This class creates an event to call an elevator to a particular floor.

Main - This class is used to run the program.

TestClass - This class has been created to test the methods are performing their assigned functions.


## Set-up Instructions:


## Test Instructions:


