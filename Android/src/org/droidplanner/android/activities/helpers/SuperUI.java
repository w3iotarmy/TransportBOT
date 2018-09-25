package org.droidplanner.android.activities.helpers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.CapabilityApi;
import com.o3dr.android.client.apis.MissionApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.attribute.error.CommandExecutionError;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.model.SimpleCommandListener;

import org.droidplanner.android.AppService;
import org.droidplanner.android.DroidPlannerApp;

import org.droidplanner.android.R;
import org.droidplanner.android.SocketDataReceiver;
import org.droidplanner.android.activities.helpers.CustomDialog.ManualFly;
import org.droidplanner.android.activities.interfaces.ManualCallbackListener;
import org.droidplanner.android.activities.interfaces.SocketCallBackListener;
import org.droidplanner.android.constant.code.ConstantCode;
import org.droidplanner.android.dialogs.SlideToUnlockDialog;
import org.droidplanner.android.dialogs.SupportYesNoDialog;
import org.droidplanner.android.dialogs.SupportYesNoWithPrefsDialog;
import org.droidplanner.android.fragments.SettingsFragment;
import org.droidplanner.android.fragments.actionbar.VehicleStatusFragment;
import org.droidplanner.android.notifications.TTSNotificationProvider;
import org.droidplanner.android.proxy.mission.MissionProxy;
import org.droidplanner.android.utils.Utils;
import org.droidplanner.android.utils.prefs.DroidPlannerPrefs;
import org.droidplanner.android.utils.unit.UnitManager;
import org.droidplanner.android.utils.unit.systems.UnitSystem;
import org.droidplanner.android.wprecyclerview.WpAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Parent class for the app activity classes.
 */
