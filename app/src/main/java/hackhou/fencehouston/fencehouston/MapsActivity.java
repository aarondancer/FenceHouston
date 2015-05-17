package hackhou.fencehouston.fencehouston;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnCameraChangeListener, AdapterView.OnItemSelectedListener {

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
    ParseObject PublicArt;
    ParseObject ChargingStation;
    List<Circle> artCircles;
    List<Circle> chargeCircles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
// Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "pnJAoPzsbulMxEzSwNLzAdIq1OlgH4NHDnNKdAXl", "I17zOttD1hoS5RErQnboUSoXEbwiXiQX2glcMu4K");
        // Initializing variables
        mGeofences = new ArrayList<Geofence>();
        mGeofenceCoordinates = new ArrayList<LatLng>();
        mGeofenceRadius = new ArrayList<Integer>();

        initialFences();

        Spinner spinner = (Spinner) findViewById(R.id.channel_spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.channels, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        populatePublicArt();
    }

    private void initialFences(){
        // Adding geofence coordinates to array.
        mGeofenceCoordinates.add(new LatLng(29.7520967, -95.3757573));
        mGeofenceCoordinates.add(new LatLng(29.759798, -95.363542));


        // Adding associated geofence radius' to array.
        mGeofenceRadius.add(100);
        mGeofenceRadius.add(250);

        // Bulding the geofences and adding them to the geofence array.

        // Performing Arts Center
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Houston Technology Center")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(0).latitude, mGeofenceCoordinates.get(0).longitude, mGeofenceRadius.get(0).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        mGeofences.add(new Geofence.Builder()
                .setRequestId("Travis Fire")
                        // The coordinates of the center of the geofence and the radius in meters.
                .setCircularRegion(mGeofenceCoordinates.get(1).latitude, mGeofenceCoordinates.get(1).longitude, mGeofenceRadius.get(1).intValue())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                .setLoiteringDelay(000)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_EXIT).build());

        // Add the geofences to the GeofenceStore object.
        mGeofenceStore = new GeofenceStore(this, mGeofences);
    }

    private void populatePublicArt(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Channel");
        query.whereEqualTo("name", "Public Art");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {
                if (e == null) {
                    PublicArt = resultList.get(0);
                    getPublicArtPieces();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getPublicArtPieces(){
        final List<String> ids = PublicArt.getList("Fences");
        for (int i = 0; i < 35; i++) {
            final int j = i;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Fence");
            query.getInBackground(ids.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Geocoder coder = new Geocoder(MapsActivity.this);
                    double longitude;
                    double latitude;
//                        Log.d("Now attempting to place", object.getObjectId());
                    try {
                        ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(object.getString("address"), 1);
                        if (addresses.size() > 0) {
                            longitude = addresses.get(0).getLongitude();
                            latitude = addresses.get(0).getLatitude();
                            float radius = 25;
                            mGeofences.add(new Geofence.Builder()
                                    .setRequestId(object.getString("title"))
                                            // The coordinates of the center of the geofence and the radius in meters.
                                    .setCircularRegion(latitude, longitude, radius)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(
                                            Geofence.GEOFENCE_TRANSITION_ENTER
                                                    | Geofence.GEOFENCE_TRANSITION_EXIT).build());
                            mGeofenceStore = new GeofenceStore(MapsActivity.this, mGeofences);

                            Circle c1 = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude))
                                    .radius(radius)
                                    .fillColor(Color.argb(100, 0, 255, 0))
                                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
                            Circle c2 = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude))
                                    .radius(radius / 1.5)
                                    .fillColor(Color.argb(100, 0, 200, 0))
                                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
//                            artCircles.add(c1);
//                            artCircles.add(c2);
                        }
                    } catch (IOException ee) {
//                            ee.printStackTrace();

                    }

                }
                }
            });
        }
    }

    private void populateChargingStations(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Channel");
        query.whereEqualTo("name", "Charging Stations");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {
            if (e == null) {
                ChargingStation = resultList.get(0);
                getChargingStations();
            } else {
                Log.d("score", "Error: " + e.getMessage());
            }
            }
        });
    }

    private void getChargingStations(){
        final List<String> ids = ChargingStation.getList("Fences");
        for (int i = 0; i < ids.size(); i++) {
            final int j = i;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Fence");
            query.getInBackground(ids.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Geocoder coder = new Geocoder(MapsActivity.this);
                    double longitude;
                    double latitude;
//                        Log.d("Now attempting to place", object.getObjectId());
                    try {
                        ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(object.getString("address"), 1);
                        if (addresses.size() > 0) {
                            longitude = addresses.get(0).getLongitude();
                            latitude = addresses.get(0).getLatitude();
                            float radius = 25;
                            mGeofences.add(new Geofence.Builder()
                                    .setRequestId(object.getString("title"))
                                            // The coordinates of the center of the geofence and the radius in meters.
                                    .setCircularRegion(latitude, longitude, radius)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(
                                            Geofence.GEOFENCE_TRANSITION_ENTER
                                                    | Geofence.GEOFENCE_TRANSITION_EXIT).build());
                            mGeofenceStore = new GeofenceStore(MapsActivity.this, mGeofences);

                            Circle c1 = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude))
                                    .radius(radius * 100)
                                    .fillColor(Color.argb(100, 255, 255, 0))
                                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
                            Circle c2 = mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude))
                                    .radius(radius / 1.5 * 100)
                                    .fillColor(Color.argb(100, 200, 200, 0))
                                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));

//                            chargeCircles.add(c1);
//                            chargeCircles.add(c2);
                        }
                    } catch (IOException ee) {
//                            ee.printStackTrace();

                    }

                }
                }
            });
        }
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

    @Override
    public void onCameraChange(CameraPosition position) {
        // Makes sure the visuals remain when zoom changes.
        mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(0))
                .radius(mGeofenceRadius.get(0))
                .fillColor(Color.argb(100, 255, 0, 255))
                .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(0))
                .radius(mGeofenceRadius.get(0) / 3)
                .fillColor(Color.argb(50, 200, 0, 200))
                .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        
        for(int i = 1; i < mGeofenceCoordinates.size(); i++) {
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i))
                    .fillColor(Color.argb(100, 255, 0, 0))
                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
            mMap.addCircle(new CircleOptions().center(mGeofenceCoordinates.get(i))
                    .radius(mGeofenceRadius.get(i) / 3)
                    .fillColor(Color.argb(50, 200, 0, 0))
                    .strokeColor(Color.TRANSPARENT).strokeWidth(2));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Log.d("Was it called?", String.valueOf(pos));
         switch (pos) {
             case 0:
                 mMap.clear();
                 initialFences();
                 populatePublicArt();
                 break;
             case 1:
                 mMap.clear();
                 initialFences();
                 populateChargingStations();
                 break;
         }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}


