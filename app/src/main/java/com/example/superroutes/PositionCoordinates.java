package com.example.superroutes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PositionCoordinates extends AppCompatActivity {

    private TextView latitude;
    private TextView longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_coordinates);

        latitude = findViewById(R.id.latitude_text);
        longitude = findViewById(R.id.longitude_text);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(location!= null) {
                    latitude.setText(String.valueOf(location.getLatitude()));
                    longitude.setText(String.valueOf(location.getLongitude()));
                }
            }
            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(PositionCoordinates.this, "Provider disabled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(PositionCoordinates.this, "Provider enabled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                //Toast.makeText(PositionCoordinates.this, "Status Changed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void seePositionOnMap(View view) {
        Toast.makeText(this, "Function not implemented yet", Toast.LENGTH_SHORT).show();
    }
}