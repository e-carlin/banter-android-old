package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class SignInOrUpActivity extends AppCompatActivity {
    private static final String TAG = "SignInOrUpActivity";

    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_or_up);

        AWSCognitoHelper.init(getApplicationContext()); //TODO: This should probably be done somewhere else, at the very least not on the ui thread
        init();
    }

    private void init() {
        Log.i(TAG, "Initializing acitivity");
        signIn = (Button) findViewById(R.id.button_sign_in);
        signIn.setOnClickListener((v) -> {
            Log.d(TAG, "User clicked sign in button. Going to sign in activity.");
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        signUp = (Button) findViewById(R.id.button_sign_up);
        signUp.setOnClickListener((v) -> {
            Log.d(TAG, "User clicked sign up button. Going to sign up activity");
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
