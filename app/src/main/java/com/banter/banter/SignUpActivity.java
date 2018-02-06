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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;

    private String userEmail;
    private String userPassword;
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
            userEmail = emailField.getText().toString();
            userPassword = passwordField.getText().toString();

            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            AWSCognitoHelper.getCognitoUserPool().signUpInBackground(userEmail, userPassword, userAttributes, null, signupHandler);
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
                Intent intent = new Intent(SignUpActivity.this, UserDetailsActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            Log.e(TAG, "User sign up failed: "+exception);
        }
    };

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("source","signup");
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivity(intent);
    }

}

