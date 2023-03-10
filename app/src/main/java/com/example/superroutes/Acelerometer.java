package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Acelerometer extends AppCompatActivity implements SensorEventListener {

    private LocalDateTime newFall;
    private LocalDateTime lastFall = LocalDateTime.now();
    private static final String WARNING_MESSAGE = "¡Oh no, you fell down!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometer);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor != null)
            sensorManager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        newFall = LocalDateTime.now();
        long timeInMinutesBetweenFalls = ChronoUnit.MINUTES.between(newFall, lastFall);
        if(sensorEvent.values[1] > 10 && timeInMinutesBetweenFalls < 30) {
            lastFall = newFall;
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.caida);
            mediaPlayer.start();
            Toast.makeText(this, WARNING_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}