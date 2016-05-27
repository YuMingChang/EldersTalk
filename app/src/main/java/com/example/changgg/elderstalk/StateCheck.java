package com.example.changgg.elderstalk;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// How can I use getSystemService in a non-activity class (LocationManager)?:
// http://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager

// android: getSystemService calling from a non extended activity class not working:
// http://stackoverflow.com/questions/17178941/android-getsystemservice-calling-from-a-non-extended-activity-class-not-working
public class StateCheck {
    private static final String TAG = "StateCheck";

    Context scContext;
    public StateCheck(Context mContext) {
        this.scContext = mContext;
    }

    // To Check Netwrok
    // http://stackoverflow.com/questions/9570237/android-check-internet-connection
    // http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) scContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if(netInfo != null) {
//            Log.i(TAG, "get NetworkInfo TypeName: " + netInfo.getTypeName());
//            Log.i(TAG, "get NetworkInfo State: " + String.valueOf(netInfo.getState()));
//        }
        return (netInfo != null) && netInfo.isConnectedOrConnecting();
    }

    // How to check if a service is running on Android?:
    // http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    protected boolean isRadioServiceRunning(Class<?> serviceClass) {
        ActivityManager am = (ActivityManager) scContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
            if ( serviceClass.getName().equals(service.service.getClassName()) ){
                return true;
            }
        }
        return false;
    }
}
