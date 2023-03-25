package com.example.superroutes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Menu extends AppCompatActivity implements SensorEventListener {

    private ArrayList<String> options;
    private SwitchCompat accelerometerSwitch;
    private static boolean isTouched = false;
    private LocalTime newFall;
    private LocalTime lastFall = LocalTime.now();
    private LocalTime timeRouteStarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String routeSelected = intent.getStringExtra("route_selected");
        TextView routeTitle = findViewById(R.id.route_selected);
        routeTitle.setText(routeSelected);

        timeRouteStarts = LocalTime.now();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        //List with all the options
        ListView list = findViewById(R.id.items_list);
        options = new ArrayList<>();
        options.add("Accelerometer");
        options.add("Gyroscope");
        options.add("Step Counter");
        options.add("Position");
        //Show list with all the options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);

        accelerometerSwitch = findViewById(R.id.accelerometer_switch);

        //Make the items clikeable
        list.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent launcher = new Intent();
            String option = options.get(position);
            switch (option) {
                /*case "Accelerometer":
                    launcher = new Intent(Menu.this, Acelerometer.class);
                    break;
                case "Gyroscope":
                    launcher = new Intent(Menu.this, Gyroscope.class);
                    break;
                case "Step Counter":
                    launcher = new Intent(Menu.this, StepCounter.class);
                    break;
                case "Position":
                    launcher = new Intent(Menu.this, Position.class);
                    break;*/
                case "Position":
                    launcher = new Intent(Menu.this, Position.class);
                    break;
            }
            startActivity(launcher);
        });

        //Check accelerometer options is checked or not
        accelerometerSwitch.setOnTouchListener((view, motionEvent) -> {
            isTouched = true;
            return false;
        });

        accelerometerSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isTouched) {
                isTouched = false;
                if(isChecked)
                    Toast.makeText(Menu.this, "Accelerometer activated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Menu.this, "Accelerometer desactivated", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(accelerometerSwitch.isChecked() && sensorEvent.values[1] > 10) {
            newFall = LocalTime.now();
            long timeInSecondsBetweenFalls = ChronoUnit.SECONDS.between(lastFall, newFall);
            if(timeInSecondsBetweenFalls > 30) {
                lastFall = newFall;
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.caida);
                mediaPlayer.start();
                Toast.makeText(this, "Sending message...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Empty
    }

    /**
     * Calculates time taken to finish the route ends it.
     * @param view The button touched.
     * @throws InterruptedException if sleeping does not work (it sleeps a little bit before going back to routes screen).
     */
    public void finishRoute(View view) throws InterruptedException {
        LocalTime timeRouteFinish = LocalTime.now();
        LocalTime tempTime = LocalTime.from(timeRouteStarts);
        long hours = tempTime.until(timeRouteFinish, ChronoUnit.HOURS);
        long minutes = tempTime.until(timeRouteFinish, ChronoUnit.MINUTES);
        long seconds = tempTime.until(timeRouteFinish, ChronoUnit.SECONDS);
        String message = "It took you " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Thread.sleep(5000);
        Intent intent = new Intent(this, RoutesSenderist.class);
        startActivity(intent);
    }

    public void notImplementedYet(View view) {
        Toast.makeText(this, "Function not implement yet", Toast.LENGTH_SHORT).show();
    }
}