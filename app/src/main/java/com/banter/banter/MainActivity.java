package com.banter.banter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button signIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        signUp = (Button) findViewById(R.id.signUp);
        signUp.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }


//    public void SignInButtonPressed(View view) {
//        Intent intent = new Intent(this, SignInActivity.class);
//        startActivity(intent);
//    }

//    public void SignUpButtonPressed(View view) {
//        Intent intent = new Intent(this, SignUpActivity.class);
//        startActivity(intent);
//    }
}
