
# Elevator Simulation

This project simulates an elevator system with functionality to handle external and internal button presses, weight constraints, and multiple cargos with different start and target floors. The core component of this simulation is the `Elevator` class.

## Elevator Class Overview

The `Elevator` class simulates an elevator with the following main features:
- Configurable number of floors, stop duration at each floor, and maximum weight limit.
- Handles external up and down button presses through the `ExternalButtonTracker`.
- Manages internal button presses directly.
- Moves between floors, stops at floors where buttons have been pressed, and handles weight constraints.
- Can pause and resume operation based on the weight limit and other conditions.

### Key Methods and Attributes

#### Attributes

- `maxFloors`: The maximum number of floors the elevator can service.
- `stopDurationInSeconds`: The duration for which the elevator stops at each floor.
- `maxTotalWeightLbs`: The maximum weight the elevator can handle.
- `externalButtonTracker`: An instance of `ExternalButtonTracker` to manage external button presses.
- `internalButtons`: A set of internal button presses.
- `currentFloor`: The current floor the elevator is on.
- `direction`: The current direction of the elevator (up or down).
- `currentWeightLbs`: The current weight of the cargo inside the elevator.
- `elevatorLock`: A lock object to manage synchronization.
- `isPaused`: A flag indicating whether the elevator is paused.

#### Methods

- **Constructor**: Initializes the elevator with the specified number of floors, stop duration, maximum weight, and an external button tracker.
    ```java
    public Elevator(int maxFloors, int stopDurationInSeconds, int maxTotalWeightLbs, ExternalButtonTracker externalButtonTracker)
    ```

- **pressInternalButton(int floor)**: Presses an internal button for the specified floor.
    ```java
    public void pressInternalButton(int floor)
    ```

- **addWeightLbs(int weightLbs)**: Adds weight to the elevator and pauses it if the weight exceeds the maximum limit.
    ```java
    public void addWeightLbs(int weightLbs)
    ```

- **reduceWeightLbs(int weightLbs)**: Reduces weight from the elevator and resumes it if the weight goes below the maximum limit.
    ```java
    public void reduceWeightLbs(int weightLbs)
    ```

- **getGetCurrentWeightLbs()**: Returns the current weight in the elevator.
    ```java
    public int getGetCurrentWeightLbs()
    ```

- **pause()**: Pauses the elevator.
    ```java
    public void pause()
    ```

- **resume()**: Resumes the elevator.
    ```java
    public void resume()
    ```

- **isPaused()**: Checks if the elevator is paused.
    ```java
    public boolean isPaused()
    ```

- **isValidFloor(int floor)**: Checks if the specified floor is valid.
    ```java
    private boolean isValidFloor(int floor)
    ```

- **startMoving()**: Starts the elevator's movement in a separate thread.
    ```java
    public void startMoving()
    ```

- **move()**: Handles the elevator's movement and stops at floors where buttons are pressed.
    ```java
    private void move()
    ```

- **moveToNextFloor()**: Moves the elevator to the next floor based on the current direction.
    ```java
    private void moveToNextFloor()
    ```

- **shouldStopAtCurrentFloor()**: Checks if the elevator should stop at the current floor.
    ```java
    private boolean shouldStopAtCurrentFloor()
    ```

- **stopAtCurrentFloor()**: Stops the elevator at the current floor and handles button presses.
    ```java
    private void stopAtCurrentFloor()
    ```

- **getCurrentFloor()**: Returns the current floor of the elevator.
    ```java
    public int getCurrentFloor()
    ```

- **getDirection()**: Returns the current direction of the elevator.
    ```java
    public Direction getDirection()
    ```

- **getLock()**: Returns the lock object used for synchronization.
    ```java
    public Object getLock()
    ```

### Example Usage

```java
import org.example.*;

public class ElevatorSimulationRun {
    public static void main(String[] args) {
        try {
            ExternalButtonTracker bt = new ExternalButtonTracker(); // Represents the external up/down button control tracker

            Cargo c1 = new Cargo(1, 200, 2, 7, bt);
            Cargo c2 = new Cargo(2, 2000, 4, 8, bt); // This must be a cargo of gold bars.
            Cargo c3 = new Cargo(3, 300, 3, 1, bt);
            Cargo c4 = new Cargo(4, 200, 2, 1, bt);
            Cargo c5 = new Cargo(5, 150, 12, 0, bt);

            // Initialize elevator for: 12 floors, 5 seconds per stop and a max weight of 1000 pounds
            Elevator elevator = new Elevator(12, 5, 1000, bt);

            elevator.startMoving(); // Elevator begins operation

            explicitWait(7);
            elevator.pause(); // Here, this is simulating someone pressing the emergency button
            explicitWait(8);
            elevator.resume(); // Here, this is simulating someone releasing the emergency button

            // The board() method starts a thread simulating when that passenger/cargo pressed the external button
            // on the floor they are originating from. In this case, they all press external button approx same time.
            c1.board(elevator);
            c2.board(elevator); // This is for an overweight cargo.
            c3.board(elevator);
            c4.board(elevator);
            c5.board(elevator); // This is for an invalid floor.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void explicitWait(int secs) {
        System.out.println(String.format("Explicit wait issues for %d secs...", secs));
        Util.wait(secs);
    }
}
```

### Key Classes

- **Elevator**: The main class simulating the elevator.
- **Cargo**: Represents the cargos that interact with the elevator.
- **ExternalButtonTracker**: Manages the external button presses.
- **Util**: Provides a utility method for waiting.
- **Direction**: Enum representing the direction of the elevator.

## Guiding Specifications and Assumptions:
- There is just 1 Elevator (not a bank of multiple Elevators). 
- The Elevator will keep track of which floors require a stop.
- Lowest floor is 1.  Max floor is N (configurable).  All intermediate floors are valid. 
- The available inputs to indicate which floor the Elevator stops are:
  - The external up button for a given floor is pressed and the Elevator headed that direction or is idle
  - The external down button for a given floor is pressed and the Elevator headed that direction or is idle
  - The internal button inside the elevator for that floor is pressed.
- There is one internal button for each floor for a maximum of N floors (configurable)
- There is one external up button and one external down botton per floor.
- It takes N (configurable) seconds per Elevator stop (to roughly simulate the delays with stops/loading/unloading/door open and close).
- Each floor door will open and close with each Elevator stop without doors being held up.
- NOTE: Theoretically, doors being held up and released can be modeled with independent calls to the Elevator pause() and resume() methods.
- The Elevator will keep going the same direction while buttons both internal and external are pressed in that direction.
- The Elevator will wait in place when no pressed buttons detected.
- The Elevator will reverse direction when no buttons are pressed in the current direction and there are some buttons presses in the reverse direction.
- When the Elevator stops in a given floor, the pressed indicator for that floor will be reset to not pressed (off).
- The Elevator will display a message for each floor that it stops.
- The Elevator will be initialized to start on Floor 1 with 0 weight.
- Door opening and closing are assumed when the Elevator hits a floor.
- Button pressed state for both internal and external for the same direction as the Elevator are reset when it stops at that floor.
- Sensors and other attributes typical of a real elevator are abstracted away to simplify the model.


## License

This project is licensed under the MIT License.
