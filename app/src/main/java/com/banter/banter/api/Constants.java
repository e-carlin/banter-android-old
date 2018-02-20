package com.banter.banter.api;

import com.banter.banter.BuildConfig;

/**
 * Created by e-carlin on 2/15/18.
 */

public interface Constants {
     /* Banter API base url */
     String BANTER_BASE_URL = BuildConfig.BANTER_BASE_URL;


     /* User endpoints*/
     String REGISTER_USER_ENDPOINT = BANTER_BASE_URL + "/user/register";

     /* Account endpoints */
     String ACCOUNT_ENDPOINT = BANTER_BASE_URL + "/account";
     String ADD_ACCOUNT_ENDPOINT = ACCOUNT_ENDPOINT + "/add";
}
