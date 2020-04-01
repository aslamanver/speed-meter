package com.aslam.speedmeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.aslam.speedmeter.databinding.ActivityMainBinding;
import com.aslam.speedmeter.services.MyForegroundService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    String TAG = "SpeedMeterTAG";

    ActivityMainBinding binding;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback locationCallback;

    Location sLocation, eLocation;
    double distance;
    double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Log.e(TAG, "m: " + locationResult.getLastLocation().getLatitude());

                if (sLocation == null) {
                    sLocation = locationResult.getLastLocation();
                }

                eLocation = locationResult.getLastLocation();
                distance = distance + (sLocation.distanceTo(eLocation) / 1000.00);
                speed = distance <= 0 ? 0 : eLocation.getSpeed() * 18 / 5;
                sLocation = eLocation;

                speed = eLocation.getSpeed() * 3.6;

                DecimalFormat df = new DecimalFormat("#.##");
                binding.txtResult.setText("k: " + df.format(speed) + "\nd: " + df.format(distance));

            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.requestLocationUpdates(new LocationRequest()
                .setInterval(500)
                .setFastestInterval(700)
                .setSmallestDisplacement(2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY), locationCallback, Looper.myLooper());

        ContextCompat.startForegroundService(this, new Intent(this, MyForegroundService.class));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        distance = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
