package com.example.cinnamon_roll.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by sriracha-sauce on 8/25/16.
 */
public class PhotoPageFragment extends VisibleFragment {
    private static final String TAG = "PhotoGalleryPage";
    private static final String ARG_URI = "photo_page_url";
    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //---------ONCREATE()-------------//
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }
    //--------ONCREATEVIEW()----------//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState){
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);
        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);//enable JavaScript because flickr photo pages use JavaScript
        mWebView.setWebChromeClient(new WebChromeClient(){  //set WebChromeClient to control progress bar
            @Override
            public void onProgressChanged(WebView webView, int newProgress){//this will be called when progress changed
                if (newProgress == 100) {//if progress is 100, make it invisible..
                    mProgressBar.setVisibility(View.GONE);
                    Log.i(TAG, "onProgressChanged called, progress 100%: "+ newProgress);
                }
                else{                  //..else update progress bar accordingly
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                    Log.i(TAG, "onProgressChanged called, progress: "+ newProgress);
                }
            }
            @Override
            public void onReceivedTitle(WebView webView, String title){ //called when WebChromeClient receive webpage's title
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setTitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){//if return false, webview will handle when user click on links on the page..
                return false;                                                       //..just like a browser would
            }
        });
        mWebView.loadUrl(mUri.toString());//actually load the photo web page
        return v;
    }
}
