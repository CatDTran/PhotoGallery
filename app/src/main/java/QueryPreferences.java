import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by trand_000 on 7/12/2016.
 */
public class QueryPreferences {
    private static String PREF_SEARCH_QUERY = "searchQuery";//will serve as key in SharedPreference
    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, null);//get the stored SharedPreferences value using key
    }
    public static void setStoredQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();//set a stored SharedPreferences value with the given key
    }
}
