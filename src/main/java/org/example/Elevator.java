package org.example;

import java.util.HashSet;
import java.util.Set;

public class Elevator {
    private final int maxFloors;
    private final int stopDurationInSeconds;
    private final int maxTotalWeightLbs;
    private final ExternalButtonTracker externalButtonTracker; // Use ExternalButtonTracker
    private final Set<Integer> internalButtons;
    private int currentFloor;
    private Direction direction;
    private int currentWeightLbs;

    private final Object elevatorLock = new Object();
    private boolean isPaused;

    public Elevator(int maxFloors, int stopDurationInSeconds, int maxTotalWeightLbs, ExternalButtonTracker externalButtonTracker) {
        this.maxFloors = maxFloors;
        this.stopDurationInSeconds = stopDurationInSeconds;
        this.maxTotalWeightLbs = maxTotalWeightLbs;
        this.externalButtonTracker = externalButtonTracker;
        this.internalButtons = new HashSet<>();
        this.currentFloor = 1;
        this.direction = Direction.UP;
        this.currentWeightLbs = 0;
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

    private void moveToNextFloor() {
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

    private void move() {
        try {
            while (true) {
                synchronized (elevatorLock) {
                    while (isPaused) {
                        elevatorLock.wait(); // Wait until resumed
                    }
                }
                while (!(externalButtonTracker.isEmpty() && internalButtons.isEmpty())) {
                    if (shouldStopAtCurrentFloor()) {
                        stopAtCurrentFloor();
                    }
                    moveToNextFloor();
                }
                System.out.println("Elevator waiting. No buttons pressed at the moment...");
                Util.wait(5);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve the interrupt status
            System.out.println("Elevator thread interrupted. Exiting...");
        }
    }

    private boolean shouldStopAtCurrentFloor() {
        return (direction == Direction.UP && externalButtonTracker.hasUpButton(currentFloor)) ||
                (direction == Direction.DOWN && externalButtonTracker.hasDownButton(currentFloor)) ||
                internalButtons.contains(currentFloor);
    }

    private void stopAtCurrentFloor() {
        System.out.println("Elevator stopped at floor " + currentFloor);
        Util.wait(stopDurationInSeconds);
        if (direction.equals(Direction.UP))
            externalButtonTracker.clearUpButton(currentFloor);   // Unset external button in the direction of the elevator
        if (direction.equals(Direction.DOWN))
            externalButtonTracker.clearDownButton(currentFloor); // Unset external button in the direction of the elevator
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


