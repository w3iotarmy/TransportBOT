package org.droidplanner.android.singleton;

import android.content.Context;

public class SingletonRC {
    private static SingletonRC instanceSingletonRC = null;
    private Context context;
    private int channel_0;
    private int channel_1;
    private int channel_2;
    private int channel_3;
    private int channel_4;
    private int channel_5;
    private int channel_6;
    private int channel_7;

    public int getChannel_0() {
        return channel_0;
    }

    public void setChannel_0(int channel_0) {
        this.channel_0 = channel_0;
    }

    public int getChannel_1() {
        return channel_1;
    }

    public void setChannel_1(int channel_1) {
        this.channel_1 = channel_1;
    }

    public int getChannel_2() {
        return channel_2;
    }

    public void setChannel_2(int channel_2) {
        this.channel_2 = channel_2;
    }

    public int getChannel_3() {
        return channel_3;
    }

    public void setChannel_3(int channel_3) {
        this.channel_3 = channel_3;
    }

    public int getChannel_4() {
        return channel_4;
    }

    public void setChannel_4(int channel_4) {
        this.channel_4 = channel_4;
    }

    public int getChannel_5() {
        return channel_5;
    }

    public void setChannel_5(int channel_5) {
        this.channel_5 = channel_5;
    }

    public int getChannel_6() {
        return channel_6;
    }

    public void setChannel_6(int channel_6) {
        this.channel_6 = channel_6;
    }

    public int getChannel_7() {
        return channel_7;
    }

    public void setChannel_7(int channel_7) {
        this.channel_7 = channel_7;
    }

    private SingletonRC(Context context) {
        this.context = context;
        this.channel_0 = 60;
        this.channel_1 = 60;
        this.channel_2 = 35;
        this.channel_3 = 60;
        this.channel_4 = 35;
        this.channel_5 = 35;
        this.channel_6 = 35;
        this.channel_7 = 35;
    }

    public static SingletonRC getInstance(Context context) {
        if (instanceSingletonRC == null) {
            instanceSingletonRC = new SingletonRC(context);
        }
        return instanceSingletonRC;
    }
}