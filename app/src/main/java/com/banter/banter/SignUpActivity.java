package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class SignUpActivity extends AppCompatActivity {

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
        emailField = (EditText) findViewById(R.id.text_email);
        passwordField = (EditText) findViewById(R.id.text_password);

        signUpButton = (Button) findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener((v) -> {
            userEmail = emailField.getText().toString();
            userPassword = passwordField.getText().toString();

            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("email", userEmail);

            AWSCognitoHelper.getCognitoUserPool().signUpInBackground(userEmail, userPassword, userAttributes, null, signupCallback);
        });
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
                confirmSignUp(cognitoUserCodeDeliveryDetails);
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

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, SignUpConfirmActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("source","signup");
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }

}

