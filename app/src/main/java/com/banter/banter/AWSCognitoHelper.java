package com.banter.banter;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

/**
 * Created by ecarlin on 1/4/18.
 */

public class AWSCognitoHelper {
    private static CognitoUserPool userPool;

    private static final Regions cognitoRegion = Regions.US_EAST_1;
    private static final String userPoolId = "us-east-1_DoNQrJT2d";
    private static final String clientId = "629iht78elltqnlugt00522r11";
    private static final String clientSecret = "19djfjs1nf80opps84g43r46u7ollfkckqi7acs5iafjcqsiujsv";

    public static void init(Context context) {
        userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
    }

    public static CognitoUserPool getCognitoUserPool() {
        return userPool;
    }

}
