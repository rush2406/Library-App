package com.example.rushali.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.rushali.library.data.BookContract;
import com.example.rushali.library.data.UserContract;
import com.example.rushali.library.data.UserDbHelper;

public class UserAccount extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    TextView name,issue,res,fine,mail;
    String[] proj={
            UserContract.UserEntry._ID,
            UserContract.UserEntry.COLUMN_NAME,
            UserContract.UserEntry.COLUMN_UID,
            UserContract.UserEntry.COLUMN_ISSUED,
            UserContract.UserEntry.COLUMN_DEPT,
            UserContract.UserEntry.COLUMN_FINE,
            UserContract.UserEntry.COLUMN_RESERVE,
            UserContract.UserEntry.COLUMN_EMAIL,
            UserContract.UserEntry.COLUMN_IDATE,
            UserContract.UserEntry.COLUMN_NUMBER
    };
    public static final int LOADER_ID = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

         name = (TextView) findViewById(R.id.name);
       mail = (TextView)findViewById(R.id.mail);
       issue = (TextView)findViewById(R.id.issue);
       res = (TextView)findViewById(R.id.res);
       fine = (TextView)findViewById(R.id.fine);

       /* String sql1 = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE userid = ?";
        UserDbHelper DbHelper = new UserDbHelper(getApplicationContext());
        SQLiteDatabase db1 = DbHelper.getReadableDatabase();
        Cursor c = db1.rawQuery(sql1, new String[]{AccountActivity.uid});*/
        getSupportLoaderManager().initLoader(LOADER_ID,null,this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] sel = {AccountActivity.uid};
        return new CursorLoader(this,
                UserContract.UserEntry.CONTENT_URI,
                proj,
                "userid=?",
                sel,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor c) {

        c.moveToFirst();
        String fname = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_UID));
        String email =c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL));
        String issued = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_ISSUED));
        String reserve = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_RESERVE));
        int fin = c.getInt(c.getColumnIndex(UserContract.UserEntry.COLUMN_FINE));

        name.setText(fname);
        mail.setText(email);
        issue.setText(issued);
        fine.setText(String.valueOf(fin));
        res.setText(reserve);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
