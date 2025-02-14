package com.thoth.client;

public class Counter {
    private static final int MODULUS = 1000000;
    private volatile int counter = 0;  // Volatile to ensure visibility across threads
    
    public synchronized int incrementAndGet() {
        counter = (counter + 1) % MODULUS; // Increment and apply modulusù
        return counter;
    }
}
