package com.example.cinnamon_roll.photogallery;

/**
 * Created by trand_000 on 6/5/2016.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
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

    @Override
    public String toString()
    {
        return mCaption;
    }
}
