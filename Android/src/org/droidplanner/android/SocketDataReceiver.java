package org.droidplanner.android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.droidplanner.android.activities.interfaces.SocketCallBackListener;
import org.droidplanner.android.constant.code.ConstantCode;
import org.droidplanner.android.customScheduler.CustomScheduleAdapter;
import org.droidplanner.android.customScheduler.CustomScheduleModel;
import org.droidplanner.android.fragments.widget.telemetry.MiniWidgetAttitudeSpeedInfo;
import org.droidplanner.android.fragments.widget.telemetry.MiniWidgetFlightTimer;
import org.droidplanner.android.maps.GoogleMapFragment;
import org.droidplanner.android.singleton.SignletonGyroscopeUpdate;
import org.droidplanner.android.singleton.SignletonLatLng;
import org.droidplanner.android.singleton.SingletonTimer;
import org.droidplanner.android.wprecyclerview.WpAdapter;
import org.droidplanner.android.wprecyclerview.WpModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by Borhan Uddin on 3/29/2018.
 */

public class SocketDataReceiver {
    public static Socket mSocket;
    GoogleMapFragment googleMapFragment = new GoogleMapFragment();
    private Context context;
    private Activity activity;
    private SocketCallBackListener socketCallBackListener;

    private Boolean isConnected = true;
    private static String mUsername = "Borhan";
    private boolean isMe = false;
    private String TAG = "borhan SocketDataReceiver";

    TextToSpeech tts;
    private String speech_txt = "";


