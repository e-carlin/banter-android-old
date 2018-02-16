package com.banter.banter.api;

import android.content.Context;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import com.banter.banter.BuildConfig;
import com.banter.banter.Constants;

/**
 * Created by e-carlin on 2/15/18.
 */

public class UserAPI {
    private static final String TAG = "UserAPI";


    public static void registerUser(String email, RegisterUserResult resultCallback, Context ctx) {
        try {
            RequestQueueSingleton requestQueue = RequestQueueSingleton.getInstance(ctx);

            JSONObject sendObj = new JSONObject();
            sendObj.put("email", email);

            JsonObjectRequest jsonObject = new JsonObjectRequest(BuildConfig.BANTER_BASE_URL+ Constants.REGISTER_USER_ENDPOINT, sendObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                                resultCallback.notifySuccess(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                        resultCallback.notifyError(error);
                }
            });
            requestQueue.add(jsonObject);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception when trying to register user: "+e);
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}
