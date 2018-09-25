package org.droidplanner.android.singleton;

import android.content.Context;

/**
 * Created by Borhan Uddin on 8/30/2018.
 */
public class SignletonGyroscopeUpdate {
    private static SignletonGyroscopeUpdate projectSingleton = null;
    private Context context;
    private float pitch,roll,yaw;
    private boolean kotlinRunnable;
    private double horizontal_speed,vertical_speed;
    private SignletonGyroscopeUpdate(Context context) {
        this.context = context;
        pitch= (float) 0.00;
        roll= (float) 0.00;
        yaw= (float) 0.00;
        kotlinRunnable=true;
    }
    public static SignletonGyroscopeUpdate getInstance(Context context) {
        if (projectSingleton == null) {
            projectSingleton = new SignletonGyroscopeUpdate(context);
        }
        return projectSingleton;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isKotlinRunnable() {
        return kotlinRunnable;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setKotlinRunnable(boolean kotlinRunnable) {
        this.kotlinRunnable = kotlinRunnable;
    }

    public double getHorizontal_speed() {
        return horizontal_speed;
    }

    public double getVertical_speed() {
        return vertical_speed;
    }

    public void setHorizontal_speed(double horizontal_speed) {
        this.horizontal_speed = horizontal_speed;
    }

    public void setVertical_speed(double vertical_speed) {
        this.vertical_speed = vertical_speed;
    }
}
