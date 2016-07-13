package com.example.cinnamon_roll.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sriracha-sauce on 6/3/16.
 */
public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    //custom constructor
    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    //----------------------ONCREATE()-----------------------------------------//
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();//this handler is automatically attached to this main thread
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);//passing the reference if the handler to background thread
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>(){
            @Override
            public void onThumbnailDownloaded(PhotoHolder photoholder, Bitmap bitmap){
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                photoholder.bindDrawable(drawable);
            }
        });

        mThumbnailDownloader.start();//should call start() before getLooper()
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread starter");
    }
    //-------------------------------------------------------------------------//

    //*********************ONCREATEVIEW()**************************************//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();//this method is called to setup Adapter for RecyclerView
        return v;
    }
    //***********************************************************************//
    //=========================ONDESTROYVIEW()===============================//
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    //-----------------------ONDESTROY()-------------------------------------//
    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailDownloader.quit();//quit background ThumbNailDownloader when fragment is destroyed
        Log.i(TAG, "Background thread destroyed...!");
    }
    //-----------------------------------------------------------------------//
    //***********************ONCREATEOPTIONSMENU()***************************//
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s){//called when user submits a query
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(getActivity(), s);
                updateItems();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s){//called when a single character in search query changes
                Log.d(TAG, "QueryTextChange: "+ s);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener(){//call back to prepopulate the search box with previous search query
            @Override
            public void onClick(View v){
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateItems(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }
    //***********************************************************************//

//=========================RECYCLERVIEW STUFF==============================================//
    //------ViewHolder for RecyclerView------------//
    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;
        public PhotoHolder(View itemView){
            super(itemView);
           mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }
        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
    }
    //--------Adapter for ViewHolder and RecyclerView-------//
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems){//custom constructor
            mGalleryItems = galleryItems;
        }
        //Called by system to create ViewHolder
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new PhotoHolder(view);
        }
        //Called by system to bind ViewHolder
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position){
            GalleryItem galleryItem = mGalleryItems.get(position);
            //Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            //photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }
        @Override//called by system to get item count; must be implemented
        public int getItemCount(){
            return mGalleryItems.size();
        }
    }

    //Called to setup adapter for RecyclerView
    private void setupAdapter(){
        if(isAdded())//check if the fragment has been attached to the hosting Activity
        {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
//============================================================================================//
//-------------------------ASYNCTASK FOR BACKGROUND NETWORKING--------------------------------//
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{
        private String mQuery;
        public FetchItemsTask(String query){
            mQuery = query;
        }
        @Override
        protected List<GalleryItem> doInBackground(Void... params){//implement this method to do tasks in the background thread
//            try
//            {
//                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: "+ result);
//            }
//            catch (IOException ioe)
//            {
//                Log.e(TAG, "Failed to fetch URL: "+ ioe);
//            }
            if(mQuery == null){
                return new FlickrFetchr().fetchRecentPhotos();
            }
            else{
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }
        @Override
        protected void onPostExecute(List<GalleryItem> items){//this will be called after doInBackground() is done
            mItems = items;
            setupAdapter();
        }
    }
//--------------------------------------------------------------------------------------------//
}
