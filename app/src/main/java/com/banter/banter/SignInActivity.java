package com.banter.banter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private TextView signUpMessage;
    private Button signInButton;
    private Button signUpButton;

    private String emailText;
    private String passwordText;

    private static final int SIGN_UP_USER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        AWSCognitoHelper.init(getApplicationContext()); //TODO: This should probably be done somewhere else, at the very least not on the ui thread

        init();
    }

    private void init() {
        Log.i(TAG, "Initializing activity");
        emailField = (EditText) findViewById(R.id.text_email);
        emailField.setHint(getString(R.string.text_sign_in_email));

        passwordField = (EditText) findViewById(R.id.text_password);
        passwordField.setHint(getString(R.string.text_sign_in_password));

        signInButton = (Button) findViewById(R.id.button_sign_in);
        signInButton.setText(R.string.button_sign_in);
        signInButton.setOnClickListener((v) -> {
            Log.i(TAG, "Sign in button pressed");
            emailText = emailField.getText().toString();
            passwordText = passwordField.getText().toString();
            if(isValidEmail(emailText) && validPassword(passwordText)) {
                AWSCognitoHelper.getCognitoUserPool().getUser(emailText).getSessionInBackground(authenticationHandler);
            }
        });

        signUpMessage = (TextView) findViewById(R.id.text_sign_up_message);
        signUpMessage.setText(R.string.text_sign_up_message);

        signUpButton = (Button) findViewById(R.id.button_sign_up);
        signUpButton.setText(R.string.button_sign_up);
        signUpButton.setOnClickListener((V) -> {
            Log.i(TAG, "Sign up button pressed");
            Intent intent = new Intent(V.getContext(), SignUpActivity.class);
            startActivityForResult(intent, SIGN_UP_USER_REQUEST);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SIGN_UP_USER_REQUEST:
                if(resultCode == RESULT_OK) {
                    Log.i(TAG, "Back from sign up. Signing in for user with the credentials from sign up.");
                    emailText = data.getStringExtra("email");
                    emailField.setText(emailText);
                    passwordText = data.getStringExtra("password");
                    passwordField.setText(passwordText);
                    if(!emailText.isEmpty() && !passwordText.isEmpty()) {
                        AWSCognitoHelper.getCognitoUserPool().getUser(emailText).getSessionInBackground(authenticationHandler);
                    }
                    else {
                        Log.w(TAG, "The username and/or password resulting from the signUpActivity were empty");
                    }
                }
                else if (resultCode == RESULT_CANCELED){
                    //One example of how we can get to this state is if the usr presses the sign up button, and then in the signUpActivity clicks the back arrrow
                    Log.w(TAG, "The result of signing up the user was not ok. We can't automatically sign them in.");
                }
        }
    }

    private boolean isValidEmail(String email) {
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
            AWSCognitoHelper.setCurrSession(cognitoUserSession);
//            AppHelper.newDevice(device);
            Intent intent = new Intent(SignInActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, passwordText, null);

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
            String errorMessage = "There was an error. Please try again.";
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

                errorMessage = "Your username or password is incorrect";

            }
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
