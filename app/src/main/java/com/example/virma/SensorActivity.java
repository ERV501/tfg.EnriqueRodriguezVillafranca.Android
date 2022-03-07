package com.example.virma;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;

    private float[] accelerometerReading = new float[3];
    private float[] magnetometerReading = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3]; // will contain: azimuth, pitch and roll

    TextView textSensorUpdate;

    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        textSensorUpdate = (TextView) findViewById(R.id.textSensorUpdate);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected void onResume(){
        super.onResume();

        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // SENSOR_DELAY_NORMAL -> desired delay between two consecutive sensor data readings
        // SENSOR_DELAY_UI -> maximum delay between our sensor data readings

        if(accelerometer != null){
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        if(magnetometer != null){
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerReading = sensorEvent.values;

        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetometerReading = sensorEvent.values;

        if (accelerometerReading != null && magnetometerReading != null) {
            // Matrix containing the current rotation based on accelerometer and magnetometer sensors readings
            boolean readingSuccess = SensorManager.getRotationMatrix(rotationMatrix, null,
                    accelerometerReading, magnetometerReading);

            if (readingSuccess) { // Check whether the matrix was successfully created
                // Express the updated rotation matrix as three orientation angles.
                SensorManager.getOrientation(rotationMatrix, orientationAngles);

                double azimuth = Math.toDegrees(orientationAngles[0]); //azimuth = angle between the device's current compass heading and magnetic north (z-axis)
                textSensorUpdate.setText(String.format("%.2f", azimuth));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
