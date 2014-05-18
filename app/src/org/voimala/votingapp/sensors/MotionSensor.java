package org.voimala.votingapp.sensors;

import java.util.logging.Logger;

import org.voimala.votingapp.activities.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

public class MotionSensor implements SensorEventListener {
    
    private static MotionSensor instanceOfThis = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Context context = null;
    private long tabChangedTimestamp = System.currentTimeMillis();
    private int tabChangeDelayMs = 500;

    public static MotionSensor getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new MotionSensor();
        }
        
        return instanceOfThis;
    }
    
    public void initialize(final Context context) {
        this.context = context;
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /* NOTE!
         * These values have been tested to work only on Samsung Galaxy S4 device. */
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float azimuth = sensorEvent.values[0];
            float pitch = sensorEvent.values[1];
            float roll = sensorEvent.values[2];
            
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean useMotionSensor = sharedPreferences.getBoolean("use_motion_sensor", true);
            
            if (useMotionSensor) {
                navigateTabRight(azimuth);
                navigateTabLeft(azimuth);
            }
        }
    }

    private void navigateTabRight(final float azimuth) {
        if (azimuth < -5 && tabChangedTimestamp + tabChangeDelayMs < System.currentTimeMillis()) {
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                try {
                    mainActivity.getActionBar().selectTab(
                            mainActivity.getActionBar().getTabAt(
                                    mainActivity.getActionBar().getSelectedNavigationIndex() + 1));
                } catch (Exception e) {
                    // Continue...
                }

            }
            
            tabChangedTimestamp = System.currentTimeMillis();
        }
    }

    private void navigateTabLeft(final float azimuth) {
        if (azimuth > 5 && tabChangedTimestamp + tabChangeDelayMs < System.currentTimeMillis()) {
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                try {
                    mainActivity.getActionBar().selectTab(
                            mainActivity.getActionBar().getTabAt(
                                    mainActivity.getActionBar().getSelectedNavigationIndex() - 1));
                } catch (Exception e) {
                    // Continue...
                }
            }
            
            tabChangedTimestamp = System.currentTimeMillis();
        }
    }
}