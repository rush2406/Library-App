package com.example.rushali.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rushali.library.data.UserContract;
import com.example.rushali.library.data.UserDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        _passwordText.setText("");
        _emailText.setText("");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setLogin();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Sign.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

       // _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        Cursor cursor = null;
        String sql ="SELECT * FROM "+ UserContract.UserEntry.TABLE_NAME+" WHERE email = ?";
        UserDbHelper mDbHelper = new UserDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        cursor= db.rawQuery(sql,new String[]{email});
        int flag=0;
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            if(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL)).equals(email)&&cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)).equals(password))
                flag=1;

        }
        if(email.equals("admin@gmail.com")&&password.equals("admin2406")) {
            final Intent i =new Intent(Login.this,MainActivity.class);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {

                            // On complete call either onLoginSuccess or onLoginFailed
                            startActivity(i);
                            // onLoginFailed();
                            progressDialog.dismiss();


                        }
                    }, 3000);
        }
        else if(flag==1)
        {

            final Intent i = new Intent(Login.this,AccountActivity.class);
            i.putExtra("name",cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME)));
            i.putExtra("userid",cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_UID)));

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            change();
                            startActivity(i);
                            progressDialog.dismiss();


                        }
                    }, 3000);
        }

    else{
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed

                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);

        }
    cursor.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        NavUtils.navigateUpFromSameTask(this);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    void change()
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Login.this).edit();
        editor.putBoolean("logout",false);
        editor.apply();
    }

    void setLogin()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences.getBoolean("logout",true))
        {   change();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setLogin();
    }
}
