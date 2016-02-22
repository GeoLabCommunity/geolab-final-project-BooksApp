package ge.geolab.bookswap.network.cloudMessaging;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ge.geolab.bookswap.R;

/**
 * Created by dalkh on 28-Jan-16.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";
    private Context context=this;
    private String registerUrl;

    private String userId;

    public RegistrationIntentService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(AccessToken.getCurrentAccessToken()!=null)
        userId= Profile.getCurrentProfile().getId();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

         registerUrl=getResources().getString(R.string.register_messaging_url);
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            Log.d(TAG, "GCM Registration Token: " + token);
            // save token
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();
            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

        }
    }
    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest jsonArrayRequest = new StringRequest(Request.Method.POST,registerUrl + "u_id/"+userId+"/token/"+token, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Volley Error: " + error.getMessage());

                sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();


            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("u_id", userId);
                params.put("token", token);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

}