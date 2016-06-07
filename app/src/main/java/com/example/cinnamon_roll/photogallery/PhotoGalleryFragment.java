package com.example.cinnamon_roll.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by sriracha-sauce on 6/3/16.
 */
public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";

    //custom constructor
    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    //ONCREATE()
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    //ONCREATEVIEW()
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return v;
    }

    //ViewHolder for RecyclerView
    private class PhotoHolder extends RecyclerView.ViewHolder{
        private TextView mTittleTextView;
        public PhotoHolder(View itemView){
            super(itemView);
            mTittleTextView = (TextView) itemView;
        }
        public void bindGalleryItem(GalleryItem item){
            mTittleTextView.setText(item.toString());
        }
    }

    //Adapter for ViewHolder and RecyclerView
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems){//custom constructor
            mGalleryItems = galleryItems;
        }
        @Override//called by system to create ViewHolder
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }
        @Override//called by system to bind ViewHolder
        public void onBindViewHolder(PhotoHolder photoHolder, int position){
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
        }
        @Override//called by system to get item count; must be implemented
        public int getItemCount(){
            return mGalleryItems.size();
        }
    }

    //Creating a background thread for networking
    private class FetchItemsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){//implement this method to do tasks in the background thread
//            try
//            {
//                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: "+ result);
//            }
//            catch (IOException ioe)
//            {
//                Log.e(TAG, "Failed to fetch URL: "+ ioe);
//            }
            new FlickrFetchr().fetchItems();
            return  null;
        }
    }

}
