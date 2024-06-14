package org.example;

class ElevatorSimulationRun {
    public static void main(String[] args) {
        try {

            ExternalButtonTracker bt = new ExternalButtonTracker();  // Represents the external up/down button control tracker

            Cargo c1 = new Cargo(1, 200, 2, 7, bt);
            Cargo c2 = new Cargo(2, 2000, 4, 8, bt);  // This must be a cargo of gold bars.
            Cargo c3 = new Cargo(3, 300, 3, 1, bt);
            Cargo c4 = new Cargo(4, 200, 2, 1, bt);
            Cargo c5 = new Cargo(5, 150, 12, 0, bt);

            // Initialize elevator for: 12 floors, 5 seconds per stop and a max weight of 1000 pounds
            Elevator elevator = new Elevator(12, 5, 1000, bt);

            elevator.startMoving();  // Elevator begins operation

            explicitWait(7);
            elevator.pause();  // Here, this is simulating someone pressing the emergency button
            explicitWait(8);
            elevator.resume(); // Here, this is simulating someone releasing the emergency button

            // The board() method starts a thread simulating when that passenger/cargo pressed the external button
            // on the floor they are originating from.  In this case, they all press external button approx same time.
            c1.board(elevator);
            c2.board(elevator);  // This is for an overweight cargo.
            c3.board(elevator);
            c4.board(elevator);
            c5.board(elevator);  // This is for an invalid floor.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void explicitWait(int secs) {
        System.out.println(String.format("Explicit wait issues for %d secs...", secs));
        Util.wait(secs);
    }
}