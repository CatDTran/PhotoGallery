package com.example.cinnamon_roll.photogallery;

import android.net.Uri;

/**
 * Created by trand_000 on 6/5/2016.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;
    //get Caption
    public String getCaption() {
        return mCaption;
    }
    //set caption
    public void setCaption(String caption) {
        mCaption = caption;
    }
    //get ID
    public String getId() {
        return mId;
    }
    //set ID
    public void setId(String id) {
        mId = id;
    }
    //get URL
    public String getUrl() {
        return mUrl;
    }
    //Set URL
    public void setUrl(String url) {
        mUrl = url;
    }
    //Get owner ID
    public String getOwner(){
        return mOwner;
    }
    //Set owner ID
    public void setOwner(String owner){
        mOwner = owner;
    }
    //Build the photo page Uri base on owner ID and photo ID
    public Uri getPhotoPageUri(){
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId).build();
    }
    @Override
    public String toString()
    {
        return mCaption;
    }
}
