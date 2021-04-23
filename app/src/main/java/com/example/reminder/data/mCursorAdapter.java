package com.example.reminder.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.reminder.R;

public class mCursorAdapter extends CursorAdapter {
    TextView listScore;

    public mCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        listScore = (TextView) view.findViewById(R.id.list_score);
        TextView listDate = (TextView) view.findViewById(R.id.list_date);

        String date = cursor.getString(cursor.getColumnIndexOrThrow(Contract.tableEntry.COLUMN_DATE));
        String score = cursor.getString(cursor.getColumnIndexOrThrow(Contract.tableEntry.COLUMN_SCORE));

        listDate.setText(date);
        listScore.setText(score);
        moodColor(Integer.parseInt(score));

    }

    private void moodColor(int moodValue) {
        if (moodValue >= 80) {
            listScore.setTextColor(Color.GREEN);
        } else if (moodValue < 80 && moodValue >= 70) {
            listScore.setTextColor((Color.parseColor("#6200EE")));
        } else if (moodValue < 70) {
            listScore.setTextColor(Color.RED);
        }
    }
}
