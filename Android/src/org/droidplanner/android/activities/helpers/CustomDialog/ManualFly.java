package org.droidplanner.android.activities.helpers.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.droidplanner.android.R;
import org.droidplanner.android.SocketDataReceiver;
import org.droidplanner.android.activities.helpers.JsonBuilder;
import org.droidplanner.android.activities.interfaces.ControlCallBackListener;
import org.droidplanner.android.activities.interfaces.ManualCallbackListener;
import org.droidplanner.android.constant.code.ConstantCode;
import org.droidplanner.android.singleton.SingletonRC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Borhan Uddin on 7/18/2018.
 */
public class ManualFly extends Dialog {
    public Activity c;
    private Context context = getContext();
    private String TAG = "borhan ManualFly";
    public Dialog d;
    public Button yes;
    public Spinner spinner;
    private static final String[] paths = {"LEFT", "RIGHT"};
    private ManualCallbackListener manualCallbackListener;
    private int leftorright = 0;
    private EditText pwm, time;
    private int pitchBar = 0, pitchMax = 85, pitchMin = 35;
    private int rollBar = 0, rollhMax = 85, rollMin = 35;
    private int throttleBar = 0, throttleMax = 85, throttleMin = 35;
    private int yawBar = 0, yawMax = 85, yawMin = 35;
    private int middleValue = 60;
    DiscreteSeekBar discreteSeekBarPitch, discreteSeekBarRoll, discreteSeekBarThrottle, discreteSeekBarYaw, discreteSeekBarRadio5, discreteSeekBarRadio6, discreteSeekBarRadio7, discreteSeekBarRadio8;
    private int channel_0 = 60, channel_1 = 60, channel_2 = 35, channel_3 = 60, channel_4 = 35, channel_5 = 35, channel_6 = 35, channel_7 = 35;
    private Button stabilize, guided, land, armed, disarmed, auto;

