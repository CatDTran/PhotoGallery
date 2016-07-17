package com.example.cinnamon_roll.photogallery;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by trand_000 on 7/12/2016.
 */
public class QueryPreferences {
    private static String PREF_SEARCH_QUERY = "searchQuery";//will serve as key in SharedPreference
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    //----------------get and set stored query------------------//
    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);//get the stored SharedPreferences value using key
    }
    public static void setStoredQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();//set a stored SharedPreferences value with the given key
    }
    //===============getj and setLastResultId()======================// Get and set the id of the last photo item fetched
    public static String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, null);
    }
    public static void setLastResultId(Context context, String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID, lastResultId).apply();
    }
}
