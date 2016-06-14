package com.example.cinnamon_roll.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by trand_000 on 6/12/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    public ThumbnailDownloader(){
        super(TAG);
    }
    public void queueThumbnail(T target, String url){
        Log.i(TAG,"Got a URL: "+ url);
    }
}
