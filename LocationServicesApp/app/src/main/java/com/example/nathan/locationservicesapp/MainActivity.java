package com.example.nathan.locationservicesapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient; // Connects to Google's API
    private Location currentLocation; // Stores the most recent location data
    private String TAG = "MainActivity"; // Used to identify error messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the API client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    // Called when the Client is initialized
    protected void onStart() {
        Log.i(TAG, "Client Connecting");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    // Called when Client is finished
    protected void onStop() {
        Log.i(TAG, "Client Disconnecting");
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    // Called when the Button is pressed
    public void getCurrentLocation(View view) {
        Log.i(TAG, "Button Clicked");

        // Get the most recent location
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // If it's not null, then update the view
        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();

            TextView manualLat = (TextView) findViewById(R.id.manual_lat);
            TextView manualLong = (TextView) findViewById(R.id.manual_long);
            manualLat.setText("Latitude:   " + lat);
            manualLong.setText("Longitude:   " + lon);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // As soon as the Client connects, get the last location
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Create a location request for the automatically updating data
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000); // Poll every second
        request.setFastestInterval(1000);

        // Create a special instance of the location provider
        FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

        // Request regular updates from the special location provider using the request object
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    // Called every time the location provider gets new data
    public void onLocationChanged(Location location) {
        // Update the current location with the new data
        currentLocation = location;

        // If it's not null, update the view
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            TextView refreshLat = (TextView) findViewById(R.id.refresh_lat);
            TextView refreshLong = (TextView) findViewById(R.id.refresh_long);
            refreshLat.setText("Latitude:   " + lat);
            refreshLong.setText("Longitude:   " + lon);
        }
    }
}
