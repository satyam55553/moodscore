package com.example.reminder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
    public  static final String CONTENT_AUTHORITY="com.example.android.reminder";
    public  static  final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public  static final String PATH_SCORE="score";

    Contract() { }//constructor

    public static final class tableEntry implements BaseColumns {
        public  static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SCORE);
        //MIME Type
        public  static  final  String CONTENT_LIST_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+PATH_SCORE;
        public  static  final  String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+PATH_SCORE;

        public static final String TABLE_NAME = "score";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SCORE = "score";
    }
}
