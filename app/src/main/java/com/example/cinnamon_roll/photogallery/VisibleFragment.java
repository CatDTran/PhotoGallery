package com.example.cinnamon_roll.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by trand_000 on 8/16/2016.
 */
public abstract class VisibleFragment extends Fragment{

    private static final String TAG = "VisibleFragment";

    @Override
    public void onStart(){
        Log.i(TAG, "VisibleFragment's onStart() called");
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);    //register dynamic broadcast receiver with filter
                                                                                                        //can only be triggered by component that have "PollService.PERM_PRIVATE" permission
    }

    @Override
    public void onStop(){
        Log.i(TAG, "VisibleFragment's onStop() called");
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);  //unregister dynamic broadcast receiver
    }

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(getActivity(), "Got a broadcast: "+ intent.getAction(), Toast.LENGTH_LONG).show();
            //If this onReceive() is called, that mean the fragemnt is visible, thus cancel notification
            Log.i(TAG, "Canceling notification...");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
