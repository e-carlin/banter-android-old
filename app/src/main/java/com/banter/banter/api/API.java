package com.banter.banter.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.banter.banter.BuildConfig;
import com.banter.banter.PlaidAddAccountActivity;
import com.banter.banter.SignInActivity;
import com.banter.banter.SignUpActivity;
import com.banter.banter.UserDetailsActivity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by evan.carlin on 2/6/2018.
 */

public class API {
    private static final String TAG = "APi";

    private static final String API_BASE_URL = BuildConfig.BANTER_BASE_URL;
    private static final String EXCHANGE_PUBLIC_TOKEN_ENDPOINT = "/exchange_plaid_public_token";
    private static final String REGISTER_USER_ENDPOINT = "/user/register";

    public static void sendPlaidPublicToken(Context ctx, JSONObject data) {
        String url = API_BASE_URL+EXCHANGE_PUBLIC_TOKEN_ENDPOINT;
        Log.e(TAG, "URL: "+url);
        Log.d(TAG, "Sending public token to our api");
        RequestQueue requestQueue = RequestQueueSingleton.getInstance(ctx).getRequestQueue();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, API_BASE_URL+EXCHANGE_PUBLIC_TOKEN_ENDPOINT, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the response string.
                        System.out.println("Response is: " + response.toString());
                        ctx.startActivity(new Intent(ctx, UserDetailsActivity.class));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error sending plaid public token to our api: "+error);
                try {
                    Log.e(TAG, "Error message: " + new String(error.networkResponse.data, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error parsing the exchange_public_token error response body");
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                Log.e(TAG, Log.getStackTraceString(error));

               showSendPublicTokenErrorAlerDialog(ctx);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private static void showSendPublicTokenErrorAlerDialog(Context ctx) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Error saving your account");
        alertDialog.setMessage("Sorry, our system encountered an error and was unable to save your account. Please try again.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ctx.startActivity(new Intent(ctx, PlaidAddAccountActivity.class));
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        ctx.startActivity(new Intent(ctx, UserDetailsActivity.class));
                    }
                });
        alertDialog.show();
    }

}
