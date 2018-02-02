package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SingInActivity";

    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;

    private String userEmail;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    private void init() {
        Log.i(TAG, "Initializing activity");
        emailField = (EditText) findViewById(R.id.text_email);
        emailField.setHint(getString(R.string.text_sign_in_email));
        emailField.setHint("HI EVAN");

        passwordField = (EditText) findViewById(R.id.text_password);
        passwordField.setHint(getString(R.string.text_sign_in_password));

        signInButton = (Button) findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener((v) -> {
            Log.i(TAG, "Sign in button pressed");
            userEmail = emailField.getText().toString();
            userPassword = passwordField.getText().toString();

            AWSCognitoHelper.getCognitoUserPool().getUser(userEmail).getSessionInBackground(authenticationHandler);
        });
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
//            AppHelper.setCurrSession(cognitoUserSession);
//            AppHelper.newDevice(device);
            Intent intent = new Intent(SignInActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            System.out.println("GETTING DETAILS");
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            //TODO: Implement
            // Multi-factor authentication is required; get the verification code from user
//            multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
//            multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void onFailure(Exception exception) {
            //TODO: Implement
            Log.d(TAG, "User sign in failed: " + exception);
            // Sign-in failed, check exception for the cause
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            //TODO: Implement
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            /**
             if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
             // This is the first sign-in attempt for an admin created user
             newPasswordContinuation = (NewPasswordContinuation) continuation;
             AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
             newPasswordContinuation.getRequiredAttributes());
             closeWaitDialog();
             firstTimeSignIn();
             } else if ("SELECT_MFA_TYPE".equals(continuation.getChallengeName())) {
             closeWaitDialog();
             mfaOptionsContinuation = (ChooseMfaContinuation) continuation;
             List<String> mfaOptions = mfaOptionsContinuation.getMfaOptions();
             selectMfaToSignIn(mfaOptions, continuation.getParameters());
             }
             **/
        }
    };
}
