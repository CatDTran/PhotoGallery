package com.example.cinnamon_roll.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sriracha-sauce on 6/3/16.
 */
public class FlickrFetchr {

    //fetch raw bytes and return an array of bytes
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);//create URL object from a string
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//create a connection object (URLConnection) and cast it to HttpURLConnection
                                                                                //this gives http-specific interfaces

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();//actually connect the connections object to an InputStream
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){//throw exception when connection fails
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){//keep reading raw bytes until InputStream runs out of data
                out.write(buffer, 0, bytesRead);//write to ByteArrayOutputStream
            }
            out.close();//close ByteArrayOutputStream after done reading
            return out.toByteArray();//return the bytes array
        }
        finally {
            connection.disconnect();
        }
    }

        public String getUrlString(String urlSpec) throws IOException{
            return new String(getUrlBytes(urlSpec));
        }
}
