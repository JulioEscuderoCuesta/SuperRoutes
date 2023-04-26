package com.example.superroutes;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Accelerometer extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private LocalTime newFall;
    private LocalTime lastFall;
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        newFall = LocalTime.now();
        long timeInMinutesBetweenFalls = ChronoUnit.MINUTES.between(newFall, lastFall);
        if(sensorEvent.values[1] > 10 && timeInMinutesBetweenFalls < 30) {
            lastFall = newFall;
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.newFallAudio);
            mediaPlayer.start();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}