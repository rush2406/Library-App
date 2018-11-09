package com.example.rushali.library.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by rushali on 11/10/18.
 */

public class BookProvider extends ContentProvider {

    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final int USERS = 200;
    private static final int USER_ID = 201;
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH, BOOKS);
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH + "/#", BOOK_ID);
        uriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH, USERS);
        uriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH + "/#", USER_ID);
        return uriMatcher;
    }

    private BookDbHelper mDbHelper;
    private UserDbHelper mDbHelper1;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        mDbHelper1 = new UserDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase db1 = mDbHelper1.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                long id = db.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);
                if (id < 0) {
                    Toast.makeText(getContext(), "Insert Failed", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Insert failed");
                } else
                       getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
            case USERS:
                long id1 = db1.insert(UserContract.UserEntry.TABLE_NAME, null, contentValues);
                if (id1 < 0) {
                    Toast.makeText(getContext(), "Insert Failed", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Insert failed");
                } else
                    getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(UserContract.UserEntry.CONTENT_URI, id1);
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteDatabase db1 = mDbHelper1.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor c;
        switch (match) {
            case BOOKS:
                c = db.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = db.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case USERS:
                if(selectionArgs==null||selectionArgs.length==0)
                c = db1.query(UserContract.UserEntry.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                else
                    c = db1.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = db1.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs,null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);
        }
        if(c!=null)
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case USERS:
                return updateInventory1(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory1(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }

    }

    private int updateInventory1(Uri uri,ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper1.getWritableDatabase();
        int rowsUpdated = database.update(UserContract.UserEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateInventory(Uri uri,ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase db1 = mDbHelper1.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rows;
        switch (match) {
            case BOOKS:
                int rowsDeleted = db.delete(BookContract.BookEntry.TABLE_NAME, s, strings);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case BOOK_ID:
                s = BookContract.BookEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(BookContract.BookEntry.TABLE_NAME, s, strings);
                break;
            case USERS:
                int rowsDeleted1 = db1.delete(UserContract.UserEntry.TABLE_NAME, s, strings);
                if (rowsDeleted1 != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted1;
            case USER_ID:
                s = UserContract.UserEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db1.delete(UserContract.UserEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri = " + uri);
        }
        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.CONTENT_ITEM_TYPE;
            case USERS:
                return UserContract.CONTENT_LIST_TYPE;
            case USER_ID:
                return UserContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}
