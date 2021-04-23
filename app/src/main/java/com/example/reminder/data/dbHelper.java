package com.example.reminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.reminder.data.Contract.tableEntry.*;

import androidx.annotation.Nullable;

public class dbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "moodscore.db";
    public static final int DATABASE_VERSION = 1;
    public dbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_SCORE_TABLE=
                "CREATE TABLE "+TABLE_NAME+"("
                        +COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_DATE+" TEXT NOT NULL,"
                        +COLUMN_SCORE+" INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
