package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.android.volley.VolleyError;
import com.banter.banter.api.RegisterUserResult;
import com.banter.banter.api.UserAPI;

import org.json.JSONObject;

public class SignUpConfirmActivity extends AppCompatActivity {
    private static final String TAG = "SignUpConfirmActivity";

    private EditText confCode;
    private Button confirm;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);

        init();
    }

    private void init() {
        Log.i(TAG, "Initializing activity");

        Bundle extras = getIntent().getExtras();

        email = extras.getString("email");


        confCode = (EditText) findViewById((R.id.text_confirmation_code));
        confCode.setHint(getString(R.string.text_sign_up_confirmation_code));

        confirm = (Button) findViewById(R.id.button_confirm);
        confirm.setOnClickListener((v) -> {
            Log.d(TAG, "Confirm user sign up submit confirmation code button pressed with "+confCode.getText());
            AWSCognitoHelper.getCognitoUserPool().getUser(email).confirmSignUpInBackground(confCode.getText().toString(),
                    true, confHandler);
        });
    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            Log.i(TAG, "Succes confirming user sign up");
            registerUser(email);
        }

        @Override
        public void onFailure(Exception exception) {
            Log.e(TAG, "Error confirming user sign up: "+exception);
            confCode.setError("Error. Please try again.");
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
                exitSuccess();
            }

            @Override
            public void notifyError(VolleyError error) {
                //TODO: Change to log.i and alert user of erro instead of just logging
                Log.e(TAG, "Fatal error registering user. We should never get here... "+error.toString());
                //TODO: we need way better handling here. This is a terrible state for the app to be in
            }
        };
        UserAPI.registerUser(email, registerUserResultCallback, this);

    }

    private void exitSuccess() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
