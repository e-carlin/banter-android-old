package com.banter.banter.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.banter.banter.AWSCognitoHelper;
import com.banter.banter.BuildConfig;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by evan.carlin on 2/16/2018.
 */

public class AccountAPI {
    private static String TAG = "AccountAPI";

    public static void addAccount(JSONObject data, Context ctx, Response.Listener responseListener, Response.ErrorListener errorListener){
        addPlaidAccount(data, ctx, responseListener, errorListener);
    }

    /**
     * Helper method for adding an account. If we ever decide to not use Plaid we can *hopefully* just point addAccount to a
     * different method and no other code will need to be changed.
     *
     * @param data
     * @param ctx
     */
    private static void addPlaidAccount(JSONObject data, Context ctx, Response.Listener responseListener, Response.ErrorListener errorListener) {
            RequestQueueSingleton requestQueue = RequestQueueSingleton.getInstance(ctx);
            Log.i(TAG, "Sending account data to our api. URL: "+Constants.ADD_ACCOUNT_ENDPOINT+"  DATA: "+data.toString());


            //TODO: subclass this so we don't have to add the getHeaders() method everytime we want to make an authorized call to our api
            JsonObjectRequest jsonObject = new JsonObjectRequest(Constants.ADD_ACCOUNT_ENDPOINT, data, responseListener, errorListener) {
                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", AWSCognitoHelper.getCurrentSessionJWTAccessToken());
                    return params;
                }
            };
            requestQueue.add(jsonObject);

    }

}
