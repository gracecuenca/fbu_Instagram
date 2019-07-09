package com.example.fbu_instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    // the attributes in the login file (activity_main)
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // the activity_main.xml holds the log in screen

        // enabling user persistence
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) { // user has already logged in
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {

        }

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                login(username, password);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                signUp(username, password);
            }
        });
    }

    // TODO -- do sign up and login have to be separate or can we channel them into one common thread
    private void signUp(String username, String password){
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Sign up successful");
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Sign up failed");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Account with this username already exists", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void login(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null){ // if there are no errors
                    Log.d("LoginActivity", "Login successful");
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // dont want the user to log out with a simple back press
                }else{
                    Log.e("LoginActivity", "Login failed");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
