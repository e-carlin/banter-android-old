package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;

public class SignUpActivity extends AppCompatActivity {

    private final String userPoolId = "us-east-1_VU4GdCuOZ";
    private final String clientId = "b51em6hvi9kldqslihjlv650l";
    private final String clientSecret = "1kuh2j8lhfedi6q9cft73gq2rgmn07ujed1gqpdhl0t8r2gau29g";
    private static final Regions cognitoRegion = Regions.US_EAST_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void SignUpButtonPressed(View view) {
        System.out.println("***** Sign up button pressed in sign up activity *****");

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        CognitoUserPool userPool = new CognitoUserPool(view.getContext(), userPoolId, clientId, clientSecret, cognitoRegion);
        System.out.println("***** Pool is "+userPool+" *****");

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", "evforward123@gmail.com");

        userPool.signUpInBackground("evforward123@gmail.com", "12345678", userAttributes, null, signupCallback);
    }

    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            System.out.println("****** SIGN UP SUCCESSFUL ******");

            // Check if this user (cognitoUser) has to be confirmed
            if(!userConfirmed) {
                // This user has to be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                System.out.println("******** USER MUST BE CONFIRMED *******");
            }
            else {
                // The user has already been confirmed
                System.out.println("****** USER DOESN\'t NEED TO BE CONFIRMED *****");
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            System.out.println("**** SING UP FAILED: "+exception);
        }
    };

}

