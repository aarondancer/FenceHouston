package hackhou.fencehouston.fencehouston;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.*;

public class MainActivity extends Activity implements LocationListener{
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;
    private LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "pnJAoPzsbulMxEzSwNLzAdIq1OlgH4NHDnNKdAXl", "I17zOttD1hoS5RErQnboUSoXEbwiXiQX2glcMu4K");

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                /* CAL METHOD requestLocationUpdates */

        // Parameters :
        //   First(provider)    :  the name of the provider with which to register
        //   Second(minTime)    :  the minimum time interval for notifications,
        //                         in milliseconds. This field is only used as a hint
        //                         to conserve power, and actual time between location
        //                         updates may be greater or lesser than this value.
        //   Third(minDistance) :  the minimum distance interval for notifications, in meters
        //   Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location)
        //                         method will be called for each location update


        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                3000,   // 3 sec
                10, this);

        /********* After registration onLocationChanged method  ********/
        /********* called periodically after each 3 sec ***********/

        Button startBtn = (Button) findViewById(R.id.start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                displayNotification();
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cancelNotification();
            }
        });

        Button updateBtn = (Button) findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateNotification();
            }
        });

        Button mapBtn = (Button) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //define a new Intent for the second Activity
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);;
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //define a new Intent for the second Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);;
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void displayNotification() {
        Log.i("Start", "notification");

      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.woman);

      /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);


      /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        events[0] = new String("This is first line....");
        events[1] = new String("This is second line...");
        events[2] = new String("This is third line...");
        events[3] = new String("This is 4th line...");
        events[4] = new String("This is 5th line...");
        events[5] = new String("This is 6th line...");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Big Title Details:");
        // Moves events into the big view
        for (int i=0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);


      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, NotificationView.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationView.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Clears notification on click
        mBuilder.setAutoCancel(true);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    protected void cancelNotification() {
        Log.i("Cancel", "notification");
        mNotificationManager.cancel(notificationID);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void updateNotification() {
        Log.i("Update", "notification");

      /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Updated Message");
        mBuilder.setContentText("You've got updated message.");
        mBuilder.setTicker("Updated Message Alert!");
        mBuilder.setSmallIcon(R.drawable.woman);

     /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, NotificationView.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationView.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Clears notification on click
        mBuilder.setAutoCancel(true);

      /* Update the existing notification using same notification ID */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    /************* Called after each 3 sec **********/
    @Override
    public void onLocationChanged(Location location) {

        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();

        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        /******** Called when User off Gps *********/

        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}