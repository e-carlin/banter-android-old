package com.banter.banter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {

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
        emailField  = (EditText) findViewById(R.id.text_email);
        passwordField = (EditText) findViewById(R.id.text_password);

        signInButton = (Button) findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener((v) -> {
            userEmail = emailField.getText().toString();
            userPassword = passwordField.getText().toString();

            //TODO: Sign in the user
        });

        //Sign in handler


    }
}
