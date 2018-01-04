package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
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
    }

}
