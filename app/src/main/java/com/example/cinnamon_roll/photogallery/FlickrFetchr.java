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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sriracha-sauce on 6/3/16.
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY  = "249365f9bd0c290b06777bba4622d745";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT =  Uri.parse("https://api.flickr.com/services/rest/")
                                            .buildUpon()
                                            .appendQueryParameter("api_key", API_KEY)
                                            .appendQueryParameter("format", "json")
                                            .appendQueryParameter("nojsoncallback", "1")
                                            .appendQueryParameter("extras", "url_s")
                                            .build();
    //=====================getUrlBytes(String)===============================//
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
    //=======================================================================//
    //~~~~~~~~~~~~~~~~~~~~~~~getUrlString~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    //--------------------------fetchRecentPhotos()-------------------------//
    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }
    //----------------------------------------------------------------------//
    //#########################  searchPhotos()  ###########################// Method called to search photos
    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }
    //######################################################################//
    //**********************downloadGalleryItems()*************************************//
    //Called to fetch items from url (data returned is List of json objects)
    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items = new ArrayList<>();
        try//try to parse uri
        {

            String jsonString = getUrlString(url);//calling getUrlString(String url) will return the data at located at url
            Log.i(TAG,"Received JSON: "+ jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);//initialize a JSONObject using jsonString
            parseItems(items, jsonBody);//parse the entire json body
        }
        catch(JSONException je)//in case initializing a JSONObject fails
        {
            Log.e(TAG,"Failed to parse JSON", je);
        }
        catch (IOException ioe)//in case initialize jsonString fails
        {
            Log.e(TAG, "Failed to fetch items: "+ ioe);
        }
        return items;
    }
    //**********************************************************************//
    //============================buildUrl()================================// (A helper method)
    private String buildUrl(String method, String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD))
        {
            uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();
    }
    //======================================================================//
    //----------------------parseItems()------------------------------------//
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException{
        JSONObject photosJSONObject = jsonBody.getJSONObject("photos");//get the JSONObject (master) that contains all other nested JSONObject which contain photos' information
        JSONArray photoJsonArray = photosJSONObject.getJSONArray("photo");//get a JSONArray of objects which contain "photo" string
        for(int i =0; i < photoJsonArray.length(); i++)
        {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            if(!photoJsonObject.has("url_s"))//ignore Json object that does not have url_s
            {
                continue;//then move to the next iteration
            }
            item.setUrl(photoJsonObject.getString("url_s"));//set url for GalleryItem if json object has url_s parameter
            item.setOwner(photoJsonObject.getString("owner"));//set owner property from JSON object
            items.add(item);                                //also add item to the list
        }
    }
    //-----------------------------------------------------------------------//
}
