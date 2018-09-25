package org.droidplanner.android.singleton;

import android.content.Context;

public class SingletonTimer {
    private static SingletonTimer projectTimer = null;
    private Context context;
    private int counter;
    private boolean timerRunnable;
    private boolean startCounter = false;

    public boolean isStartCounter() {
        return startCounter;
    }

    public void setStartCounter(boolean startCounter) {
        this.startCounter = startCounter;
    }

    public boolean isTimerRunnable() {
        return timerRunnable;
    }

    public void setTimerRunnable(boolean timerRunnable) {
        this.timerRunnable = timerRunnable;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    private SingletonTimer(Context context) {
        this.context = context;
        this.counter = 0;
        this.timerRunnable = true;
    }

    public static SingletonTimer getInstance(Context context) {
        if (projectTimer == null) {
            projectTimer = new SingletonTimer(context);
        }
        return projectTimer;
    }
}