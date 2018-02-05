package com.banter.banter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoParameterInvalidException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException;

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
            if(validEmail(userEmail) && validPassword(userPassword)) {
                AWSCognitoHelper.getCognitoUserPool().getUser(userEmail).getSessionInBackground(authenticationHandler);
            }
        });
    }

    private boolean validEmail(String email) {
        if(email.isEmpty()){
            emailField.setError("Cannot be empty");
            return false;
        }
        // TODO: Add email valid regex
        return true;
    }
    private boolean validPassword(String password) {
        if(password.isEmpty()) {
            passwordField.setError("Cannot be empty");
            return false;
        }
        if(password.length() < 6) {
            passwordField.setError("Must be at least 6 characters long");
        }
        //TODO: Add more password validation
        return true;
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, "Success signing in");
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

        //TODO: Clean up the log statements
        @Override
        public void onFailure(Exception exception) {
            String errorMessage = "There was an error. Please try again.";
            Log.e(TAG, "M: "+exception.getMessage());
            if(exception instanceof UserNotFoundException){
                Log.d(TAG, "Sign in failed. Email not found. Exception: "+exception);
                Log.d(TAG, Log.getStackTraceString(exception));

                errorMessage = "Email not found.";
                emailField.setError(errorMessage);
                return;
            }
            if(exception instanceof CognitoParameterInvalidException && exception.getMessage().contains("user ID cannot be null")) {
                Log.d(TAG, "Sign in failed. Email field empty. Exception: "+exception);
                Log.d(TAG, Log.getStackTraceString(exception));

                errorMessage = "Cannot be empty";
                emailField.setError(errorMessage);
                return;
            }
            //I Think the text for this message isn't the message that corresponds with credentials did not mathc
            if((exception instanceof CognitoInternalErrorException && exception.getMessage().contains("Failed to authenticate user")) ||
                    (exception instanceof NotAuthorizedException && exception.getMessage().contains("Incorrect username or password"))) {
                Log.d(TAG, "Sign in failed. Credentials did not match. Exception: "+exception);
                Log.d(TAG, Log.getStackTraceString(exception));

                errorMessage = "The credentials you supplied did not match";

            }
            Log.e(TAG, "CLASS: "+exception.getClass());
            Log.d(TAG, "Sign in failed: "+exception);
            Log.d(TAG, Log.getStackTraceString(exception));

            AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
            alertDialog.setTitle("Sign in error");
            alertDialog.setMessage(errorMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
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
