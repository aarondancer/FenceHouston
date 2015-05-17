package hackhou.fencehouston.fencehouston;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnCameraChangeListener {

    /**
     * Google Map object
     */
    private GoogleMap mMap;

    /**
     * Geofence Data
     */

    /**
     * Geofences Array
     */
    ArrayList<Geofence> mGeofences;

    /**
     * Geofence Coordinates
     */
    ArrayList<LatLng> mGeofenceCoordinates;

    /**
     * Geofence Radius'
     */
    ArrayList<Integer> mGeofenceRadius;

    /**
     * Geofence Store
     */
    private GeofenceStore mGeofenceStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        // Initializing variables
        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();

        // Adding geofence coordinates to array.
        mGeofenceCoordinates.add(new LatLng(29.752000, -95.375462));
        mGeofenceCoordinates.add(new LatLng(43.042998, -87.909753));
        mGeofenceCoordinates.add(new LatLng(43.040732, -87.921364));
        mGeofenceCoordinates.add(new LatLng(43.039912, -87.897038));
        mGeofenceCoordinates.add(new LatLng(29.953470, -95.505461));

        // Adding associated geofence radius' to array.
        mGeofenceRadius.add(500);
        mGeofenceRadius.add(50);
        mGeofenceRadius.add(160);
        mGeofenceRadius.add(160);
        mGeofenceRadius.add(500);

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

        // G Home
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Milwaukee Art Museum")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(4).latitude, mGeofenceCoordinates.get(4).longitude, mGeofenceRadius.get(3).intValue())
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

    @Override
    protected void onStop() {
        mGeofenceStore.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setUpMapIfNeeded();
        } else {
            GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this, 0);
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            //mMap =  ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            //  mMap = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.google_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

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
                29.752000, -95.375462), 14));

        // Hide labels.
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener((GoogleMap.OnCameraChangeListener) this);
        mMap.setBuildingsEnabled(true);

    }

    //@Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        for(int i = 0; i < mGeofenceCoordinates.size(); i++) {
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i))
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i) / 2)
                    .fillColor(0x80AA0000)
                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        }
    }
}


