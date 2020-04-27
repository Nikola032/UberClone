package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class DriverRequestListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btnGetRequests;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private  ListView listView;
    private ArrayList<String> nearByDriveRequests;
    private  ArrayAdapter adapter;
    private ArrayList<Double> passingersLatitudes;
    private ArrayList<Double> passingersLongitudes;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);

        btnGetRequests = findViewById(R.id.btnGetRequests);
        btnGetRequests.setOnClickListener(this);

        listView = findViewById(R.id.requestListView);

        nearByDriveRequests = new ArrayList<>();
        passingersLatitudes = new ArrayList<>();
        passingersLongitudes = new ArrayList<>();

        adapter = new ArrayAdapter(DriverRequestListActivity.this, android.R.layout.simple_list_item_1, nearByDriveRequests);
        listView.setAdapter(adapter);

        nearByDriveRequests.clear();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(DriverRequestListActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
        listView.setOnItemClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if( item.getItemId() == R.id.LogOutItem){
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Intent intent = new Intent(DriverRequestListActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (Build.VERSION.SDK_INT < 23){
            Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateRequestsListView(currentDriverLocation);
        }else if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DriverRequestListActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);

            }else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0 ,locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateRequestsListView(currentDriverLocation);
            }
        }
        

    }

    private void updateRequestsListView(Location driverLocation) {

        if (driverLocation != null) {


            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(driverLocation.getLatitude(),driverLocation.getLongitude());

            ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("RequestCar");

            requestCarQuery.whereNear("passingerLocation",driverCurrentLocation);
            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null){
                    if (objects.size() > 0) {
                        if (nearByDriveRequests.size() > 0 ) {
                            nearByDriveRequests.clear();

                        }
                        if (passingersLatitudes.size() > 0 ){
                            passingersLatitudes.clear();
                        }
                        if (passingersLongitudes.size() > 0 ) {
                            passingersLongitudes.clear();
                        }
                        for (ParseObject nearRequest : objects){

                            ParseGeoPoint pLocation = (ParseGeoPoint) nearRequest.get("passingerLocation");
                            Double milesDistanceToPassinger =
                                    driverCurrentLocation.distanceInMilesTo(pLocation);
                            float roundedDistanceValue = Math.round(milesDistanceToPassinger * 10) / 10;
                            nearByDriveRequests.add("There are " + roundedDistanceValue + " miles to " + nearRequest.get("username"));

                            passingersLatitudes.add(pLocation.getLatitude());
                            passingersLongitudes.add(pLocation.getLongitude());

                        }
                    }
                    else {
                        FancyToast.makeText(DriverRequestListActivity.this,"There is no requests yes", Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                        adapter.notifyDataSetChanged();

                    }
                }
            });
        }

        
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                updateRequestsListView(currentDriverLocation);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FancyToast.makeText(this,"clicked",Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
    }
}
