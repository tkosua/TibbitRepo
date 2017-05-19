package com.example.theopsyphertxt.tibbit;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    protected TextView  mSignUpTV;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //calls the setSupportActionBar() method using Toolbar widget
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //hides action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

         mSignUpTV = (TextView) findViewById(R.id.signUpText);
         mSignUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentS = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intentS);
            }
        });

        mUsername = (EditText)findViewById(R.id.usernameID);
        mPassword = (EditText)findViewById(R.id.passwordID);
        mLogInButton = (Button) findViewById(R.id.loginButton);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    //LogIn in background
                    ParseUser.logInInBackground (username, password, new LogInCallback () {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                //Success!
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage ())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    });
                }
          }

        });
    }
}
