package com.example.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminder.data.Contract;
import com.example.reminder.data.mCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import static com.example.reminder.data.Contract.tableEntry.COLUMN_DATE;
import static com.example.reminder.data.Contract.tableEntry.COLUMN_ID;
import static com.example.reminder.data.Contract.tableEntry.COLUMN_SCORE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    mCursorAdapter mCursorAdapter;
    int score = 100;
    TextView moodScore, setReminderDialog, alarmDateTime, viewHistory,tipsText;
    Button sadBtn, greatBtn, setReminder, cancelBtn,decentBtn;
    TimePicker timePicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    AlertDialog alertDialog;
    public static final int NOTIFICATION_ID = 1;
    String currentDate;
    boolean itemPresent = false;
    int moodScoreAvg = 0;
    private static final int SCORE_LOADER = 1, MOOD_SCORE_LOADER = 3;
    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";
    ProgressBar mProgress;
    int pStatus = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        createNotificationChannel();

        moodScore = (TextView) findViewById(R.id.mood_score);
        tipsText = (TextView) findViewById(R.id.tipsText);
        sadBtn = (Button) findViewById(R.id.sad_button);
        decentBtn = (Button) findViewById(R.id.decent_button);
        greatBtn = (Button) findViewById(R.id.great_button);
        viewHistory = findViewById(R.id.viewHistory);

        sadBtn.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        decentBtn.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY);
        greatBtn.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

        setReminderDialog = (TextView) findViewById(R.id.setReminderDialog);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);

        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        setReminderDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopUp();
            }
        });

        getLoaderManager().restartLoader(MOOD_SCORE_LOADER, null, MainActivity.this);

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(i);
            }
        });

        sadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoaderManager().restartLoader(SCORE_LOADER, null, MainActivity.this);
                score = 50;
                currentDate = currentDateFun();

                if (itemPresent) {
                    updateScore(score);
                } else {
                    insertScore(currentDate, score);
                }
                getLoaderManager().restartLoader(MOOD_SCORE_LOADER, null, MainActivity.this);
                mProgress.setProgress(moodScoreAvg);   // Main Progress
            }
        });

        decentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                score = 75;

                currentDate = currentDateFun();
                getLoaderManager().restartLoader(SCORE_LOADER, null, MainActivity.this);
                if (itemPresent) {
                    updateScore(score);
                } else {
                    insertScore(currentDate, score);
                }
                getLoaderManager().restartLoader(MOOD_SCORE_LOADER, null, MainActivity.this);
                mProgress.setProgress(moodScoreAvg);   // Main Progress
            }
        });

        greatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                score = 100;

                currentDate = currentDateFun();
                getLoaderManager().restartLoader(SCORE_LOADER, null, MainActivity.this);
                if (itemPresent) {
                    updateScore(score);
                } else {
                    insertScore(currentDate, score);
                }
                getLoaderManager().restartLoader(MOOD_SCORE_LOADER, null, MainActivity.this);
                mProgress.setProgress(moodScoreAvg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(SCORE_LOADER, null, MainActivity.this);
        getLoaderManager().restartLoader(MOOD_SCORE_LOADER, null, MainActivity.this);

    }

    private void dialogPopUp() {

        Rect rect = new Rect();
        Window window = MainActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dialog_time, viewGroup, false);
        dialogView.setMinimumWidth((int) (rect.width() * 0.9f));
        dialogView.setMinimumHeight((int) (rect.height() * 0.5f));
        builder.setView(dialogView);

        alertDialog = builder.create();

        cancelBtn = (Button) dialogView.findViewById(R.id.cancelBtn);
        setReminder = (Button) dialogView.findViewById(R.id.set_reminder);
        timePicker = dialogView.findViewById(R.id.timePicker);

        setReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to broadcast receiver
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                intent.putExtra("notificationId", NOTIFICATION_ID);
                //PendingIntent
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                //AlarmManager
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                //Create a time
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                long alarmStartTime = calendar.getTimeInMillis();

                alarmDateTime = findViewById(R.id.alarmDateTime);
                String dateTime = "";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa",
                        Locale.getDefault());
                dateTime = simpleDateFormat.format(alarmStartTime);
                alarmDateTime.setText("Reminder was set at " + dateTime);

                //Set Alarm
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(MainActivity.this,
                        "Done!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

//                //Enable receiver explicitly (restart fix)
//                ComponentName receiver = new ComponentName(getApplicationContext(), AlarmReceiver.class);
//                PackageManager pm = getApplicationContext().getPackageManager();
//
//                pm.setComponentEnabledSetting(receiver,
//                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                        PackageManager.DONT_KILL_APP);

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);

