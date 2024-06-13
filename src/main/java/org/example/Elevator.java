package org.example;

import java.util.HashSet;
import java.util.Set;

public class Elevator {
    private final int maxFloors;                   // Configurable max floors
    private final int stopDurationInSeconds;       // To simulate time it takes to stop at each floor
    private final int maxTotalWeightLbs;           // Configurable maximum total WeightLbs
    private final Set<Integer> externalUpButtons;
    private final Set<Integer> externalDownButtons;
    private final Set<Integer> internalButtons;
    private int currentFloor;
    private Direction direction;
    private int currentWeightLbs;                  // Track total current WeightLbs

    private final Object elevatorLock = new Object();
    private boolean isPaused;

    public Elevator(int maxFloors, int stopDurationInSeconds, int maxTotalWeightLbs) {
        this.maxFloors = maxFloors;
        this.stopDurationInSeconds = stopDurationInSeconds;
        this.maxTotalWeightLbs = maxTotalWeightLbs;
        this.externalUpButtons = new HashSet<>();
        this.externalDownButtons = new HashSet<>();
        this.internalButtons = new HashSet<>();
        this.currentFloor = 1;
        this.direction = Direction.UP;
        this.currentWeightLbs = 0;
    }

    public void pressExternalUpButton(int floor) {
        if (isValidFloor(floor))
            externalUpButtons.add(floor);
    }

    public void pressExternalDownButton(int floor) {
        if (isValidFloor(floor))
            externalDownButtons.add(floor);
    }

    public void pressInternalButton(int floor) {
        if (isValidFloor(floor))
            internalButtons.add(floor);
    }

    public void addWeightLbs(int WeightLbs) {
        synchronized (this.elevatorLock) {
            currentWeightLbs += WeightLbs;
            if (currentWeightLbs > maxTotalWeightLbs) {
                pause();
            }
        }
    }

    public void reduceWeightLbs(int WeightLbs) {
        synchronized (this.elevatorLock) {
            currentWeightLbs -= WeightLbs;
            if (currentWeightLbs <= maxTotalWeightLbs && isPaused) {
                resume();
            }
        }
    }

    public int getGetCurrentWeightLbs() {
        synchronized (this.elevatorLock) {
            return currentWeightLbs;
        }
    }

    public void pause() {
        synchronized (elevatorLock) {
            System.out.println("Elevator paused.");
            isPaused = true;
        }
    }

    public void resume() {
        synchronized (elevatorLock) {
            isPaused = false;
            elevatorLock.notifyAll(); // Notify all waiting threads
            System.out.println("Elevator resumed.");
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    private boolean isValidFloor(int floor) {
        if (floor < 1 || floor > maxFloors) {
            System.out.println("Ignoring input due to invalid floor: " + floor);
            return false;
        }
        return true;
    }

    public void startMoving() {
        Thread elevatorThread = new Thread(() -> {
            while (true) {
                synchronized (this.elevatorLock) {
                    while (isPaused) {
                        try {
                            this.elevatorLock.wait(); // Wait til notified
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.move();
            }
        });
        elevatorThread.start();
    }

    private void move() {
        try {
            while (true) {
                synchronized (elevatorLock) {
                    while (isPaused) {
                        elevatorLock.wait(); // Wait until resumed
                    }
                }
                while (!(externalUpButtons.isEmpty() && externalDownButtons.isEmpty() && internalButtons.isEmpty())) {
                    if (direction == Direction.UP && externalUpButtons.contains(currentFloor)) {
                        stopAtCurrentFloor();
                    } else if (direction == Direction.DOWN && externalDownButtons.contains(currentFloor)) {
                        stopAtCurrentFloor();
                    } else if (internalButtons.contains(currentFloor)) {
                        stopAtCurrentFloor();
                    }

                    if (direction == Direction.UP) {
                        if (currentFloor < maxFloors) {
                            currentFloor++;
                        } else {
                            direction = Direction.DOWN;
                        }
                    } else {
                        if (currentFloor > 1) {
                            currentFloor--;
                        } else {
                            direction = Direction.UP;
                        }
                    }
                }
                System.out.println("Elevator waiting.  No buttons pressed at the moment...");
                try {
                    Util.wait(5);
                } catch (Exception _) {}
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve the interrupt status
            System.out.println("Elevator thread interrupted. Exiting...");
        }
    }

    private void stopAtCurrentFloor() {
        System.out.println("Elevator stopped at floor " + currentFloor);
        Util.wait(stopDurationInSeconds);
        if (direction.equals(Direction.UP))
            externalUpButtons.remove(currentFloor);   // Unset external button in the direction of the elevator
        if (direction.equals(Direction.DOWN))
            externalDownButtons.remove(currentFloor); // Unset external button in the direction of the elevator
        internalButtons.remove(currentFloor);         // Unset buttons for current floor when Elevator stops there
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Object getLock() {
        return this.elevatorLock;
    }
}

