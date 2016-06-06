package com.example.cinnamon_roll.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sriracha-sauce on 6/3/16.
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY  = "249365f9bd0c290b06777bba4622d745";
    //fetch raw bytes and return an array of bytes
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);//create URL object from a string
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//create a connection object (URLConnection) and cast it to HttpURLConnection
                                                                                //this gives http-specific interfaces

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();//get input stream from connection
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)//throw exception when connection fails
            {
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            //keep reading raw bytes until InputStream runs out of data
            while((bytesRead = in.read(buffer)) > 0)
            {
                //write to ByteArrayOutputStream
                out.write(buffer, 0, bytesRead);
            }
            out.close();//close ByteArrayOutputStream after done reading
            return out.toByteArray();//return the bytes array
        }
        finally
        {
            connection.disconnect();
        }
    }

        public String getUrlString(String urlSpec) throws IOException{
            return new String(getUrlBytes(urlSpec));
        }

    //Called to fetch items from url (data returned is in JSON format)
    public void fetchItems(){
        try
        {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojasoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);//calling getUrlString(String url) will return the data at located at url
            Log.i(TAG,"Received JSON: "+ jsonString);
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "Failed to fetch items: "+ ioe);
        }
    }
}
