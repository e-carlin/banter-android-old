package com.banter.banter;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;

/**
 * Created by ecarlin on 1/4/18.
 */

public class AWSCognitoHelper {
    private static CognitoUserPool userPool;
    private static CognitoUserSession currSession;

    private static final Regions cognitoRegion = Regions.US_EAST_1;
    private static final String userPoolId = "us-east-1_M0GwiV1g7";
    private static final String clientId = "77b82fsn31abs9p13uir9f6nt0";
    private static final String clientSecret = "1265kol92vp06blr6f2u4fl1tc5cqtag2l373h47qp61vd9dfqcd";

    public static void init(Context context) {

        userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
    }

    public static CognitoUserPool getCognitoUserPool() {

        return userPool;
    }

    public static void setCurrSession(CognitoUserSession session) {

        currSession = session;
    }

    public static CognitoUserSession getCurrSession() {

        return currSession;
    }

}
