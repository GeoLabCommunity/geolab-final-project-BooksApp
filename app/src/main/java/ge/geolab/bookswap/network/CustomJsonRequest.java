package ge.geolab.bookswap.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalkh on 20-Jan-16.
 */
public class CustomJsonRequest extends Request {

    Map<String, String> params;
    private Response.Listener listener;

    public CustomJsonRequest(int requestMethod, String url, Map<String, String> params,
                             Response.Listener responseListener, Response.ErrorListener errorListener) {

        super(requestMethod, url, errorListener);


        params=new HashMap<String, String>();
        //params.put("key", value);
        this.params = params;
        this.listener = responseListener;
    }

    @Override
    protected void deliverResponse(Object response) {
        listener.onResponse(response);

    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {

        return params;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
    }