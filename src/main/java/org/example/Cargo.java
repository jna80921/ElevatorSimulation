package org.example;

public class Cargo {
    private final int id;
    private final int weightLbs;
    private final int startFloor;
    private final int targetFloor;
    private final Direction direction;

    public Cargo(int id, int weightLbs, int startFloor, int targetFloor) {
        this.id = id;
        this.weightLbs = weightLbs;
        this.startFloor = startFloor;
        this.targetFloor = targetFloor;
        if (startFloor < targetFloor)
            direction = Direction.UP;
        else
            direction = Direction.DOWN;
    }

    // Method to initiate the cargo boarding process, starting from pressing the external button
    public void board(Elevator elevator) {
        Thread cargoThread = new Thread(() -> {
            synchronized (elevator.getLock()) {
                while (elevator.isPaused()) {
                    try {
                        elevator.getLock().wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Preserve the interrupt status
                        System.out.println("Cargo boarding process interrupted. Exiting...");
                        return;
                    }
                }
            }
            startBoardingProcess(elevator);
        });
        cargoThread.start();
    }

    private void startBoardingProcess(Elevator elevator) {
        if (startFloor < targetFloor) {
            elevator.pressExternalUpButton(startFloor);
            System.out.println(String.format("Cargo %d press UP button from floor %d going to floor %d...", id, startFloor, targetFloor));
        } else {
            elevator.pressExternalDownButton(startFloor);
            System.out.println(String.format("Cargo %d press DOWN button from floor %d going to floor %d...", id, startFloor, targetFloor));
        }
        while (!(elevator.getCurrentFloor() == startFloor && elevator.getDirection() == this.direction))
            Util.wait(1);
        elevator.addWeightLbs(weightLbs);
        if (elevator.isPaused()) {           // Excess weight will trigger isPaused condition
            System.out.println(String.format("Max elevator cargo weight exceeded, cargo %s disembarking.", id));
            elevator.reduceWeightLbs(weightLbs);   // Assume if cargo causing excess weight will disembark
        } else {
            elevator.pressInternalButton(targetFloor);
            while (elevator.getCurrentFloor() != targetFloor)
                Util.wait(1);
            elevator.reduceWeightLbs(weightLbs);
        }
    }
}



