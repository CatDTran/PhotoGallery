package com.example.cinnamon_roll.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
        try//try to parse uri
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
            JSONObject jsonBody = new JSONObject(jsonString);//initialize a JSONObject using jsonString
        }
        catch(JSONException je)//in case initializing a JSONObject fails
        {
            Log.e(TAG,"Failed to parse JSON", je);
        }
        catch (IOException ioe)//in case initialize jsonString fails
        {
            Log.e(TAG, "Failed to fetch items: "+ ioe);
        }
    }

    //
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException{
        JSONObject photosJSONObject = jsonBody.getJSONObject("photo");
        JSONArray photoJsonArray = photosJSONObject.getJSONArray("photo");
        for(int i =0; i < photoJsonArray.length(); i++)
        {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("tittle"));
            if(!photoJsonObject.has("url_s"))//if the photoJsonObject does not have url_s
            {
                continue;//then move to the next iteration
            }
            item.setUrl(photoJsonObject.getString("url_s"));//set url for GalleryItem if there is url_s parameter for each iteration
            items.add(item);                                //also add item to the list
        }
    }
}
