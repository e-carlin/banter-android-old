package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class SignInOrUpActivity extends AppCompatActivity {

    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_or_up);

        AWSCognitoHelper.init(getApplicationContext());
        init();
    }

    private void init() {
        signIn = (Button) findViewById(R.id.button_sign_in);
        signIn.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        signUp = (Button) findViewById(R.id.button_sign_up);
        signUp.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}
