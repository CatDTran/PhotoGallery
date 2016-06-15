package com.example.cinnamon_roll.photogallery;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by trand_000 on 6/12/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    //----------CONSTRUCTOR---------------------//
    public ThumbnailDownloader(){
        super(TAG);
    }
    //---------------queueThubmnail()-----------//
    public void queueThumbnail(T target, String url){
        Log.i(TAG,"Got a URL: "+ url);
        if(url == null)
            mRequestMap.remove(target);
        else
        {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }
}
