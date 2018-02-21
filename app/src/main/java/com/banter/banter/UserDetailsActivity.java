package com.banter.banter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

import java.util.Arrays;
import java.util.Map;

import static java.lang.System.exit;

public class UserDetailsActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

    private EditText userEmail;
    private Button signOut;
    private Button addAccount;
    private CognitoUser user;

    //TODO: Delete
    private Button doSomething;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        init();
    }

    private void init() {

        //TODO: Delete
        this.doSomething = (Button) findViewById(R.id.button_do_something);
        this.doSomething.setText("DO SOMETHING");
        this.doSomething.setOnClickListener((View v) -> {
            Log.e(TAG, "DO SOMETHING button pressed");
            CognitoUserSession s = AWSCognitoHelper.getCurrSession();
            String u = AWSCognitoHelper.getCognitoUserPool().getCurrentUser().getUserId();
             Log.e(TAG, "TOKEN: er"+s.getAccessToken().getJWTToken());
//            Log.e(TAG, "USERID is: "+u);
//            AWSCognitoHelper.getCognitoUserPool().getCurrentUser().getDetailsInBackground(
//                    new GetDetailsHandler(
//
//                    ) {
//                        @Override
//                        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
//                            Log.e(TAG, "Success");
//                            Log.e(TAG,   "Details: "+Arrays.toString(cognitoUserDetails.getAttributes().getAttributes().entrySet().toArray()));
//                        }
//
//                        @Override
//                        public void onFailure(Exception exception) {
//                            Log.e(TAG, "FAILURE: "+exception);
//
//                        }
//                    }
//            );
        });

        this.user = AWSCognitoHelper.getCognitoUserPool().getCurrentUser();

        this.userEmail = (EditText) findViewById(R.id.text_user_email);
        this.userEmail.setText(this.user.getUserId());

        this.addAccount = (Button) findViewById(R.id.button_add_account);
        this.addAccount.setText(getString(R.string.button_add_account));
        this.addAccount.setOnClickListener((v) -> {
            Log.d(TAG, "Add account button pressed");

            Intent intent = new Intent(this, PlaidAddAccountActivity.class);
            startActivity(intent);
        });

        this.signOut = (Button) findViewById(R.id.button_sign_out);
        this.signOut.setText(R.string.button_sign_out);
        this.signOut.setOnClickListener((v) -> {
            Log.d(TAG, "Sign out button pressed");

            this.user.signOut();

            Intent intent = new Intent(UserDetailsActivity.this, SignInActivity.class);
            startActivity(intent);

        });
    }
}
