package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.sip.SipSession;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUserName, edtPassword,edtDOrP;
    private Button btnSignUpLogIn,btnOneTimeLogIn;
    private RadioButton rdbPassinger, rdbDriver;

    enum State {
        SIGNUP,LOGIN}
    private State state;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtDOrP = findViewById(R.id.edtDOrP);

        btnOneTimeLogIn = findViewById(R.id.btnOneTimeLogIn);
        btnSignUpLogIn = findViewById(R.id.btnSignUpLogIn);

        rdbDriver = findViewById(R.id.rdbDriver);
        rdbPassinger = findViewById(R.id.rdbPassinger);

        btnOneTimeLogIn.setOnClickListener(this);

        state = State.SIGNUP;


        if (ParseUser.getCurrentUser() != null) {

        }

        btnSignUpLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == State.SIGNUP) {
                    if (rdbDriver.isChecked() == false && rdbPassinger.isChecked() == false) {
                        FancyToast.makeText(MainActivity.this,
                                "Are you driver or passinger?", Toast.LENGTH_SHORT, FancyToast.INFO,
                                true).show();
                        return;
                    }
                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUserName.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());

                    if (rdbDriver.isChecked()) {
                        appUser.put("as", "Driver");
                    } else if (rdbPassinger.isChecked()) {
                        appUser.put("as", "Passinger");
                    }
                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                FancyToast.makeText(MainActivity.this, "Signed Up",
                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            }
                        }
                    });


                } else if (state == State.LOGIN) {

                    ParseUser.logInInBackground(edtUserName.getText().toString(),
                            edtPassword.getText().toString(),
                            new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                FancyToast.makeText(MainActivity.this,
                                        "User Logged in",Toast.LENGTH_SHORT,FancyToast.INFO,
                                        true).show();
                            }

                        }
                    });
                }
            }

        });
    }


    @Override
    public void onClick(View view) {
        if (edtDOrP.getText().toString().equals("Driver") || edtDOrP.getText().toString().equals("Passinger")) {
            if ( ParseUser.getCurrentUser() == null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if ( user != null && e == null) {
                            FancyToast.makeText(MainActivity.this,
                                    "we have Anonymous User",
                                    Toast.LENGTH_SHORT,FancyToast.INFO,true).show();

                            user.put("as",edtDOrP.getText().toString());

                            user.saveInBackground();

                        }
                    }
                });
            }
        }


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.LoginItem:
                if ( state == State.SIGNUP) {
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUpLogIn.setText("Log In");
                }else if (state == State.LOGIN){
                    state = State.SIGNUP;
                    item.setTitle("Log In");
                    btnSignUpLogIn.setText("Sign Up");
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
