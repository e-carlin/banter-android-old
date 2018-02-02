package com.banter.banter;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

/**
 * Created by ecarlin on 1/4/18.
 */

public class AWSCognitoHelper {
    private static CognitoUserPool userPool;

    private static final String userPoolId = "us-east-1_VU4GdCuOZ";
    private static final String clientId = "b51em6hvi9kldqslihjlv650l";
    private static final String clientSecret = "1kuh2j8lhfedi6q9cft73gq2rgmn07ujed1gqpdhl0t8r2gau29g";
    private static final Regions cognitoRegion = Regions.US_EAST_1;

    public static void init(Context context) {
        userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
    }

    public static CognitoUserPool getCognitoUserPool() {
        return userPool;
    }

}