    public ManualFly(Activity a, ManualCallbackListener manualCallbackListener) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.manualCallbackListener = manualCallbackListener;
    }

    private JSONObject jsonobjectManuall() throws JSONException {
        JSONObject json = null;

        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.MANUAL_FLY + "\"" + ","
                + "\"" + ConstantCode.CHANNEL_0 + "\":" + "\"" + channel_0 + "\","
                + "\"" + ConstantCode.CHANNEL_1 + "\":" + "\"" + channel_1 + "\","
                + "\"" + ConstantCode.CHANNEL_2 + "\":" + "\"" + channel_2 + "\","
                + "\"" + ConstantCode.CHANNEL_3 + "\":" + "\"" + channel_3 + "\","
                + "\"" + ConstantCode.CHANNEL_4 + "\":" + "\"" + channel_4 + "\","
                + "\"" + ConstantCode.CHANNEL_5 + "\":" + "\"" + channel_5 + "\","
                + "\"" + ConstantCode.CHANNEL_6 + "\":" + "\"" + channel_6 + "\","
                + "\"" + ConstantCode.CHANNEL_7 + "\":" + "\"" + channel_7 + "\"" +
                "}");
        return json;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.manual_oparate_dialog);
        discreteSeekBarPitch = (DiscreteSeekBar) findViewById(R.id.simpleSeekBar);
        discreteSeekBarRoll = (DiscreteSeekBar) findViewById(R.id.simpleSeekBar2);
        discreteSeekBarThrottle = (DiscreteSeekBar) findViewById(R.id.simpleSeekBar3);
        discreteSeekBarYaw = (DiscreteSeekBar) findViewById(R.id.simpleSeekBar4);
        discreteSeekBarRadio5 = (DiscreteSeekBar) findViewById(R.id.simpleSeekBarRadio5);
        discreteSeekBarRadio6 = (DiscreteSeekBar) findViewById(R.id.simpleSeekBarRadio6);
        discreteSeekBarRadio7 = (DiscreteSeekBar) findViewById(R.id.simpleSeekBarRadio7);
        discreteSeekBarRadio8 = (DiscreteSeekBar) findViewById(R.id.simpleSeekBarRadio8);
        stabilize = (Button) findViewById(R.id.stabilize);
        guided = (Button) findViewById(R.id.guided);
        land = (Button) findViewById(R.id.land);
        armed = (Button) findViewById(R.id.armed);
        disarmed = (Button) findViewById(R.id.disarmed);
        auto = (Button) findViewById(R.id.auto);
        initComponents();

        channel_0 = SingletonRC.getInstance(context).getChannel_0();
        channel_1 = SingletonRC.getInstance(context).getChannel_1();
        channel_2 = SingletonRC.getInstance(context).getChannel_2();
        channel_3 = SingletonRC.getInstance(context).getChannel_3();
        channel_4 = SingletonRC.getInstance(context).getChannel_4();
        channel_5 = SingletonRC.getInstance(context).getChannel_5();
        channel_6 = SingletonRC.getInstance(context).getChannel_6();
        channel_7 = SingletonRC.getInstance(context).getChannel_7();

        int c0 = SingletonRC.getInstance(context).getChannel_0();
        int c1 = SingletonRC.getInstance(context).getChannel_1();
        int c2 = SingletonRC.getInstance(context).getChannel_2();
        int c3 = SingletonRC.getInstance(context).getChannel_3();
        int c4 = SingletonRC.getInstance(context).getChannel_4();
        int c5 = SingletonRC.getInstance(context).getChannel_5();
        int c6 = SingletonRC.getInstance(context).getChannel_6();
        int c7 = SingletonRC.getInstance(context).getChannel_7();

        stabilize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketDataReceiver.attemptSend(JsonBuilder.getModeChanger("STABILIZE").toString());
            }
        });
        guided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketDataReceiver.attemptSend(JsonBuilder.getModeChanger("GUIDED").toString());
            }
        });
        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketDataReceiver.attemptSend(JsonBuilder.getModeChanger("LAND").toString());
            }
        });
        armed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketDataReceiver.attemptSend(JsonBuilder.armed_or_disarmed(ConstantCode.ACTION_ARMED).toString());
            }
        });
        disarmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketDataReceiver.attemptSend(JsonBuilder.armed_or_disarmed(ConstantCode.ACTION_DISARMED).toString());
            }
        });
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SocketDataReceiver.attemptSend(JsonBuilder.getModeChanger("AUTO").toString());
                String aiface = "{\"u\":\"g\",\"135\":\"130\",\"142\":\"s1\"}";
                SocketDataReceiver.attemptSend(aiface);
            }
        });
        discreteSeekBarPitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        discreteSeekBarPitch.setProgress(middleValue);
                        break;
                }
                return false;
            }
        });
        discreteSeekBarRoll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        discreteSeekBarRoll.setProgress(middleValue);
                        break;
                }
                return false;
            }
        });
        discreteSeekBarThrottle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //discreteSeekBarThrottle.setProgress(middleValue);
                        break;
                }
                return false;
            }
        });
        discreteSeekBarYaw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        discreteSeekBarYaw.setProgress(middleValue);
                        break;
                }
                return false;
            }
        });
        discreteSeekBarPitch.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_0 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_0(value);
                try {

                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarRoll.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_1 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_1(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarThrottle.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_2 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_2(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarYaw.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_3 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_3(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarRadio5.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_4 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_4(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarRadio6.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_5 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_5(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarRadio7.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                channel_6 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_6(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
        discreteSeekBarRadio8.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {

                channel_7 = (int) ((value * 4.5) + 150);
                SingletonRC.getInstance(context).setChannel_7(value);
                try {
                    SocketDataReceiver.attemptSend(jsonobjectManuall().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });

        discreteSeekBarPitch.setProgress(c0);
        discreteSeekBarRoll.setProgress(c1);
        discreteSeekBarThrottle.setProgress(c2);
        discreteSeekBarYaw.setProgress(c3);
        discreteSeekBarRadio5.setProgress(c4);
        discreteSeekBarRadio6.setProgress(c5);
        discreteSeekBarRadio7.setProgress(c6);
        discreteSeekBarRadio8.setProgress(c7);
    }

    public void initComponents() {
        //Max Min value
        discreteSeekBarPitch.setMax(pitchMax);
        discreteSeekBarPitch.setMin(pitchMin);
        discreteSeekBarRoll.setMax(rollhMax);
        discreteSeekBarRoll.setMin(rollMin);
        discreteSeekBarThrottle.setMax(throttleMax);
        discreteSeekBarThrottle.setMin(throttleMin);
        discreteSeekBarYaw.setMax(yawMax);
        discreteSeekBarYaw.setMin(yawMin);
        discreteSeekBarRadio5.setMax(yawMax);
        discreteSeekBarRadio5.setMin(yawMin);
        discreteSeekBarRadio6.setMax(yawMax);
        discreteSeekBarRadio6.setMin(yawMin);
        discreteSeekBarRadio7.setMax(yawMax);
        discreteSeekBarRadio7.setMin(yawMin);
        discreteSeekBarRadio8.setMax(yawMax);
        discreteSeekBarRadio8.setMin(yawMin);
        discreteSeekBarYaw.setMax(yawMax);
        discreteSeekBarYaw.setMin(yawMin);
        //set Default value progress
        discreteSeekBarPitch.setProgress(middleValue);
        discreteSeekBarRoll.setProgress(middleValue);
        discreteSeekBarThrottle.setProgress(throttleMin);
        discreteSeekBarYaw.setProgress(middleValue);
    }
}
