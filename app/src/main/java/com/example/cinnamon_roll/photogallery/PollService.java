package com.example.cinnamon_roll.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.List;

/**
 * Created by trand_000 on 7/16/2016.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";
    //--------------------newIntent()-----------------------//
    public static Intent newIntent(Context context){
        return new Intent(context, PollService.class);
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
            Log.i(TAG, "Got a new result: "+ resultId);
        }
        else{
            Log.i(TAG, "Got a new result: "+ resultId);
        }
    }
    //=======isNetworkAvailableAndConnected()======// A helper function to check for network connectivity
    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