//                    ComponentName receiver = new ComponentName(getApplicationContext(), AlarmReceiver.class);
//                    PackageManager pm = getApplicationContext().getPackageManager();
//
//                    pm.setComponentEnabledSetting(receiver,
//                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                            PackageManager.DONT_KILL_APP);

                    alarmDateTime.setText("");
                    Toast.makeText(MainActivity.this,
                            "Cancelled!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "No Reminders Yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    public void insertScore(String mDate, int mScore) {
        ContentValues values = new ContentValues();
        values.put(Contract.tableEntry.COLUMN_DATE, mDate);
        values.put(Contract.tableEntry.COLUMN_SCORE, mScore);
        Uri newUri = getContentResolver().insert(Contract.tableEntry.CONTENT_URI, values);
        Log.e("MainActivity.this", "Score inserted at=" + newUri);
    }

    public void updateScore(int uScore) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COLUMN_SCORE, uScore);

        int rowsUpdated = 0;
        String selection = COLUMN_DATE + "=?";
        String[] selectionArgs = new String[]{currentDateFun()};
        rowsUpdated = getContentResolver().update(Contract.tableEntry.CONTENT_URI,
                updatedValues, selection, selectionArgs);

        if (rowsUpdated == 0) {
            Toast.makeText(this, "Mood update failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mood changed!", Toast.LENGTH_SHORT).show();
        }
    }

    public String currentDateFun() {
        Calendar calendar = Calendar.getInstance();
        long alarmStartTime = calendar.getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());

        return simpleDateFormat.format(alarmStartTime);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == SCORE_LOADER) {
            String[] projection = {Contract.tableEntry._ID, Contract.tableEntry.COLUMN_DATE,
                    Contract.tableEntry.COLUMN_SCORE};
            String selection = COLUMN_DATE + "=?";
            String[] selectionArgs = new String[]{currentDateFun()};
            Log.e("MainActivity.this", "Create loader 1");
            return new CursorLoader(this, Contract.tableEntry.CONTENT_URI, projection,
                    selection, selectionArgs, null);
        } else if (id == MOOD_SCORE_LOADER) {
            String[] projection = {Contract.tableEntry._ID, Contract.tableEntry.COLUMN_DATE,
                    Contract.tableEntry.COLUMN_SCORE};
            String selection = COLUMN_DATE + "=?";
            String[] selectionArgs = new String[]{currentDateFun()};
            Log.e("MainActivity.this", "Create loader 3");
            return new CursorLoader(this, Contract.tableEntry.CONTENT_URI, projection,
                    selection, selectionArgs, COLUMN_DATE + " DESC LIMIT 3");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        if (id == SCORE_LOADER) {
            itemPresent = itemExists(cursor);
            Log.e("MainActivity.this", "item present=" + itemPresent);
        } else if (id == MOOD_SCORE_LOADER) {
            moodScoreAvg = moodScoreCal(cursor);
            mProgress.setProgress(moodScoreAvg);   // Main Progress
            moodScore.setText(moodScoreAvg+"");
            tipsSelector(moodScoreAvg);
            //moodColor(moodScoreAvg);
            Log.e("MainActivity.this", "mood score avg=" + moodScoreAvg);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public boolean itemExists(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            if (currentDateFun().equals(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)))) {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    public int moodScoreCal(Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int moodScoreSum = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
            for (int i = 0; i < cursor.getCount(); i++) {
                if (cursor.moveToNext()) {
                    moodScoreSum = moodScoreSum +
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                }
            }
            moodScoreAvg = moodScoreSum / (cursor.getCount());
            cursor.close();
            return moodScoreAvg;
        }
        return 100;
    }

    /*private void moodColor(int moodValue) {
        if (moodValue >= 80) {
            moodScore.setTextColor(Color.GREEN);
        } else if (moodValue < 80 && moodValue >= 70) {
            moodScore.setTextColor(getResources().getColor(R.color.colorPrimary));
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                Drawable drawable = DrawableCompat.wrap(mProgress.getIndeterminateDrawable());
//                DrawableCompat.setTint(drawable, ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
//                mProgress.setIndeterminateDrawable(DrawableCompat.unwrap(drawable));
//            } else {
//                mProgress.getIndeterminateDrawable()
//                        .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
//                                PorterDuff.Mode.SRC_IN);
//            }
        } else if (moodValue < 70) {
            moodScore.setTextColor(Color.RED);
        }
    }*/

    private void tipsSelector(int score){
        if(score>85){
            tipsText.setText("Do you know? \n" +
                    "Your body requires about 3 litres of water per day \n " +
                    "So stay hydrated!!");
        }else if(score<=85 && score>75){
            tipsText.setText("The younger you are more sleep you require!! \n" +
                    "Adults require atleast 7 hours of sleep \n" +
                    "So lets sleep!!!");
        }else if(score<=75 && score>50){
            tipsText.setText("Focus on eating plenty of fruits and vegetables along with foods rich in omega-3 fatty acids");
        }else if(score<=50){
            tipsText.setText("Hey you seem Sad? \n" +
                    "Cheer up!! \n");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //For API 26 & above
            CharSequence channelName = "Mood Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = "Channel for MoodScore Notification on Android 8+";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    channelName, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}