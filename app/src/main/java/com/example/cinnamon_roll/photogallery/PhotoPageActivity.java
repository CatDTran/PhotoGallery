package com.example.cinnamon_roll.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.webkit.WebView;

/**
 * Created by sriracha-sauce on 8/25/16.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context, Uri photoPageUri){  //will be called by another class to create an intent that start this Activity
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment(){
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    //Overriding this to enable go-back functionality inside the WebView
    @Override
    public void onBackPressed(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        PhotoPageFragment photoPageFragment = (PhotoPageFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        WebView webView = photoPageFragment.getWebView();

        if(webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    };
}
