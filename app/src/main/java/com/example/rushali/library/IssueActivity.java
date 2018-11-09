package com.example.rushali.library;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rushali.library.data.BookContract;
import com.example.rushali.library.data.BookDbHelper;
import com.example.rushali.library.data.UserContract;
import com.example.rushali.library.data.UserDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class IssueActivity extends AppCompatActivity {

    EditText book,student;
    Button issue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        book = (EditText)findViewById(R.id.bookid);
        student = (EditText)findViewById(R.id.stid);
        issue = (Button)findViewById(R.id.issuebook);

        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                issuebook();
            }
        });

    }

    void issuebook()
    {
        String bid = book.getText().toString();
        String uid = student.getText().toString();

        //4 books issued or not

        String w = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE userid = ?";
        UserDbHelper Db = new UserDbHelper(getApplicationContext());
        SQLiteDatabase sqldb = Db.getReadableDatabase();
        Cursor here = sqldb.rawQuery(w, new String[]{uid});
        String branch="";
        if(here!=null&&here.moveToFirst())
        {
            int quant = here.getInt(here.getColumnIndex(UserContract.UserEntry.COLUMN_NUMBER));
            branch = here.getString(here.getColumnIndex(UserContract.UserEntry.COLUMN_DEPT));
            if(quant==4)
            {
                Toast.makeText(getApplicationContext(),"Cannot issue more than 4 books",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        here.close();


        String sql ="SELECT * FROM "+ BookContract.BookEntry.TABLE_NAME+" WHERE bookid = ?";
        BookDbHelper mDbHelper = new BookDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(sql,new String[]{bid});
        if(cursor!=null&& cursor.moveToFirst()) {
            Date cur = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
            String date = dateFormat.format(cur);
            Log.d("date",date);
            String issueids = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESIDS));
            int total = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TOTALQUANT));
            int res = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESQUANT));
            String reserve = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESERVE));
            String tags = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TAGS));
            ArrayList<String> tag = new ArrayList<>(Arrays.asList(tags.split(" ")));
            if(!tag.contains(branch))
            {
                Toast.makeText(getApplicationContext(),"Cannot borrow other branch books",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (total - res ==total) {
                Toast.makeText(getApplicationContext(), "out of stock", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                issueids += uid + " ";
                int stock = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESQUANT));
                stock--;

                int id = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));
                ArrayList<String> list = new ArrayList<>(Arrays.asList(reserve.split(" ")));
                String change = "";
                ContentValues values = new ContentValues();
                if(list.contains(uid))
                {
                    for(int i=0;i<list.size();i++)
                        if(!uid.equals(list.get(i))&&!list.get(i).equals(" "))
                        change+=list.get(i)+" ";
                    Log.d("Entered","changed");
                    values.put(BookContract.BookEntry.COLUMN_RESERVE,change);
                }
                else
                    values.put(BookContract.BookEntry.COLUMN_RESERVE,reserve);

                Uri currentProductUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);


                values.put(BookContract.BookEntry.COLUMN_RESIDS, issueids);
                values.put(BookContract.BookEntry.COLUMN_RESQUANT, stock);



                int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(getApplicationContext(), "Error with updating ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

                cursor.close();

                String sql1 = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE userid = ?";
                UserDbHelper DbHelper = new UserDbHelper(getApplicationContext());
                SQLiteDatabase db1 = DbHelper.getReadableDatabase();
                Cursor c = db1.rawQuery(sql1, new String[]{uid});
                if (c != null && c.moveToFirst()) {
                    String issue = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_ISSUED));
                    String reserved = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_RESERVE));
                    String dateissue = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_IDATE));
                    int took = c.getInt(c.getColumnIndex(UserContract.UserEntry.COLUMN_NUMBER));
                    ArrayList<String> list1 = new ArrayList<>(Arrays.asList(reserved.split(" ")));
                    String newres="";
                    ContentValues val = new ContentValues();
                    if(list1.contains(bid))
                    {
                        for(int i=0;i<list1.size();i++)
                            if(!bid.equals(list1.get(i))&&!list1.get(i).equals(" "))
                            newres+=list1.get(i)+" ";
                        val.put(UserContract.UserEntry.COLUMN_RESERVE,newres);
                    }
                    else
                        val.put(UserContract.UserEntry.COLUMN_RESERVE,reserved);

                    String x = "";
                    if (issue.length() == 0)
                        x += bid + " ";
                    else
                        x += issue + bid + " ";
                    String y="";
                    if(dateissue.equals(" "))
                        y= date +" ";
                    else
                        y+= dateissue+" "+date+" ";

                    Log.d("Printing",y);
                    int ids = c.getInt(c.getColumnIndex(UserContract.UserEntry._ID));
                    Uri currentProduct = ContentUris.withAppendedId(UserContract.UserEntry.CONTENT_URI, ids);


                    val.put(UserContract.UserEntry.COLUMN_ISSUED, x);
                    val.put(UserContract.UserEntry.COLUMN_IDATE,y);
                    val.put(UserContract.UserEntry.COLUMN_NUMBER,took+1);


                    int rowsA = getContentResolver().update(currentProduct, val, null, null);
                    if (rowsA == 0) {
                        Toast.makeText(getApplicationContext(), "Error with updating ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Unknown user", Toast.LENGTH_SHORT).show();
                    finish();
                }
                c.close();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Unknown book", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
