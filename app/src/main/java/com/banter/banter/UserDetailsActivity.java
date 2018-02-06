package com.banter.banter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button addAccount;
    private CognitoUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init();
    }

    private void init() {

        this.user = AWSCognitoHelper.getCognitoUserPool().getCurrentUser();

        this.userEmail = (EditText) findViewById(R.id.text_user_email);
        this.userEmail.setText(this.user.getUserId());

        this.addAccount = (Button) findViewById(R.id.button_add_account);
        this.addAccount.setText(R.string.button_add_account);
        this.addAccount.setOnClickListener((v) -> {
            Log.d(TAG, "Add account button pressed");

            //TODO: Initiate plaid add account workflow
        });

        this.signOut = (Button) findViewById(R.id.button_sign_out);
        this.signOut.setText(R.string.button_sign_out);
        this.signOut.setOnClickListener((v) -> {
            Log.d(TAG, "Sign out button pressed");

            this.user.signOut();

            Intent intent = new Intent(UserDetailsActivity.this, SignInOrUpActivity.class);
            startActivity(intent);

        });
    }
}
