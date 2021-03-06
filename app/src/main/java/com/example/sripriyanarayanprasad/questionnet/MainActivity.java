package com.example.sripriyanarayanprasad.questionnet;

import java.util.ArrayList;

import com.example.sripriyanarayanprasad.questionnet.FileUpload.FileUploadActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnCameraChangeListener {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private GoogleMap mMap;
    ArrayList<Geofence> mGeofences;
    ArrayList<LatLng> mGeofenceCoordinates;
    ArrayList<Integer> mGeofenceRadius;
    private GeofenceStore mGeofenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FileUploadActivity.class));
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


        // Initializing variables
        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();

        // Adding geofence coordinates to array.
        mGeofenceCoordinates.add(new LatLng(43.042861, -87.911559));
        mGeofenceCoordinates.add(new LatLng(43.042998, -87.909753));
        mGeofenceCoordinates.add(new LatLng(43.040732, -87.921364));
        mGeofenceCoordinates.add(new LatLng(43.039912, -87.897038));

        // Adding associated geofence radius' to array.
        mGeofenceRadius.add(100);
        mGeofenceRadius.add(50);
        mGeofenceRadius.add(160);
        mGeofenceRadius.add(160);

        // Bulding the geofences and adding them to the geofence array.

        // Performing Arts Center
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Performing Arts Center")
                // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(0).latitude, mGeofenceCoordinates.get(0).longitude, mGeofenceRadius.get(0).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(30000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        // Starbucks
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Starbucks")
                // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(1).latitude, mGeofenceCoordinates.get(1).longitude, mGeofenceRadius.get(1).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(30000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        // Milwaukee Public Museum
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Milwaukee Public Museum")
                // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(2).latitude, mGeofenceCoordinates.get(2).longitude, mGeofenceRadius.get(2).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        // Milwaukee Art Museum
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Milwaukee Art Museum")
                // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(3).latitude, mGeofenceCoordinates.get(3).longitude, mGeofenceRadius.get(3).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        // Add the geofences to the GeofenceStore object.
        mGeofenceStore = new GeofenceStore(this, mGeofences);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //@Override
    //protected void onStop() {
    //mGeofenceStore.disconnect();
    //super.onStop();
    //}

    // @Override
    // protected void onResume() {
    //   super.onResume();
    //   if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
    //   setUpMapIfNeeded();
    //  } else {
    //  GooglePlayServicesUtil.getErrorDialog(
    //   GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
    //  this, 0);
    //  }
    //  }

    //private void setUpMapIfNeeded() {
    // Do a null check to confirm that we have not already instantiated the
    // map.
    // if (mMap == null) {
    // Try to obtain the map from the SupportMapFragment.
    // mMap = ((SupportMapFragment) getSupportFragmentManager()
    // .findFragmentById(R.id.map)).getMap();

    // Check if we were successful in obtaining the map.
    // if (mMap != null) {
    // setUpMap();
    // }
    //  }
    // }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera. In this case, we just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {
        // Centers the camera over the building and zooms int far enough to
        // show the floor picker.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                43.039634, -87.908395), 14));

        // Hide labels.
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setIndoorEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(this);

    }

    @Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        for (int i = 0; i < mGeofenceCoordinates.size(); i++) {
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i).intValue())
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        }
    }
}