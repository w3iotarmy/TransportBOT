package org.droidplanner.android.fragments.widget.telemetry

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent
import com.o3dr.services.android.lib.drone.attribute.AttributeType
import com.o3dr.services.android.lib.drone.property.Attitude
import com.o3dr.services.android.lib.drone.property.Speed
import org.droidplanner.android.R
import org.droidplanner.android.fragments.widget.TowerWidget
import org.droidplanner.android.fragments.widget.TowerWidgets
import org.droidplanner.android.singleton.SignletonGyroscopeUpdate
import org.droidplanner.android.singleton.SignletonLatLng
import org.droidplanner.android.view.AttitudeIndicator
import timber.log.Timber
import java.util.*

/**
 * Created by Fredia Huya-Kouadio on 8/27/15.
 */
public class MiniWidgetAttitudeSpeedInfo : TowerWidget() {

    companion object {
        private val filter = initFilter()

        private fun initFilter(): IntentFilter {
            val temp = IntentFilter()
            temp.addAction(AttributeEvent.ATTITUDE_UPDATED)
            temp.addAction(AttributeEvent.SPEED_UPDATED)
            temp.addAction(AttributeEvent.GPS_POSITION)
            temp.addAction(AttributeEvent.ALTITUDE_UPDATED)
            temp.addAction(AttributeEvent.HOME_UPDATED)
            return temp
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                AttributeEvent.ATTITUDE_UPDATED -> onOrientationUpdate()
                AttributeEvent.SPEED_UPDATED, AttributeEvent.ALTITUDE_UPDATED -> onSpeedUpdate()
            }
        }
    }

    private val TAG="MMMMM MiniWidgetAttitudeSpeedInfo"
    private var attitudeIndicator: AttitudeIndicator? = null
    private var roll: TextView? = null
    private var yaw: TextView? = null
    private var pitch: TextView? = null

    private var horizontalSpeed: TextView? = null
    private var verticalSpeed: TextView? = null

    private var headingModeFPV: Boolean = false

    private val MIN_VERTICAL_SPEED_MPS = 0.10 //Meters Per Second
    val handler = Handler()
    val delay = 100 //milliseconds
    var signton = SignletonGyroscopeUpdate.getInstance(context)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_mini_widget_attitude_speed_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attitudeIndicator = view.findViewById(R.id.aiView) as AttitudeIndicator?

        roll = view.findViewById(R.id.rollValueText) as TextView?
        yaw = view.findViewById(R.id.yawValueText) as TextView?
        pitch = view.findViewById(R.id.pitchValueText) as TextView?

        horizontalSpeed = view.findViewById(R.id.horizontal_speed_telem) as TextView?
        verticalSpeed = view.findViewById(R.id.vertical_speed_telem) as TextView?
    }

    override fun onStart() {
        super.onStart()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        headingModeFPV = prefs.getBoolean("pref_heading_mode", false)
        if(signton.isKotlinRunnable)
        {
            handler.postDelayed(object : Runnable {
                override fun run() {
                    updateGyro()
                    updateSpeed()
                    handler.postDelayed(this, delay.toLong())
                }
            }, delay.toLong())
            signton.isKotlinRunnable=false
        }

    }

    override fun getWidgetType() = TowerWidgets.ATTITUDE_SPEED_INFO

    override fun onApiConnected() {
        updateAllTelem()
        broadcastManager.registerReceiver(receiver, filter)
    }

    override fun onApiDisconnected() {
        broadcastManager.unregisterReceiver(receiver)
    }

    private fun updateAllTelem() {
        onOrientationUpdate()
        onSpeedUpdate()
    }

    fun updateAllTelemSocket() {
        onOrientationUpdate()
        onSpeedUpdate()
    }

    fun updateGyro() {
        val r = signton.roll
        val p = signton.pitch
        var y = signton.yaw
        if (!headingModeFPV and (y < 0)) {
            y += 360
        }
        attitudeIndicator?.setAttitude(r, p, y)

        roll?.text = String.format(Locale.US, "%3.0f\u00B0", r)
        pitch?.text = String.format(Locale.US, "%3.0f\u00B0", p)
        yaw?.text = String.format(Locale.US, "%3.0f\u00B0", y)
    }
    fun updateSpeed()
    {
        var vs = signton.vertical_speed
        var hs = signton.horizontal_speed


        horizontalSpeed?.text = getString(R.string.horizontal_speed_telem, speedUnitProvider.boxBaseValueToTarget(hs).toString())
        verticalSpeed?.text = getString(R.string.vertical_speed_telem, speedUnitProvider.boxBaseValueToTarget(vs).toString())
    }

    private fun onOrientationUpdate() {
        if (!isAdded)
            return

        val drone = drone

        val attitude = drone.getAttribute<Attitude>(AttributeType.ATTITUDE) ?: return

        val r = attitude.roll.toFloat()
        val p = attitude.pitch.toFloat()
        var y = attitude.yaw.toFloat()

        if (!headingModeFPV and (y < 0)) {
            y += 360
        }

        attitudeIndicator?.setAttitude(r, p, y)

        roll?.text = String.format(Locale.US, "%3.0f\u00B0", r)
        pitch?.text = String.format(Locale.US, "%3.0f\u00B0", p)
        yaw?.text = String.format(Locale.US, "%3.0f\u00B0", y)
    }

    private fun onSpeedUpdate() {
        if (!isAdded)
            return

        val drone = drone
        val speed = drone.getAttribute<Speed>(AttributeType.SPEED) ?: return

        /* val groundSpeedValue = speed.groundSpeed
        val verticalSpeedValue = speed.verticalSpeed */

        val groundSpeedValue = 1.0
        val verticalSpeedValue = 2.0

        val speedUnitProvider = speedUnitProvider

        horizontalSpeed?.text = getString(R.string.horizontal_speed_telem, speedUnitProvider.boxBaseValueToTarget(groundSpeedValue).toString())
        verticalSpeed?.text = getString(R.string.vertical_speed_telem, speedUnitProvider.boxBaseValueToTarget(verticalSpeedValue).toString())

        if (verticalSpeedValue >= MIN_VERTICAL_SPEED_MPS) {
            verticalSpeed?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_debug_step_up, 0, 0, 0);
        } else if (verticalSpeedValue <= -(MIN_VERTICAL_SPEED_MPS)) {
            verticalSpeed?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_debug_step_down, 0, 0, 0);
        } else {
            verticalSpeed?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_debug_step_none, 0, 0, 0);
        }
    }
}