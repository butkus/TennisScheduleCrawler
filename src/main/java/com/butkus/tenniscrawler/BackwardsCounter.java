package com.butkus.tenniscrawler;

public class BackwardsCounter {

    private int counter;

    public BackwardsCounter() {
        this.counter = 0;
    }

    public boolean isEnabled() {
        return counter > 0;
    }

    public BackwardsCounter enableFor(int setTo) {
        if (counter == 0) {
            counter = setTo;
        }
        return this;
    }

    public void crawls() {
        // for readability only
    }

    public void decrement() {
        if (counter > 0) counter--;
    }

    public void disable() {
        counter = 0;
    }

    @Override
    public String toString() {
        return "BackwardsCounter{" +
                "counter=" + counter +
                '}';
    }
}
