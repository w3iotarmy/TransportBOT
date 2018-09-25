package org.droidplanner.android.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.droidplanner.android.R;
import org.droidplanner.android.dialogs.DialogMaterialFragment;
import org.droidplanner.android.fragments.FlightDataFragment;
import org.droidplanner.android.fragments.WidgetsListFragment;
import org.droidplanner.android.fragments.actionbar.ActionBarTelemFragment;
import org.droidplanner.android.utils.Utils;

import java.lang.ref.WeakReference;

public class FlightActivity extends DrawerNavigationUI implements SlidingUpPanelLayout.PanelSlideListener {

    private static final String EXTRA_IS_ACTION_DRAWER_OPENED = "extra_is_action_drawer_opened";
    private static final boolean DEFAULT_IS_ACTION_DRAWER_OPENED = true;

    private FlightDataFragment flightData;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    //private TextView display;
    //private EditText editText;
    private String TAG ="Borhan";
    private CheckBox box9600, box38400;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    @Override
    public void onDrawerClosed() {
        super.onDrawerClosed();

        if (flightData != null)
            flightData.onDrawerClosed();
    }


    @Override
    public void onDrawerOpened() {
        super.onDrawerOpened();

        if (flightData != null)
            flightData.onDrawerOpened();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        final FragmentManager fm = getSupportFragmentManager();
        //Add the flight data fragment
        flightData = (FlightDataFragment) fm.findFragmentById(R.id.flight_data_container);

        if(flightData == null){
            Bundle args = new Bundle();
            args.putBoolean(FlightDataFragment.EXTRA_SHOW_ACTION_DRAWER_TOGGLE, true);

            flightData = new FlightDataFragment();
            flightData.setArguments(args);
            fm.beginTransaction().add(R.id.flight_data_container, flightData).commit();
        }

        // Add the telemetry fragment
        final int actionDrawerId = getActionDrawerId();
        WidgetsListFragment widgetsListFragment = (WidgetsListFragment) fm.findFragmentById(actionDrawerId);
        if (widgetsListFragment == null) {
            widgetsListFragment = new WidgetsListFragment();
            fm.beginTransaction()
                    .add(actionDrawerId, widgetsListFragment)
                    .commit();
        }

        boolean isActionDrawerOpened = DEFAULT_IS_ACTION_DRAWER_OPENED;
        if (savedInstanceState != null) {
            isActionDrawerOpened = savedInstanceState.getBoolean(EXTRA_IS_ACTION_DRAWER_OPENED, isActionDrawerOpened);
        }

        if (isActionDrawerOpened)
            openActionDrawer();
        mHandler = new MyHandler(this);

        //display = (TextView) findViewById(R.id.textView1);
        //editText = (EditText) findViewById(R.id.editText1);
        /*Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "Borhan";//editText.getText().toString();
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write(data.getBytes());
                }
                *//*if (!editText.getText().toString().equals("")) {
                    String data = "Borhan";//editText.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(data.getBytes());
                    }
                }*//*
            }
        });*/
    }

    @Override
    protected void onToolbarLayoutChange(int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom){
        if(flightData != null)
            flightData.updateActionbarShadow(bottom);
    }

    @Override
    protected void addToolbarFragment() {
        final int toolbarId = getToolbarId();
        final FragmentManager fm = getSupportFragmentManager();
        Fragment actionBarTelem = fm.findFragmentById(toolbarId);
        if (actionBarTelem == null) {
            actionBarTelem = new ActionBarTelemFragment();
            fm.beginTransaction().add(toolbarId, actionBarTelem).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_IS_ACTION_DRAWER_OPENED, isActionDrawerOpened());
    }

    @Override
    public void onStart(){
        super.onStart();

        final Context context = getApplicationContext();
        //Show the changelog if this is the first time the app is launched since update/install
        if(Utils.getAppVersionCode(context) > mAppPrefs.getSavedAppVersionCode()) {
            DialogMaterialFragment changelog = new DialogMaterialFragment();
            changelog.show(getSupportFragmentManager(), "Changelog Dialog");
            mAppPrefs.updateSavedAppVersionCode(context);
        }
    }

    @Override
    protected int getToolbarId() {
        return R.id.actionbar_toolbar;
    }

    @Override
    protected int getNavigationDrawerMenuItemId() {
        return R.id.navigation_flight_data;
    }

    @Override
    protected boolean enableMissionMenus() {
        return true;
    }

    @Override
    public void onPanelSlide(View view, float v) {
        final int bottomMargin = (int) getResources().getDimension(R.dimen.action_drawer_margin_bottom);

        //Update the bottom margin for the action drawer
        final View flightActionBar = ((ViewGroup)view).getChildAt(0);
        final int[] viewLocs = new int[2];
        flightActionBar.getLocationInWindow(viewLocs);
        updateActionDrawerBottomMargin(viewLocs[0] + flightActionBar.getWidth(), Math.max((int) (view.getHeight() * v), bottomMargin));
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch(newState){
            case COLLAPSED:
            case HIDDEN:
                resetActionDrawerBottomMargin();
                break;

            case EXPANDED:
                //Update the bottom margin for the action drawer
                ViewGroup slidingPanel = (ViewGroup) ((ViewGroup)panel).getChildAt(1);
                final View flightActionBar = slidingPanel.getChildAt(0);
                final int[] viewLocs = new int[2];
                flightActionBar.getLocationInWindow(viewLocs);
                updateActionDrawerBottomMargin(viewLocs[0] + flightActionBar.getWidth(), slidingPanel.getHeight());
                break;
        }
    }

    private void updateActionDrawerBottomMargin(int rightEdge, int bottomMargin){
        final ViewGroup actionDrawerParent = (ViewGroup) getActionDrawer();
        final View actionDrawer = ((ViewGroup)actionDrawerParent.getChildAt(1)).getChildAt(0);

        final int[] actionDrawerLocs = new int[2];
        actionDrawer.getLocationInWindow(actionDrawerLocs);

        if(actionDrawerLocs[0] <= rightEdge) {
            updateActionDrawerBottomMargin(bottomMargin);
        }
    }

    private int getActionDrawerBottomMargin(){
        final ViewGroup actionDrawerParent = (ViewGroup) getActionDrawer();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) actionDrawerParent.getLayoutParams();
        return lp.bottomMargin;
    }

    private void updateActionDrawerBottomMargin(int newBottomMargin){
        final ViewGroup actionDrawerParent = (ViewGroup) getActionDrawer();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) actionDrawerParent.getLayoutParams();
        lp.bottomMargin = newBottomMargin;
        actionDrawerParent.requestLayout();
    }

    private void resetActionDrawerBottomMargin(){
        updateActionDrawerBottomMargin((int) getResources().getDimension(R.dimen.action_drawer_margin_bottom));
    }
    private static class MyHandler extends Handler {
        private final WeakReference<FlightActivity> mActivity;
        private String TAG= "Borhan MyHandler";

        public MyHandler(FlightActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    //mActivity.get().display.append(data);
                    Log.d(TAG, data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.SYNC_READ:
                    String buffer = (String) msg.obj;
                    //mActivity.get().display.append(buffer);
                    break;
            }
        }
    }
}
