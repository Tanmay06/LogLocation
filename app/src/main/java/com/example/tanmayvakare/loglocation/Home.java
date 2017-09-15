package com.example.tanmayvakare.loglocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.tanmayvakare.loglocation.R.id.loc;

public class Home extends AppCompatActivity {

    TextView loct;
    FirebaseDatabase busData;
    DatabaseReference busRef;
    LocationManager mLocationManager;
    android.location.LocationListener mLocationListener;
    EditText mBusNo;
    Button startLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        busData = FirebaseDatabase.getInstance();

        busRef = busData.getReference("/Bus_details");

        startLog =(Button) findViewById(R.id.start);
        loct = (TextView) findViewById(loc);
        mBusNo = (EditText) findViewById(R.id.bus_no);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loct.setText(location.getLongitude() + " " + location.getLatitude());
                busRef.child(mBusNo.getText().toString()).child("loclong").setValue(location.getLongitude());
                busRef.child(mBusNo.getText().toString()).child("loclat").setValue(location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settings);

            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},100);
            }else{
                startLogging();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100 : if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLogging();
            }
        }
    }

    public void startLogging() {
        startLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBusNo.getText() == null){
                    Toast.makeText(getBaseContext(),"Enter Bus no",Toast.LENGTH_LONG).show();
                }else{
                    mLocationManager.requestLocationUpdates("gps", 5000, 0, mLocationListener);
                }
            }
        });
    }
}
