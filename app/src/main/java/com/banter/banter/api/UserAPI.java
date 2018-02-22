package com.banter.banter.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import com.banter.banter.BuildConfig;

/**
 * Created by e-carlin on 2/15/18.
 */

public class UserAPI {
    private static final String TAG = "UserAPI";

    private UserAPI() {
        throw new UnsupportedOperationException();
    }

    public static void registerUser(String email, Context ctx, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        try {
            RequestQueueSingleton requestQueue = RequestQueueSingleton.getInstance(ctx);

            JSONObject sendObj = new JSONObject();
            sendObj.put("email", email);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Constants.REGISTER_USER_ENDPOINT,
                    sendObj,responseListener,errorListener);
            requestQueue.add(jsonObject);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception when trying to call api to register user: "+e);
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
