package org.example;

import java.util.HashSet;
import java.util.Set;

public class ExternalButtonTracker {
    private final Set<Integer> upButtons;
    private final Set<Integer> downButtons;

    public ExternalButtonTracker() {
        this.upButtons = new HashSet<>();
        this.downButtons = new HashSet<>();
    }

    public synchronized void pressUpButton(int floor) {
        upButtons.add(floor);
    }

    public synchronized void pressDownButton(int floor) {
        downButtons.add(floor);
    }

    public synchronized void clearUpButton(int floor) {
        upButtons.remove(floor);
    }

    public synchronized void clearDownButton(int floor) {
        downButtons.remove(floor);
    }

    public synchronized boolean hasUpButton(int floor) {
        return upButtons.contains(floor);
    }

    public synchronized boolean hasDownButton(int floor) {
        return downButtons.contains(floor);
    }

    public synchronized boolean isEmpty() {
        return upButtons.isEmpty() && downButtons.isEmpty();
    }
}



