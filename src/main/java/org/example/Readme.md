

# Description
This project attempts to simulate a simple elevator.  A number of assumptions are made to simplify the model and focus on key behavior, attributes and state transitions of an elevation.       

## Elevator class
This class **Elevator** is the key entity of this model and attempts to model an elevator.  

A summary of keys methods are given below:

- **Elevator(int maxFloors, int stopDurationInSeconds, int maxTotalWeightLbs):** This is the constructor for the Elevator class. It initializes the elevator with the specified number of floors, the time it takes to stop at each floor, and the maximum total weight the elevator can carry. It also initializes the elevator's current floor, direction, and weight.

- **pressExternalUpButton(int floor):** Adds an external up button press to the elevator's queue.

- **pressExternalDownButton(int floor):** Adds an external down button press to the elevator's queue.

- **pressInternalButton(int floor):** Adds an internal button press to the elevator's queue.

- **addWeightLbs(int WeightLbs):** Increases the current weight of the elevator and pauses if weight limit is exceeded.

- **reduceWeightLbs(int WeightLbs):** Reduces the current weight of the elevator and resumes if weight falls below the limit.

- **getGetCurrentWeightLbs():** Retrieves the current weight of the elevator.

- **pause():** Pauses the elevator operation.

- **resume():** Resumes the elevator operation and notifies waiting threads.

- **isPaused():** Checks if the elevator is currently paused.

- **isValidFloor(int floor):** Validates if the floor input is within the valid range.

- **startMoving():** Starts the elevator movement thread.

- **move():** Simulates the elevator movement and stopping at floors based on button presses.

- **stopAtCurrentFloor():** Stops the elevator at the current floor and removes button presses for that floor.

- **getCurrentFloor():** Retrieves the current floor of the elevator.

- **getDirection():** Retrieves the current direction of the elevator.

- **getLock():** Retrieves the lock object used for synchronization.


## Cargo class
This class **Cargo** represents cargo (combining people and other objects accompanying them) that is being loaded and transported in an elevator. 

It has the following properties:

- **id:** a unique identifier for the cargo.
- **weightLbs:** the weight of the cargo in pounds.
- **startFloor:** the floor where the cargo starts.
- **targetFloor:** the floor where the cargo is headed.
- **direction:** the direction in which the cargo is moving (either UP or DOWN), derived from startFloor and targetFloor.


The class has two methods:

- **board(Elevator elevator):** This method initiates the cargo's boarding process by starting a new thread that waits until the elevator is not paused. Then, it calls the startBoardingProcess(Elevator elevator) method to start the actual process of moving the cargo.

- **startBoardingProcess(Elevator elevator):** This method is called by the board(Elevator elevator) method. It first presses the external button (up or down) based on the cargo's direction. Then, it waits until the elevator reaches the cargo's start floor and has the same direction as the cargo. After that, it adds the cargo's weight to the elevator's weight. If the elevator is paused due to excess weight, it reduces the elevator's weight by the cargo's weight. Otherwise, it presses the internal button for the cargo's target floor and waits until the elevator reaches the target floor. Finally, it reduces the elevator's weight by the cargo's weight.

## ElevatorSimulationRun class 
The **ElevatorSimulationRun** class is the entry point of the program.  It has the following 2 methods: 

- **main()**:  Steps included are:  
  - initializes a simulation of an elevator with 12 floors and a maximum weight capacity of 1000 pounds.
  - It creates 5 Cargo objects representing passengers or cargo, and then starts the elevator moving. 
  - The flow then simulates the following events:
    - The elevator pauses for 7 seconds to simulate someone pressing the emergency button.
    - The elevator resumes moving for 8 seconds to simulate someone releasing the emergency button.
    - Each Cargo object presses an external button to board the elevator.

- **explicitWait(int secs):** Is used to print a message and wait for a specified number of seconds


# Guiding Specifications and Assumptions:
- The Elevator will keep track of which floors require a stop.
- The available inputs to indicate which floor the Elevator stops are:
  - The external up button for a given floor is pressed and the Elevator headed that direction or is idle
  - The external down button for a given floor is pressed and the Elevator headed that direction or is idle
  - The internal button inside the elevator for that floor is pressed.
- There is one internal button for each floor for a maximum of N floors (configurable)
- There is one external up button and one external down botton per floor.
- It takes N (configurable) seconds per Elevator stop (to roughly simulate the delays with stops/loading/unloading/door open and close).
- The Elevator will keep going the same direction while buttons both internal and external are pressed in that direction.
- The Elevator will wait in place when no pressed buttons detected. 
- The Elevator will reverse direction when no buttons are pressed in the current direction and there are some buttons presses in the reverse direction.
- When the Elevator stops in a given floor, the pressed indicator for that floor will be reset to not pressed (off).
- The Elevator will display a message for each floor that it stops.
- The Elevator will be initialized to start on Floor 1 with 0 weight.
- Door opening and closing are assumed when the Elevator hits a floor.
- Button pressed state for both internal and external for the same direction as the Elevator are reset when it stops at that floor.
- Sensors and other attributes typical of a real elevator are abstracted away to simplify the model. 