    public SocketDataReceiver(Activity activity, Context context, SocketCallBackListener socketCallBackListener) {
        this.context = context;
        this.socketCallBackListener = socketCallBackListener;
        this.activity = activity;
        //ChatApplication app = (ChatApplication) activity.getApplication();
        mSocket = this.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        mSocket.on("chat message", onNewMessage);

        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();

        LayoutInflater li = LayoutInflater.from(context);
        View wpV = li.inflate(R.layout.fragment_generic_mission_control, null);
        Button aiface = (Button) wpV.findViewById(R.id.mc_connectBtn);
        aiface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = "borhanreo";
                Log.d(TAG, "i am here");
            }
        });
    }


    public Socket getSocket() {
        Socket sSocket = null;
        try {
            sSocket = IO.socket(ConstantCode.SOCKET_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return sSocket;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(activity.getApplicationContext(), R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(activity.getApplicationContext(), R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(activity.getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data;
                    int control_type;
                    String username;
                    int message;
                    try {
                        data = new JSONObject((String) args[0]);
                        username = data.getString(ConstantCode.USERNAME_KEY);

                        if (username.equals(ConstantCode.USER)) {
                            String action_type = data.getString(ConstantCode.ACTION_TYPE);

                        } else if (username.equals(ConstantCode.USER_EAGLE_EYE)) {
                            String action_type = data.getString(ConstantCode.ACTION_TYPE);
                            if (action_type.equals(ConstantCode.ACTION_BATTERY)) {
                                // battery and speed details
                                socketCallBackListener.backJsonString(ConstantCode.ACTION_BATTERY, data.toString());
                            }
                            if (action_type.equals(ConstantCode.ACTION_LOCATION)) {
                                try {
                                    double lat = data.getDouble(ConstantCode.LAT);
                                    double lng = data.getDouble(ConstantCode.LNG);
                                    SignletonLatLng.getInstance(context).setLat(lat);
                                    SignletonLatLng.getInstance(context).setLon(lng);
                                    //Log.d("ferdib", "SDR " + String.valueOf(lat) + " " + String.valueOf(lng));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                socketCallBackListener.backJsonString(ConstantCode.ACTION_LOCATION, data.toString());
                            }
                            if (action_type.equals(ConstantCode.ACTION_GPSINFO)) {
                                socketCallBackListener.backJsonString(ConstantCode.ACTION_GPSINFO, data.toString());
                            }
                            if (action_type.equals(ConstantCode.MODE_CHANGE)) {
                                JSONObject mode = new JSONObject((String) data.toString());
                                String modeName = mode.getString(ConstantCode.DATA_STRING);
                                Toast.makeText(context, "Res Change Mode to " + modeName, Toast.LENGTH_LONG).show();
                                speech_txt = " Change Mode to " + modeName;
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.WAY_RES)) {
                                Toast.makeText(context, " Waypoint uploaded ", Toast.LENGTH_LONG).show();
                                speech_txt = " Waypoint uploaded to drone";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.IS_ARMED)) {
                                //Toast.makeText(context," Waypoint uploaded ",Toast.LENGTH_LONG).show();
                                speech_txt = "armed";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.ACTION_TAKEOFF)) {
                                //Toast.makeText(context," Waypoint uploaded ",Toast.LENGTH_LONG).show();
                                speech_txt = "takeoff ";
                                speech_content();
                                SingletonTimer.getInstance(context).setStartCounter(true);
                            }
                            if (action_type.equals(ConstantCode.ACTION_TIMEROFF)) {
                                SingletonTimer.getInstance(context).setStartCounter(false);
                            }
                            if (action_type.equals(ConstantCode.NO_WAYPOINT)) {
                                //Toast.makeText(context," Waypoint uploaded ",Toast.LENGTH_LONG).show();
                                speech_txt = "No Waypoint upload ";
                                speech_content();

                            }
                            // all scheduler response 17-8-18
                            final AlertDialog.Builder allSchedulerAlert = new AlertDialog.Builder(context);
                            List<CustomScheduleModel> CustomScheduleModel = new ArrayList<>();

                            if (action_type.equals(ConstantCode.ACTION_ALL_SCHEDULE_RES)) {
                                String array = data.getString(ConstantCode.ARRAY_SCH);
                                JSONArray jsonArray = new JSONArray(array);
                                JSONObject jsonObject = new JSONObject();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    String sch_name = jsonObject.getString(ConstantCode.S_NAME);
                                    String id = jsonObject.getString(ConstantCode.ID);
                                    String drone_power_on = jsonObject.getString(ConstantCode.DRONE_POWER_ON);
                                    String flight_start = jsonObject.getString(ConstantCode.FLIGHT_START);

                                    Date objDate = new java.util.Date((Long.valueOf(drone_power_on) * 1000L) - 21600);
                                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                    String date = sdf.format(objDate);
                                    SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("hh:mm:ss");
                                    String startTime = sdfTime.format(objDate);
                                    Date flight = new java.util.Date((Long.valueOf(flight_start) * 1000L) - 21600);
                                    SimpleDateFormat sdfFlight = new java.text.SimpleDateFormat("hh:mm:ss");
                                    String flightTime = sdfTime.format(flight);
                                    CustomScheduleModel.add(new CustomScheduleModel(id, sch_name, date, startTime, flightTime, "dummy", "dummy"));
                                }

                                LayoutInflater layoutInflater = LayoutInflater.from(context);
                                View cusSchView = layoutInflater.inflate(R.layout.custom_schedule_rec_view, null);

                                RecyclerView recyclerViewSchedule = (RecyclerView) cusSchView.findViewById(R.id.customRecView);
                                CustomScheduleAdapter customScheduleAdapter = new CustomScheduleAdapter(context, CustomScheduleModel);
                                recyclerViewSchedule.setAdapter(customScheduleAdapter);
                                recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(context));
                                allSchedulerAlert.setView(cusSchView);
                                allSchedulerAlert.show();
                            }

                            if (action_type.equals(ConstantCode.ACTION_ABC)) {
                                rcvMap();
                            }
                            //wp schedule create
                            final AlertDialog.Builder alert_scheduler = new AlertDialog.Builder(context);
                            //alert_scheduler.setTitle("        DRONE PATH LIST");
                            List<WpModel> WpModel = new ArrayList<>();

                            if (action_type.equals(ConstantCode.ACTION_WP_SCHEDULE)) {
                                //{"action": "wp_schedule", "array": "[{\"id\": 1, \"s_name\": \"qqqq\"}, {\"id\": 2, \"s_name\": \"aaaa\"}]", "u": "eagle"}
                                String array = data.getString(ConstantCode.ARRAY_SCH_2);
                                JSONArray jsonArray = new JSONArray(array);
                                JSONObject jsonObject = new JSONObject();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    String sch_name = jsonObject.getString(ConstantCode.S_NAME_2);
                                    String sch_id = jsonObject.getString(ConstantCode.ID_2);

                                    WpModel.add(new WpModel(sch_id, sch_name, "", ""));
                                }
                                LayoutInflater li = LayoutInflater.from(context);
                                View wpV = li.inflate(R.layout.alertwpname, null);
                                Button settime = (Button) wpV.findViewById(R.id.settime);
                                settime.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Calendar now = Calendar.getInstance();
                                        int year = now.get(Calendar.YEAR);
                                        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
                                        int day = now.get(Calendar.DAY_OF_MONTH);
                                        int hour = now.get(Calendar.HOUR_OF_DAY);
                                        int minutes = now.get(Calendar.MINUTE);
                                        int second = now.get(Calendar.SECOND);

                                        String set_time = "{\"u\":\"g\",\"135\":\"119\",\"year\":" + year + ",\"month\":" + month + ",\"day\":" + day + "," +
                                                "\"hour\":" + hour + ",\"minutes\":" + minutes + ",\"second\":" + second + "}";
                                        SocketDataReceiver.attemptSend(set_time);
                                    }
                                });

                                RecyclerView recyclerView = (RecyclerView) wpV.findViewById(R.id.nameRv);
                                WpAdapter adapter = new WpAdapter(context, WpModel);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                alert_scheduler.setView(wpV);
                                alert_scheduler.show();
                            }
                            if (action_type.equals(ConstantCode.ACTION_RES_SCHEDULE)) {
                                speech_txt = "schedule created";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.ACTION_DEL_WP_ID_RES)) {
                                speech_txt = "Waypoint deleted";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.ACTION_DEL_SCH_ID_RES)) {
                                speech_txt = "Schedule deleted";
                                speech_content();
                            }

                            if (action_type.equals(ConstantCode.ACTION_GYROSCOPE)) {
                                //JSONObject jsonObject = new JSONObject( data.toString());
                                float pitch = Float.parseFloat(data.getString(ConstantCode.PITCH));
                                float roll = Float.parseFloat(data.getString(ConstantCode.ROLL));
                                float yaw = Float.parseFloat(data.getString(ConstantCode.YAW));


                                double horizontal_speed = data.getDouble(ConstantCode.AIR_SPEED);
                                double vertical_speed = data.getDouble(ConstantCode.GROUND_SPEED);
                                SignletonGyroscopeUpdate.getInstance(context).setHorizontal_speed(horizontal_speed);
                                SignletonGyroscopeUpdate.getInstance(context).setVertical_speed(vertical_speed);


                                SignletonGyroscopeUpdate.getInstance(context).setPitch(pitch);
                                SignletonGyroscopeUpdate.getInstance(context).setRoll(roll);
                                SignletonGyroscopeUpdate.getInstance(context).setYaw(yaw);
                                //miniWidgetAttitudeSpeedInfo.updateGyro();
                            }
                            if (action_type.equals(ConstantCode.START_OBJ)) {
                                //socketCallBackListener.backJsonString(ConstantCode.START_OBJ, data.toString());
                                speech_txt = "Object Detection On";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.STOP_OBJ)) {
                                //socketCallBackListener.backJsonString(ConstantCode.STOP_OBJ, data.toString());
                                speech_txt = "Object Detection Off";
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.ACTION_DRONE_MOVEMENT)) {
                                String movement = data.getString(ConstantCode.MOVEMENT_DIRECTION);
                                speech_txt = movement;
                                speech_content();
                            }
                            if (action_type.equals(ConstantCode.FACE_CAPTURE_RES)) {

                                String array = data.getString("str");
                                JSONArray jsonArray = new JSONArray(array);
                                String[] faceName = new String[100];
                                String sp = "hi ";
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Log.d(TAG, jsonArray.getString(i));
                                        String speak = jsonArray.getString(i);
                                        sp = sp + " " + speak;
                                        //faceName[i] = speak;
                                        //speech_txt = "hi " + speak;
                                        //speech_content();
                                    }
                                    speech_txt = sp + "Have a nice day";
                                    speech_content();
                                }
                            }
                            if (action_type.equals(ConstantCode.FACE_DETECT_START)) {
                                //speech_txt = "Face Detected";
                                //speech_content();
                                Toast.makeText(context, "Face Detected", Toast.LENGTH_LONG).show();
                            }
                            if (action_type.equals(ConstantCode.FACE_CAPTURE_START)) {
                                //speech_txt = "Face Captured";
                                //speech_content();
                                Toast.makeText(context, "Face Captured", Toast.LENGTH_LONG).show();
                            }
                            if (action_type.equals(ConstantCode.FACE_CAPTURE_RES_JOHN)) {
                                speech_txt = "Hi John. Welcome to the office. We are very excited to have you with us. How was your trip to Cox's Bazar? ";
                                speech_content();
                                String johnFace = "John Detected";
                                Toast.makeText(context, johnFace, Toast.LENGTH_LONG).show();
                            }
                            if (action_type.equals(ConstantCode.FACE_NONE)) {
                                String noFace = "No Face Detected";
                                Toast.makeText(context, noFace, Toast.LENGTH_LONG).show();
                                //speech_txt = noFace;
                                //speech_content();
                            }
                        }
                    } catch (JSONException e) {

                    }
                }
            });
        }
    };

    private void speech_content() {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("error", "This Language is not supported");
                    } else {

                        ConvertTextToSpeech(speech_txt);
                    }
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });

        tts.setSpeechRate(.8f);
    }


    private void rcvMap() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Drone Position Tracker");

        WebView wv = new WebView(context);
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        String serverUrl = "http://184.72.95.87:3000/drone";
        wv.loadUrl(serverUrl);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        alert.setView(wv);
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void ConvertTextToSpeech(String text) {
        if (text == null || "".equals(text)) {
            //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    // addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    /*addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);*/
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    //addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    ///removeTyping(username);
                }
            });
        }
    };


    public static void attemptSend(String message) {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;
        mSocket.emit("chat message", message);
    }



    /*
    private void showAlertRecycler(String id, String name, String date, String time){
        final AlertDialog.Builder alert_scheduler = new AlertDialog.Builder(context);
        alert_scheduler.setTitle("        DRONE PATH LIST");

        List<WpModel> WpModel = new ArrayList<>();

        WpModel.add(new WpModel("test1", "date 1", "time1", ""));
        WpModel.add(new WpModel("test2", "date 2", "time2", ""));
        WpModel.add(new WpModel("test3", "date 3", "time3", ""));
        WpModel.add(new WpModel("test4", "date 4", "time4", ""));
        WpModel.add(new WpModel("test5", "date 5", "time5", ""));
        WpModel.add(new WpModel("test6", "date 6", "time6", ""));
        WpModel.add(new WpModel("test7", "date 7", "time7", ""));
        WpModel.add(new WpModel("test8", "date 8", "time8", ""));
        WpModel.add(new WpModel("test9", "date 9", "time9", ""));
        WpModel.add(new WpModel("test10", "date 10", "time10", ""));



        LayoutInflater li = LayoutInflater.from(context);
        View wpV = li.inflate(R.layout.alertwpname, null);

        RecyclerView recyclerView = (RecyclerView) wpV.findViewById(R.id.nameRv);
        WpAdapter adapter = new WpAdapter(context, WpModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        alert_scheduler.setView(wpV);
        alert_scheduler.show();

    }
     */
}
