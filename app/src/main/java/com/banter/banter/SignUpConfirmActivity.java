package com.banter.banter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        if (extras != null) {
            if (extras.containsKey("email")) {
                email = extras.getString("email");
            }
        }

        confCode = (EditText) findViewById((R.id.editTextConfCode));

        confirm = (Button) findViewById(R.id.buttonConfirmCode);
        confirm.setOnClickListener((v) -> {
            AWSCognitoHelper.getCognitoUserPool().getUser(email).confirmSignUpInBackground(confCode.getText().toString(),
                    true, confHandler);
        });
    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            System.out.println("***** USER CONFIRMATION SUCCESS *****");
        }

        @Override
        public void onFailure(Exception exception) {
            System.out.println("***** USER CONFIRMATION FAILED *****");
        }
    };
}
