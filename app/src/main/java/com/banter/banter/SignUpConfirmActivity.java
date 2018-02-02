package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

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
            Intent intent = new Intent(SignUpConfirmActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFailure(Exception exception) {
            Log.e(TAG, "Error confirming user sign up: "+exception);
            confCode.setError("Error. Please try again.");
        }
    };
}