public abstract class SuperUI extends AppCompatActivity implements DroidPlannerApp.ApiListener,
        SupportYesNoDialog.Listener, ServiceConnection {
    private TextView batteryTextview;
    private static final String MISSION_UPLOAD_CHECK_DIALOG_TAG = "Mission Upload check.";
    private static final IntentFilter superIntentFilter = new IntentFilter();
    private static final int CONNECT_CODE_VOICE = 12340;
    private static final int DISCONNECT_CODE_VOICE = 12341;
    private static final int ARMED_CODE_VOICE = 12342;
    private static final int DISARMED_CODE_VOICE = 12343;
    private ListView wordsList;
    private String TAG = "Borhan Class SuperUI";
    private SocketDataReceiver socketDataReceiver;
    private boolean isSensorEnable = false;
    private boolean isFlightModeAltEnable = false;
    private TTSNotificationProvider mTtsNotification;


    private Double voltage;
    private Double current;
    private int level;
    private Activity mActivity;
    private Context mContext;
    TextToSpeech tts;
    private String speech_txt = "";

    static {
        superIntentFilter.addAction(AttributeEvent.STATE_CONNECTED);
        superIntentFilter.addAction(AttributeEvent.STATE_DISCONNECTED);
        superIntentFilter.addAction(SettingsFragment.ACTION_ADVANCED_MENU_UPDATED);
    }

    private final BroadcastReceiver superReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case AttributeEvent.STATE_CONNECTED:
                    onDroneConnected();
                    break;

                case AttributeEvent.STATE_DISCONNECTED:
                    onDroneDisconnected();
                    break;

                case SettingsFragment.ACTION_ADVANCED_MENU_UPDATED:
                    supportInvalidateOptionsMenu();
                    break;
            }
        }
    };

    private ScreenOrientation screenOrientation = new ScreenOrientation(this);
    private LocalBroadcastManager lbm;

    /**
     * Handle to the app preferences.
     */
    protected DroidPlannerPrefs mAppPrefs;
    protected UnitSystem unitSystem;
    protected DroidPlannerApp dpApp;

    private VehicleStatusFragment statusFragment;

    @Override
    public void setContentView(int resId) {
        super.setContentView(resId);

        final int toolbarId = getToolbarId();
        final Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        initToolbar(toolbar);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        final int toolbarId = getToolbarId();
        final Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        initToolbar(toolbar);
    }

    protected void initToolbar(Toolbar toolbar) {
        if (toolbar == null)
            return;

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        addToolbarFragment();
    }

    public void setToolbarTitle(CharSequence title) {
        if (statusFragment == null)
            return;

        statusFragment.setTitle(title);
    }

    public void setToolbarTitle(int titleResId) {
        if (statusFragment == null)
            return;

        statusFragment.setTitle(getString(titleResId));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    protected void addToolbarFragment() {
        final int toolbarId = getToolbarId();
        final FragmentManager fm = getSupportFragmentManager();
        statusFragment = (VehicleStatusFragment) fm.findFragmentById(toolbarId);
        if (statusFragment == null) {
            statusFragment = new VehicleStatusFragment();
            fm.beginTransaction().add(toolbarId, statusFragment).commit();
        }
    }

    protected abstract int getToolbarId();

    SocketCallBackListener socketCallBackListener = new SocketCallBackListener() {
        @Override
        public void backJsonString(String code, String data) {
            /*
            if ((ConstantCode.ACTION_BATTERY).equals(code)) {

            }
             */


            /*Log.d(TAG, " socketCallBackListener "+ string+ " "+string.length());
            if((SocketServerCode.PWM_ENABLED+"").equals(string))
            {
                mTtsNotification.speak("PWM SIGNAL ENABLED",true,"");
                Toast.makeText(getApplication(), "PWM SIGNAL ENABLED",Toast.LENGTH_LONG).show();
            }else if((SocketServerCode.OBSTACLE_ENABLE+"").equals(string))
            {
                Toast.makeText(getApplication(), "OBSTACLE ENABLED",Toast.LENGTH_LONG).show();
            }else if((SocketServerCode.OBSTACLE_DISABLE+"").equals(string))
            {
                Toast.makeText(getApplication(), "OBSTACLE DISABLE",Toast.LENGTH_LONG).show();
            }else if((SocketServerCode.DRONE_LEFT_RIGHT_CONTROL_RESPONSE+"").equals(string))
            {
                Toast.makeText(getApplication(), "Successfully set the pwm and time ",Toast.LENGTH_LONG).show();
            }
            else if((SocketServerCode.DRONE_GET_LAT_LNG_REQUEST+"").equals(string))
            {
                //Toast.makeText(getApplication(), "Successfully set the pwm and time ",Toast.LENGTH_LONG).show();

                *//*double lat = MiniWidgetGeoInfo.instance.getReturnLat();
                double lng = MiniWidgetGeoInfo.instance.getReturnLng();*//*
             *//* Altitude droneAltitude = dpApp.getDrone().getAttribute(AttributeType.ALTITUDE);
                double vehicleAltitude = droneAltitude.getAltitude();
                Gps droneGps = dpApp.getDrone().getAttribute(AttributeType.GPS);
                LatLong vehiclePosition = droneGps.getPosition();

                double distanceFromHome =  0;
                vehiclePosition.set(new LatLongAlt(0.00,0.00,120));*//*

                double lat = 37.6178687783914;
                double lng = -122.373125553131;
                try {
                    SocketDataReceiver.attemptSend(jsonResultLatLng(ConstantCode.USER,ConstantCode.SENSOR_DISABLE,ConstantCode.DRONE_LAT_LNG_RESPONSE,"A",lat+"",lng+"").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if((SocketServerCode.DRONE_GET_OBJECT_DETECT+"").equals(string))
            {

                try {
                    JSONObject targetData = new JSONObject((String)data);
                    Double lat = targetData.getDouble(ConstantCode.LAT);
                    Double lng = targetData.getDouble(ConstantCode.LNG);
                    int alt = targetData.getInt(ConstantCode.ALT);
                    Log.d(TAG," Going to mode GUIDED target lat:"+lat+" lng:"+lng+" alt:"+alt);
                    LatLngAltModel latLngAltModel = GetCurrentPosition();
                    Log.d(TAG," Going to mode GUIDED current position lat:"+latLngAltModel.getLat()+" lng:"+latLngAltModel.getLng()+" alt:"+latLngAltModel.getAlt());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }*/
        }


    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        final Context context = getApplicationContext();


        dpApp = (DroidPlannerApp) getApplication();
        lbm = LocalBroadcastManager.getInstance(context);

        mAppPrefs = DroidPlannerPrefs.getInstance(context);
        unitSystem = UnitManager.getUnitSystem(context);
        mTtsNotification = new TTSNotificationProvider(context, dpApp.getDrone());
        /*
         * Used to supplant wake lock acquisition (previously in
         * org.droidplanner.android.service .MAVLinkService) as suggested by the
         * android android.os.PowerManager#newWakeLock documentation.
         */
        if (mAppPrefs.keepScreenOn()) {
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        screenOrientation.unlock();
        Utils.updateUILanguage(context);

        bindService(new Intent(context, AppService.class), this, Context.BIND_AUTO_CREATE);
        Activity activity = this;

        //socketDataReceiver = new SocketDataReceiver(activity, context, socketCallBackListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(this);
        lbm = null;
    }

    protected LocalBroadcastManager getBroadcastManager() {
        return lbm;
    }

    @Override
    public void onApiConnected() {
        invalidateOptionsMenu();

        getBroadcastManager().registerReceiver(superReceiver, superIntentFilter);
        if (dpApp.getDrone().isConnected())
            onDroneConnected();
        else
            onDroneDisconnected();

        lbm.sendBroadcast(new Intent(MissionProxy.ACTION_MISSION_PROXY_UPDATE));
    }

    @Override
    public void onApiDisconnected() {
        getBroadcastManager().unregisterReceiver(superReceiver);
        onDroneDisconnected();
    }

    protected void onDroneConnected() {
        invalidateOptionsMenu();
        screenOrientation.requestLock();
    }

    protected void onDroneDisconnected() {
        invalidateOptionsMenu();
        screenOrientation.unlock();
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Context context = getApplicationContext();
        unitSystem = UnitManager.getUnitSystem(context);
        dpApp.addApiListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        dpApp.removeApiListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_super_activity, menu);

        final MenuItem toggleConnectionItem = menu.findItem(R.id.menu_connect);

        Drone drone = dpApp.getDrone();
        if (drone == null || !drone.isConnected()) {
            menu.setGroupEnabled(R.id.menu_group_connected, false);
            menu.setGroupVisible(R.id.menu_group_connected, false);

            toggleConnectionItem.setTitle(R.string.menu_connect);

            return super.onCreateOptionsMenu(menu);
        }

        menu.setGroupEnabled(R.id.menu_group_connected, true);
        menu.setGroupVisible(R.id.menu_group_connected, true);

        final MenuItem killSwitchItem = menu.findItem(R.id.menu_kill_switch);
        killSwitchItem.setEnabled(false);
        killSwitchItem.setVisible(false);

        final boolean isKillEnabled = mAppPrefs.isKillSwitchEnabled();
        if (killSwitchItem != null && isKillEnabled) {
            CapabilityApi.getApi(drone).checkFeatureSupport(CapabilityApi.FeatureIds.KILL_SWITCH, new CapabilityApi.FeatureSupportListener() {
                @Override
                public void onFeatureSupportResult(String s, int i, Bundle bundle) {
                    switch (i) {
                        case CapabilityApi.FEATURE_SUPPORTED:
                            killSwitchItem.setEnabled(true);
                            killSwitchItem.setVisible(true);
                            break;
                        default:
                            killSwitchItem.setEnabled(false);
                            killSwitchItem.setVisible(false);
                            break;
                    }
                }
            });

        }

        final boolean areMissionMenusEnabled = enableMissionMenus();

        final MenuItem sendMission = menu.findItem(R.id.menu_upload_mission);
        sendMission.setEnabled(areMissionMenusEnabled);
        sendMission.setVisible(areMissionMenusEnabled);

        final MenuItem loadMission = menu.findItem(R.id.menu_download_mission);
        loadMission.setEnabled(areMissionMenusEnabled);
        loadMission.setVisible(areMissionMenusEnabled);

        toggleConnectionItem.setTitle(R.string.menu_disconnect);

        return super.onCreateOptionsMenu(menu);
    }

    protected boolean enableMissionMenus() {
        return false;
    }

    @Override
    public void onDialogYes(String dialogTag) {
        final Drone drone = dpApp.getDrone();
        final MissionProxy missionProxy = dpApp.getMissionProxy();

        switch (dialogTag) {
            case MISSION_UPLOAD_CHECK_DIALOG_TAG:
                missionProxy.addTakeOffAndRTL();
                missionProxy.sendMissionToAPM(drone);
                break;
        }
    }

    @Override
    public void onDialogNo(String dialogTag) {
        final Drone drone = dpApp.getDrone();
        final MissionProxy missionProxy = dpApp.getMissionProxy();

        switch (dialogTag) {
            case MISSION_UPLOAD_CHECK_DIALOG_TAG:
                missionProxy.sendMissionToAPM(drone);
                break;
        }
    }

    public String name;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Drone dpApi = dpApp.getDrone();

        switch (item.getItemId()) {
           /* case R.id.menu_connect:
                toggleDroneConnection();
                return true; */

            case R.id.menu_upload_mission: {
                final MissionProxy missionProxy = dpApp.getMissionProxy();
                if (missionProxy.getItems().isEmpty() || missionProxy.hasTakeoffAndLandOrRTL()) {
                    missionProxy.sendMissionToAPM(dpApi);
                } else {
                    SupportYesNoWithPrefsDialog dialog = SupportYesNoWithPrefsDialog.newInstance(
                            getApplicationContext(), MISSION_UPLOAD_CHECK_DIALOG_TAG,
                            getString(R.string.mission_upload_title),
                            getString(R.string.mission_upload_message),
                            getString(android.R.string.ok),
                            getString(R.string.label_skip),
                            DroidPlannerPrefs.PREF_AUTO_INSERT_MISSION_TAKEOFF_RTL_LAND, this);

                    if (dialog != null) {
                        dialog.show(getSupportFragmentManager(), MISSION_UPLOAD_CHECK_DIALOG_TAG);
                    }
                }
                return true;
            }

            case R.id.menu_download_mission:
                MissionApi.getApi(dpApi).loadWaypoints();
                return true;

            case R.id.menu_kill_switch:
                SlideToUnlockDialog unlockDialog = SlideToUnlockDialog.newInstance("disable vehicle", new Runnable() {
                    @Override
                    public void run() {
                        VehicleApi.getApi(dpApi).arm(false, true, new SimpleCommandListener() {
                            @Override
                            public void onError(int error) {
                                final int errorMsgId;
                                switch (error) {
                                    case CommandExecutionError.COMMAND_UNSUPPORTED:
                                        errorMsgId = R.string.error_kill_switch_unsupported;
                                        break;

                                    default:
                                        errorMsgId = R.string.error_kill_switch_failed;
                                        break;
                                }

                                Toast.makeText(getApplicationContext(), errorMsgId, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTimeout() {
                                Toast.makeText(getApplicationContext(), R.string.error_kill_switch_failed, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                unlockDialog.show(getSupportFragmentManager(), "Slide to use the Kill Switch");
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_voice:

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Drone Task...");
                startActivityForResult(intent, CONNECT_CODE_VOICE);
                return true;
            case R.id.menu_sen_enable:
                isSensorEnable = true;
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_ENABLE, ConstantCode.CONTROL_TYPE_RADAR, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_sen_disable:
                isSensorEnable = false;
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_DISABLE, ConstantCode.CONTROL_TYPE_RADAR, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_save_drone:
                try {
                    VehicleApi.getApi(dpApp.getDrone()).arm(false);

                } catch (Exception e) {

                }
                return true;
            case R.id.menu_enable_pwm:
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_DISABLE, ConstantCode.CONTROL_TYPE_ENABLE_PWM, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_enable_alt:
                isFlightModeAltEnable = true;
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_DISABLE, ConstantCode.CONTROL_TYPE_ENABLE_ALT, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_enable_loiter:
                isFlightModeAltEnable = false;
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_DISABLE, ConstantCode.CONTROL_TYPE_ENABLE_LOITER, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_open_control_dialog:
                /*CustomDialogClass cdd=new CustomDialogClass(this,controlCallBackListener);
                cdd.show();*/
                return true;
            case R.id.menu_store_param:
                //String params = "{\"u\":\"ground\",\"action\":\"store_param\"}";
                String params = "{\"u\":\"g\",\"135\":\"store_param\"}";
                SocketDataReceiver.attemptSend(params);
                return true;
            case R.id.menu_open_test:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final AlertDialog dialogInstance = alert.create();
                dialogInstance.setTitle("Waypoint Name? (Optional)");

                LayoutInflater wpView = LayoutInflater.from(this);
                View wpLayout = wpView.inflate(R.layout.wp_enter_name, null);

                final EditText wpName = (EditText) wpLayout.findViewById(R.id.enterWpName);
                Button wpSave = (Button) wpLayout.findViewById(R.id.wp);
                wpSave.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        name = wpName.getText().toString();
                        WayPointMake.setName(name);
                        SocketDataReceiver.attemptSend(WayPointMake.getString());
                        alert.setCancelable(true);
                        dialogInstance.cancel();
                    }
                });
                dialogInstance.setView(wpLayout);
                dialogInstance.show();

                /*
                DroneDB droneDB = new DroneDB(getApplicationContext());
                droneDB.addDroneData(WayPointMake.getString());
                droneDB.getDroneData();

                droneDB.addWpData(32.54843, 89.45924, 60.04003);
                droneDB.getWpData();

                DroneDB droneDB = new DroneDB(getApplicationContext());
                droneDB.addDroneData(WayPointMake.getString());

                List<LatLngDatabasesModel> latLngDatabasesModels = SignletonLatLng.getInstance(getApplicationContext()).getLatLngModel();
                for(int i=0;i<latLngDatabasesModels.size();i++)
                {
                    Log.d("wpdatas "+i, String.valueOf(latLngDatabasesModels.get(i).getLat())+" "+String.valueOf(latLngDatabasesModels.get(i).getLng())+" "+ String.valueOf(latLngDatabasesModels.get(i).getAlt()));

                    double dbLat = latLngDatabasesModels.get(i).getLat();
                    double dbLon = latLngDatabasesModels.get(i).getLng();
                    double dbAlt = latLngDatabasesModels.get(i).getAlt();
                    //droneDB.addWpData(dbLat, dbLon, dbAlt);
                }
                droneDB.getWpData();
                 */
                return true;
            case R.id.menu_scheduler:
                String strSchedule = "{\"u\":\"g\",\"135\":\"116\"}";
                SocketDataReceiver.attemptSend(strSchedule);
                return true;
            case R.id.drone_position:
                String droneLocation = "{\"u\":\"g\",\"135\":\"give_drone_location\"}";
                SocketDataReceiver.attemptSend(droneLocation);
                return true;
            case R.id.scheduler:
                String schGet = "{\"u\":\"g\",\"135\":\"121\"}";
                SocketDataReceiver.attemptSend(schGet);
                return true;
            case R.id.menu_object_dect:
                try {
                    SocketDataReceiver.attemptSend(jsonResult(ConstantCode.USER, ConstantCode.SENSOR_DISABLE, ConstantCode.CONTROL_TYPE_OBSTACLE_DETECT, "Borhan").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_armed:
                try {
                    SocketDataReceiver.attemptSend(jsonArmed().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_simple_takeoff:
                try {
                    SocketDataReceiver.attemptSend(jsonSimpleTakeOff(5).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.obj_detect_on:
                String startObjectDetect = "{\"u\":\"g\",\"135\":\"164\"}";
                SocketDataReceiver.attemptSend(startObjectDetect);
                return true;
            case R.id.obj_detect_off:
                String stopObjectDetect = "{\"u\":\"g\",\"135\":\"132\"}";
                SocketDataReceiver.attemptSend(stopObjectDetect);
                return true;
            case R.id.menu_mode_guided:
                try {
                    SocketDataReceiver.attemptSend(jsonModeGuided("GUIDED").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_simple_takeoff_land:
                String takeoff_land = "{\"u\":\"g\",\"135\":\"108\",\"142\":5}";
                SocketDataReceiver.attemptSend(takeoff_land);
                return true;
            case R.id.menu_mode_stabilize:
                try {
                    SocketDataReceiver.attemptSend(jsonModeGuided("LAND").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_disarmed:
                try {
                    SocketDataReceiver.attemptSend(jsonDisArmed().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.home_location:
                String home_location = "{\"u\":\"g\",\"135\":\"101\"}";
                SocketDataReceiver.attemptSend(home_location);
                /*speech_txt = "Mode change to LAND";
                speech_content();*/
                return true;
            case R.id.menu_menual:
                ManualFly manualFly = new ManualFly(this, manualCallbackListener);
                manualFly.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void get_vec_mode() {
        State vehicleState = dpApp.getDrone().getAttribute(AttributeType.STATE);
        VehicleMode vehicleMode = vehicleState.getVehicleMode();

        //ArrayAdapter arrayAdapter = (ArrayAdapter)this.modeSelector.getAdapter();
        //this.modeSelector.setSelection(arrayAdapter.getPosition(vehicleMode));
    }

    public void set_vec_mode() {
        State vehicleState = dpApp.getDrone().getAttribute(AttributeType.STATE);
        VehicleApi.getApi(dpApp.getDrone()).setVehicleMode(VehicleMode.COPTER_LOITER);
    }

    public void get_vec_altitude() {
        Altitude droneAltitude = dpApp.getDrone().getAttribute(AttributeType.ALTITUDE);
        //droneAltitude.setAltitude(120.00);

    }

    public void set_vec_altitude() {
        Altitude droneAltitude = dpApp.getDrone().getAttribute(AttributeType.ALTITUDE);
        //droneAltitude.setAltitude(120.00);
    }

    public LatLngAltModel GetCurrentPosition() {
        Altitude droneAltitude = dpApp.getDrone().getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = dpApp.getDrone().getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();
        LatLngAltModel latLngAltModel = new LatLngAltModel(vehiclePosition.getLatitude(), vehiclePosition.getLongitude(), vehicleAltitude);
        return latLngAltModel;
    }

    public void get_current_lat_lng() {
        Altitude droneAltitude = dpApp.getDrone().getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = dpApp.getDrone().getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();
        double lat = 37.6178687783914;
        double lng = -122.373125553131;
        vehiclePosition.set(new LatLongAlt(lat, lng, 120));

        /*double distanceFromHome =  0;
        vehiclePosition.set(new LatLongAlt(0.00,0.00,120));

        if (droneGps.isValid()) {
            LatLongAlt vehicle3DPosition = new LatLongAlt(vehiclePosition.getLatitude(), vehiclePosition.getLongitude(), vehicleAltitude);
            Home droneHome = dpApp.getDrone().getAttribute(AttributeType.HOME);
            //distanceFromHome = distanceBetweenPoints(droneHome.getCoordinate(), vehicle3DPosition);
        } else {
            distanceFromHome = 0;
        }*/

        //distanceTextView.setText(String.format("%3.1f", distanceFromHome) + "m");
    }

    ManualCallbackListener manualCallbackListener = new ManualCallbackListener() {
        @Override
        public void data_back(JSONObject data) {

        }
    };

    private JSONObject jsonHomelocation() throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.HOME_LOCATION + "\"" + "}");
        return json;
    }

    private JSONObject jsonArmed() throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.ACTION_ARMED + "\"" + "}");
        return json;
    }

    private JSONObject jsonDisArmed() throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.ACTION_DISARMED + "\"" + "}");
        return json;
    }

    private JSONObject jsonSimpleTakeOff(int alt) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.ACTION_TAKEOFF + "\"" + ","
                + "\"" + ConstantCode.DATA_STRING + "\":" + alt + "}");
        return json;
    }

    private JSONObject jsonModeGuided(String mode) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + ConstantCode.USER + "\"" + ","
                + "\"" + ConstantCode.ACTION_TYPE + "\":" + "\"" + ConstantCode.MODE_CHANGE + "\"" + ","
                + "\"" + ConstantCode.DATA_STRING + "\":" + "\"" + mode + "\"" + "}");
        return json;
    }

    private JSONObject jsonResult(String username, int enable_disable_value, int control_type, String variableArray) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + username + "\"" + ","
                + "\"" + ConstantCode.CONTROL_TYPE + "\":" + "\"" + control_type + "\"" + ","
                + "\"" + ConstantCode.ENABLE_DISABLE_KEY + "\":" + enable_disable_value + "," + "\""
                + ConstantCode.VARIABLE_ARRAY_KEY + "\":" + "\"" + variableArray + "\"" + "}");
        return json;
    }

    private JSONObject jsonResultLatLng(String username, int enable_disable_value, int control_type, String variableArray, String lat, String lng) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{\"" + ConstantCode.USERNAME_KEY + "\":" + "\"" + username + "\"" + ","
                + "\"" + ConstantCode.CONTROL_TYPE + "\":" + "\"" + control_type + "\"" + ","
                + "\"" + ConstantCode.ENABLE_DISABLE_KEY + "\":" + enable_disable_value + "," + "\""
                + ConstantCode.VARIABLE_ARRAY_KEY + "\":" + "\"" + variableArray + "\"," + "\""
                + ConstantCode.VARIABLE_LAT + "\":" + "\"" + lat + "\"," + "\""
                + ConstantCode.VARIABLE_LNG + "\":" + "\"" + lng + "\"" + "}");
        return json;
    }

    private JSONObject jsonDroneControlLeftRight(int leftright, int pwm, int time) throws JSONException {
        JSONObject json = null;
        json = new JSONObject("{" + "\"" + ","
                + ConstantCode.VARIABLE_ARRAY_LEFT_OR_RIGHT + "\":" + "\"" + leftright + "\"" + ","
                + ConstantCode.VARIABLE_ARRAY_PWM + "\":" + "\"" + pwm + "\"" + ","
                + ConstantCode.VARIABLE_ARRAY_TIME + "\":" + "\"" + time + "\"" + "}");
        return json;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemE, menuItemD, menuItemModeAlt, menuItemModeLoiter;
        menuItemE = menu.findItem(R.id.menu_sen_enable);
        menuItemD = menu.findItem(R.id.menu_sen_disable);
        menuItemModeAlt = menu.findItem(R.id.menu_enable_alt);
        menuItemModeLoiter = menu.findItem(R.id.menu_enable_loiter);

        /*if(isSensorEnable)
        {
            menuItemE.setVisible(false);
            menuItemD.setVisible(true);
        }else
        {
            menuItemE.setVisible(true);
            menuItemD.setVisible(false);
        }
        if(isFlightModeAltEnable)
        {
            menuItemModeAlt.setVisible(false);
            menuItemModeLoiter.setVisible(true);
        }else
        {
            menuItemModeAlt.setVisible(true);
            menuItemModeLoiter.setVisible(false);
        }*/

        return true;
    }

    private JSONObject makeJson(String username, int value) {
        JSONObject sensor_enable_data = new JSONObject();
        try {
            sensor_enable_data.put(ConstantCode.USERNAME_KEY, username);
            sensor_enable_data.put(ConstantCode.ENABLE_DISABLE_KEY, value);
        } catch (JSONException e) {
            // TODO when error
            e.printStackTrace();
        }
        return sensor_enable_data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECT_CODE_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.contains("connect") || matches.contains("Connect") || matches.contains("disconnect") || matches.contains("Disconnect")) {
                toggleDroneConnection();
                /*final double takeOffAltitude = DroidPlannerPrefs.getInstance(getContext()).getDefaultAltitude(); */
            } else if (matches.contains("Armed") || matches.contains("armed") || matches.contains("Arm") || matches.contains("arm") || matches.contains("arms")) {
                VehicleApi.getApi(dpApp.getDrone()).arm(true);
            } else if (matches.contains("Disarmed") || matches.contains("disarmed") || matches.contains("Disarm") || matches.contains("disarm")) {
                VehicleApi.getApi(dpApp.getDrone()).arm(false);

            } else if (matches.contains("Auto") || matches.contains("auto")) {
                //dpApp.getDrone().doGuidedTakeoff(10);
                /*VehicleApi.getApi(dpApp.getDrone()).setVehicleMode(VehicleMode.COPTER_LOITER);
                VehicleApi.getApi(dpApp.getDrone()).setVehicleMode(VehicleMode.COPTER_LOITER);*/
                /*final double takeOffAltitude = DroidPlannerPrefs.getInstance(getContext()).getDefaultAltitude();

                ControlApi.getApi(dpApp.getDrone()).takeoff(takeOffAltitude, null);*/
                //this.drone.doGuidedTakeoff(10);
                //final Drone drone = getDrone();
                //    final State droneState = drone.getAttribute(AttributeType.STATE);
                //    final VehicleMode flightMode = droneState.getVehicleMode();

            }
            //wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));*/

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void toggleDroneConnection() {
        final Drone drone = dpApp.getDrone();
        if (drone != null && drone.isConnected())
            dpApp.disconnectFromDrone();
        else
            dpApp.connectToDrone();

    }


}