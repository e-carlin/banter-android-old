package com.banter.banter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
    private final Regions cognitoRegion = Regions.US_EAST_1;

    private EditText email;
    private EditText password;
    private Button signUp;

    private String userEmail;
    private String userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    private void init() {
        email = (EditText) findViewById(R.id.editTextEmail);
        password= (EditText) findViewById(R.id.editTextPassword);

        signUp = (Button) findViewById(R.id.buttonSignUp);
        signUp.setOnClickListener((v) -> {
            userEmail = email.getText().toString();
            userPassword = password.getText().toString();

            System.out.println("*********************************");
            System.out.println("Signing up email: "+email+" password: "+password);
            System.out.println("*********************************");

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            CognitoUserPool userPool = new CognitoUserPool(v.getContext(), userPoolId, clientId, clientSecret, cognitoRegion);

            CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("email", userEmail);

            userPool.signUpInBackground(userEmail, userPassword, userAttributes, null, signupCallback);
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

