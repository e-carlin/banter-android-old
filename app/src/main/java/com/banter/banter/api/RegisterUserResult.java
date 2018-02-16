package com.banter.banter.api;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by e-carlin on 2/15/18.
 */

public interface RegisterUserResult {
    void notifySuccess(JSONObject response);
    void notifyError(VolleyError error);
}
