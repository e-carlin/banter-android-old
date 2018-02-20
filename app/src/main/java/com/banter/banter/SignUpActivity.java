package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.android.volley.VolleyError;
import com.banter.banter.api.RegisterUserResult;
import com.banter.banter.api.UserAPI;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int CONFIRM_USER_REQUEST = 2;

    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;

    private String emailText;
    private String passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    private void init() {
        Log.i(TAG, "Initializing activity");
        emailField = (EditText) findViewById(R.id.text_email);
        emailField.setHint(getString(R.string.text_sign_up_email));

        passwordField = (EditText) findViewById(R.id.text_password);
        passwordField.setHint(getString(R.string.text_sign_up_password));

        signUpButton = (Button) findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener((v) -> {
            Log.i(TAG, "User sign up button pressed");
            emailText = emailField.getText().toString();
            passwordText = passwordField.getText().toString();

            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            AWSCognitoHelper.getCognitoUserPool().signUpInBackground(emailText, passwordText, userAttributes, null, signupHandler);
        });
    }

    SignUpHandler signupHandler = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            Log.d(TAG, "User sign up was succesful");

            // Check if this user (cognitoUser) has to be confirmed
            if(!userConfirmed) {
                // This user has to be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                Log.d(TAG, "User must be confirmed.");
                confirmSignUp(cognitoUserCodeDeliveryDetails);
            }
            else {
                Log.d(TAG, "User does not need to be confirmed");
                registerUser(emailText);
                exitSuccess(emailText, passwordText);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            Log.e(TAG, "User sign up failed: "+exception);
        }
    };


    private void registerUser(String email) {
        Log.e(TAG, "Register user called!!!!!!!!!!!!!!!!!");
        RegisterUserResult registerUserResultCallback = new RegisterUserResult() {
            @Override
            public void notifySuccess(JSONObject response) {
                //TODO: Change to log.i and call exitSuccess() instead of just logging
                Log.e(TAG, "Success registering user");
                Log.e(TAG, response.toString());
                exitSuccess(email, passwordText);
            }

            @Override
            public void notifyError(VolleyError error) {
                //TODO: Change to log.i and alert user of erro instead of just logging
                Log.e(TAG, "Fatal error registering user. We should never get here... "+error.toString());
                exitError();
            }
        };
        UserAPI.registerUser(email, registerUserResultCallback, this);
    }



    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case CONFIRM_USER_REQUEST:
                if(resultCode == RESULT_OK) {
                    Log.i(TAG, "Success confirming user. Going back to sign up to log them in.");
                    exitSuccess(emailText, passwordText);
                }
                else {
                    Log.e(TAG, "The result of confirming the user was not ok. We can't automatically sign them in.");
                    //TODO: Double check that this work and we don't get some null error when trying to putExtra with ""
                    exitError();
                }
        }
    }

    private void exitSuccess(String email, String password) {
        Intent intent = new Intent();
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void exitError() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        intent.putExtra("email", emailText);
        intent.putExtra("source","signup");
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, CONFIRM_USER_REQUEST);
    }

}

