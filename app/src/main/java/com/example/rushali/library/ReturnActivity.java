package com.example.rushali.library;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ReturnActivity extends AppCompatActivity {

    EditText book, student;
    Button returns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        book = (EditText) findViewById(R.id.bookid);
        student = (EditText) findViewById(R.id.stid);
        returns = (Button) findViewById(R.id.retbook);

        returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                returnbook();
            }
        });
    }

    void returnbook()
    {

        String bid = book.getText().toString();
        String uid = student.getText().toString();
        String name="";

        String sql ="SELECT * FROM "+ BookContract.BookEntry.TABLE_NAME+" WHERE bookid = ?";
        BookDbHelper mDbHelper = new BookDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(sql,new String[]{bid});
        ArrayList<String> emails = new ArrayList<>();
        if(cursor!=null&&cursor.moveToFirst()) {

            String issueids = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESIDS));
            String reserve = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESERVE));
            String newidlist = "";
            ArrayList<String> list = new ArrayList<>(Arrays.asList(issueids.split(" ")));
            ArrayList<String> list2 = new ArrayList<>(Arrays.asList(reserve.split(" ")));
            if (!list.contains(uid)) {
                Toast.makeText(getApplicationContext(), uid + " did not borrow book " + bid, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                int flag=0;
                Log.d("size=",String.valueOf(list2.size()));
                for (int i = 0; i < list.size(); i++) {

                    if (!list.get(i).equals(uid)||flag==1) {
                        newidlist += list.get(i) + " ";
                    }
                    else if(list.get(i).equals(uid))
                        flag=1;
                }
                for(int i=0;i<list2.size();i++)
                {
                    Log.d("reserve","hello"+list2.get(i));
                    String sql1 = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE userid = ?";
                    UserDbHelper DbHelper = new UserDbHelper(getApplicationContext());
                    SQLiteDatabase db1 = DbHelper.getReadableDatabase();
                    String reg = list2.get(i);
                    Cursor c = db1.rawQuery(sql1, new String[]{reg});
                    if (c != null && c.moveToFirst()) {
                        String email = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_EMAIL));
                        emails.add(email);
                    }
                }
                int stock = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RESQUANT));
                stock++;

                int id = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));
               name = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME));
                Uri currentProductUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);

                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_RESIDS, newidlist);
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
                String retdate="";
                UserDbHelper DbHelper = new UserDbHelper(getApplicationContext());
                SQLiteDatabase db1 = DbHelper.getReadableDatabase();
                Cursor c = db1.rawQuery(sql1, new String[]{uid});
                if (c != null && c.moveToFirst()) {

                    String issue = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_ISSUED));
                    int took = c.getInt(c.getColumnIndex(UserContract.UserEntry.COLUMN_NUMBER));
                    int sum = c.getInt(c.getColumnIndex(UserContract.UserEntry.COLUMN_FINE));
                    ArrayList<String> list1 = new ArrayList<>(Arrays.asList(issue.split(" ")));
                    String x = " ";
                    int index=0;
                    String dateissue = c.getString(c.getColumnIndex(UserContract.UserEntry.COLUMN_IDATE));
                    int flag1=0;
                    ArrayList<String> list3 = new ArrayList<>(Arrays.asList(dateissue.split(" ")));

                    for (int i = 0; i < list1.size(); i++) {
                        if ((!bid.equals(list1.get(i))&&!list1.get(i).equals(" "))||flag1==1)
                            x += list1.get(i) + " ";
                        else
                            flag1=1;
                    }

                    flag=0;
                   // Log.d("retdate","hi"+retdate);
                    //date string
                    ArrayList<String> list4=new ArrayList<>();
                    for(int i=0;i<list3.size();i++) {
                        if(list3.get(i).length()>0&&list3.get(i).charAt(0)!=' ')
                        list4.add(list3.get(i));
                    }

                    //book id
                    ArrayList<String> list5=new ArrayList<>();
                    for(int i=0;i<list1.size();i++)
                        if(list1.get(i).length()>0&&list1.get(i).charAt(0)!=' ')
                            list5.add(list1.get(i));

                    for(int i=0;i<list5.size();i++)
                    {
                        if(list5.get(i).equals(bid))
                        {
                            index=i;
                            Log.d("found","yes");
                            break;
                        }
                    }

                    retdate=list4.get(index);

                    for(int i=0;i<list4.size();i++)
                        Log.d("List4","hi"+list4.get(i));

                    String y="";
                    for(int i=0;i<list3.size();i++)
                    {
                        if((!retdate.equals(list3.get(i))&&!list3.get(i).equals(" "))||flag==1)
                            y+=list3.get(i)+" ";
                        else
                        {
                            flag=1;
                        }
                    }
           /* if(issue.length()==0)
                x+= bid + " ";
            else
                x+=issue+bid+" ";*/

                    int ids = c.getInt(c.getColumnIndex(UserContract.UserEntry._ID));
                    Uri currentProduct = ContentUris.withAppendedId(UserContract.UserEntry.CONTENT_URI, ids);
                    int fine = calcFine(retdate);
                    //Log.d("got",retdate);

                    ContentValues val = new ContentValues();
                    val.put(UserContract.UserEntry.COLUMN_ISSUED, x);
                    val.put(UserContract.UserEntry.COLUMN_IDATE,y);
                    val.put(UserContract.UserEntry.COLUMN_FINE,sum+fine);
                    val.put(UserContract.UserEntry.COLUMN_NUMBER,took-1);

                    int rowsA = getContentResolver().update(currentProduct, val, null, null);
                    if (rowsA == 0) {
                        Toast.makeText(getApplicationContext(), "Error with updating ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    if(emails.size()>0) {
                        String[] to = new String[emails.size()];
                        to = emails.toArray(to);
                        Log.d("emails", String.valueOf(emails.size()));
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
                        intent.putExtra(Intent.EXTRA_SUBJECT, name + " is available now. Hurry up!");
                        intent.putExtra(Intent.EXTRA_TEXT, (Serializable) new StringBuilder());
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
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
    int calcFine(String ret)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        Log.d("ret",ret);

        try {
            Date date1 = simpleDateFormat.parse(ret);
            Log.d("return",ret);
            Date date2 = simpleDateFormat.parse("7/11/2018");

            long x=printDifference(date1,date2);
            return (int)x;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    long printDifference(Date startDate,Date endDate)
    {
        /*Date d1=null,d2=null,d3=null,endDate,retdate=null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        try {
            d1 = simpleDateFormat.parse("10/11/2018");
            d2 = simpleDateFormat.parse("25/11/2018");
            d3 = simpleDateFormat.parse("30/12/2018");

        }catch (ParseException p)
        {

        }

        Date d[]={d1,d2,d3};
        int i;
        //finding nearest ending date
        for(i=0;i<3;i++)
        {
            if(startDate.compareTo(d[i])<=0)
                break;
        }
        endDate = d[i];*/
        //getting current time
        Date retdate=null;
        Date cur = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
        try {
            retdate = dateFormat.parse(dateFormat.format(cur));
        }catch (ParseException p)
        {

        }
        long different=0;
        if(retdate!=null)
        different = endDate.getTime() - retdate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        //different = different % daysInMilli;

       /* long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;*/

        Log.d("difference",String.valueOf(elapsedDays));
        if(elapsedDays<0)
        return -1*elapsedDays;
        return 0;
    }
}
