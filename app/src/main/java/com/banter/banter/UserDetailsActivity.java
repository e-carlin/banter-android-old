package com.banter.banter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

import static java.lang.System.exit;

public class UserDetailsActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

    private EditText userEmail;
    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init();
    }

    private void init() {

        signOut = (Button) findViewById(R.id.button_sign_out);
        signOut.setOnClickListener((v) -> {
            Log.d(TAG, "Sign out button pressed");


            AWSCognitoHelper.getCognitoUserPool().getCurrentUser().signOut();

            Intent intent = new Intent(UserDetailsActivity.this, SignInOrUpActivity.class);
            startActivity(intent);

        });



//        userEmail = (EditText) findViewById(R.id.text_email);
//        userEmail.setText(user.getUserId());
//        userEmail.setText("HELLO USER");
    }
}
