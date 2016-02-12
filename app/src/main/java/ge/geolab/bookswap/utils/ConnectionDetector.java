package ge.geolab.bookswap.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.VolleyError;

import ge.geolab.bookswap.R;


/**
 * Created by g.vakhtangishvili on 8/27/13.
 */
public class ConnectionDetector {

    /**
     * Checking Whether there's a connection to internet or not
     *
     * @return
     */
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            NetworkInfo [] info = connectivity.getAllNetworkInfo();
            if(info != null)
                for(int i = 0; i < info.length; i++)
                    if(info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        //Toast.makeText(context, context.getResources().getString(R.string.ar_aris_internet_kavshiri), Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean enoughConnectionToInternet(VolleyError error, Context context){
        if(error.networkResponse == null || error.networkResponse.statusCode == 504){
            //Toast.makeText(context, context.getResources().getString(R.string.ar_aris_internet_kavshiri), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
