package com.example.rushali.library;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rushali.library.data.BookContract;
import com.example.rushali.library.data.UserContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Sign extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_regno)
    EditText _regnoText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    @BindView(R.id.input_branch)
    EditText branch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Sign.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String regno = _regnoText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String dept = branch.getText().toString();

        String x="";
        // TODO: Implement your own signup logic here.
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME,name);
        values.put(UserContract.UserEntry.COLUMN_EMAIL,email);
        values.put(UserContract.UserEntry.COLUMN_DEPT,dept);
        values.put(UserContract.UserEntry.COLUMN_FINE,0);
        values.put(UserContract.UserEntry.COLUMN_UID,regno);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD,password);
        values.put(UserContract.UserEntry.COLUMN_ISSUED,x);
        values.put(UserContract.UserEntry.COLUMN_RESERVE,x);
        values.put(UserContract.UserEntry.COLUMN_IDATE," ");
        values.put(UserContract.UserEntry.COLUMN_NUMBER,0);


        Uri uri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI,values);
        Log.d("URi = ",uri.toString());
        if (uri == null) {
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(getApplicationContext(),"Sign in now!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Sign.this,Login.class);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String regno = _regnoText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String dept = branch.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (regno.isEmpty()) {
            _regnoText.setError("Enter reg no.");
            valid = false;
        } else {
            _regnoText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

       if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Passwords do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
        ArrayList<String> list=new ArrayList<>();
        list.add("CSE");
        list.add("ECE");
        list.add("Chemical");
        list.add("Mech");
        list.add("Civil");
        list.add("Metallurgy");
        list.add("BioTech");
        list.add("EEE");
        if(!list.contains(dept))
            valid=false;
        else
            branch.setError(null);

        return valid;
    }
}
