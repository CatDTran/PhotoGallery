package com.example.cinnamon_roll.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

/**
 * Created by trand_000 on 7/16/2016.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";
    private static final int POLL_INTERNAL = 1000*60;//60 seconds
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
    }

    //==================Constructor========================//
    public PollService(){
        super(TAG);
    }
    //---------------------onHandleIntent()----------------//
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
        }
        QueryPreferences.setLastResultId(this, resultId);
    }
    //=======isNetworkAvailableAndConnected()======// A helper function to check for network connectivity
    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
