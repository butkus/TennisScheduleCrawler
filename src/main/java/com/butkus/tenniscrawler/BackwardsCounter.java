package com.butkus.tenniscrawler;

public class BackwardsCounter {

    private int counter;
    private boolean enabled;

    public BackwardsCounter() {
        init();
    }

    public boolean isOn() {
        return counter > 0;
    }

    public BackwardsCounter enableOnceFor(int setTo) {
        if (!enabled) {
            enabled = true;
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
        init();
    }

    private void init() {
        counter = 0;
        enabled = false;
    }
}
