package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class SignUpConfirmActivity extends AppCompatActivity {

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
        Bundle extras = getIntent().getExtras();

        email = extras.getString("email");


        confCode = (EditText) findViewById((R.id.text_confirmation_code));

        confirm = (Button) findViewById(R.id.button_confirm);
        confirm.setOnClickListener((v) -> {
            AWSCognitoHelper.getCognitoUserPool().getUser(email).confirmSignUpInBackground(confCode.getText().toString(),
                    true, confHandler);
        });
    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            System.out.println("***** USER CONFIRMATION SUCCESS *****");
                Intent intent = new Intent(SignUpConfirmActivity.this, SignInActivity.class);
                startActivity(intent);
        }

        @Override
        public void onFailure(Exception exception) {
            System.out.println("***** USER CONFIRMATION FAILED *****");
            System.out.println(exception);
        }
    };
}
