package com.banter.banter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

public class UserDetailsActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

    private EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init();
    }

    private void init() {
        CognitoUser user = AWSCognitoHelper.getCognitoUserPool().getUser();

//        userEmail = (EditText) findViewById(R.id.text_email);
//        userEmail.setText(user.getUserId());
//        userEmail.setText("HELLO USER");
    }

}
