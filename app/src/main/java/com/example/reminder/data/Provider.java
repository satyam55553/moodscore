package com.example.reminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.reminder.data.Contract.tableEntry.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Provider extends ContentProvider {
    public static final String LOG_TAG = Provider.class.getSimpleName();
    dbHelper dbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SCORE = 100;//whole table
    private static final int SCORE_ID = 101;//particular item in the table

    // Static initializer. This runs the first time anything is called from this class.
    static {
        // add content uri to uri matcher
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_SCORE, SCORE);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_SCORE + "/#", SCORE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new dbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                cursor = database.query(Contract.tableEntry.TABLE_NAME, projection, null, null,
                        null, null, sortOrder);
                break;
            case SCORE_ID:
                selection = Contract.tableEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(Contract.tableEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //data at uri changes,then cursor is notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                return Contract.tableEntry.CONTENT_LIST_TYPE;
            case SCORE_ID:
                return Contract.tableEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                long id = database.insert(Contract.tableEntry.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }
                //notify the content resolver that data has changed for 'uri'
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SCORE:
                rowsDeleted = database.delete(Contract.tableEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    //notify the content resolver that data has changed for 'uri'
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case SCORE_ID:
                selection = Contract.tableEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.tableEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    //notify the content resolver that data has changed for 'uri'
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            default:
                throw new IllegalArgumentException("Deleting is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                return updateScore(uri, contentValues, selection, selectionArgs);
            case SCORE_ID:
                selection = Contract.tableEntry.COLUMN_ID + "=?";
                //Parsing row id from content uri
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateScore(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Updating is not supported for " + uri);
        }
    }

    private int updateScore(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsUpdated = database.update(Contract.tableEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            //notify the content resolver that data has changed for 'uri'
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public String currentDateFun() {
        Calendar calendar = Calendar.getInstance();
        long alarmStartTime = calendar.getTimeInMillis();
        String dateTime = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        return dateTime = simpleDateFormat.format(alarmStartTime);
    }
}
