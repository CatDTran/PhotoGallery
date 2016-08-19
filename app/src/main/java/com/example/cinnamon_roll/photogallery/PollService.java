package com.example.cinnamon_roll.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by trand_000 on 7/16/2016.
 */
public class PollService extends IntentService{

    private static final String TAG = "PollService";
    private static final long POLL_INTERNAL = 500;//AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final String ACTION_SHOW_NOTIFICATION = "com.cinnamon_roll.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.cinnamon_roll.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    //--------------------newIntent()-----------------------//
    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
    }
    //**************** setServiceAlarm() ********************//
    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERNAL, pi);
        }
        else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
        QueryPreferences.setAlarmOn(context, isOn);//save alarn status to SharedPreference whenever it is set
    }
    //--------------------isServiceAlarmOn()---------------//
    public static boolean isServiceAlarmOn(Context context){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    //==================Constructor========================//
    public PollService(){
        super(TAG);
    }
    //---------------------onHandleIntent()----------------//
    @Override
    protected void onHandleIntent(Intent intent){
        if(!isNetworkAvailableAndConnected())//it is good practice to check for network connection before downloading data in background
        {
            return;
        }
        //Log.i(TAG, "Receive an intent: "+ intent);
        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;
        if(query == null) {
            items = new FlickrFetchr().fetchRecentPhotos();
        }
        else{
            items = new FlickrFetchr().searchPhotos(query);
        }
        if (items.size() == 0) {
            return;
        }
        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: "+ resultId);
        }
        else{
            Log.i(TAG, "Got a new result: "+ resultId);
            //Using notification to notify user
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            /*NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, notification);
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);    //send out broadcast every time new search result are available;
                                                                                  //using private permission here makes sure any application must have the same permission to be triggered by the intent
            */
            showBackGroundNotification(0, notification);
        }
        QueryPreferences.setLastResultId(this, resultId);
    }

    private void showBackGroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }

    //=======isNetworkAvailableAndConnected()======// A helper function to check for network connectivity
    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
