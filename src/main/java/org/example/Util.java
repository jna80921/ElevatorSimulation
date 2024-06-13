package org.example;

public class Util {
    static public void wait(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (Exception _) {
        }
    }
}
