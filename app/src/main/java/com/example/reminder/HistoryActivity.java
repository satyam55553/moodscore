package com.example.reminder;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminder.data.Contract;
import com.example.reminder.data.mCursorAdapter;
import static com.example.reminder.data.Contract.tableEntry.*;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    mCursorAdapter mCursorAdapter;
    private static final int SCORE_LOADER = 0;
    ImageButton deleteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        GridView listView = (GridView) findViewById(R.id.list_view_history);

        TextView emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new mCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        deleteHistory=(ImageButton) findViewById(R.id.button_delete_history);
        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAllConfirmationDialog();
            }
        });
        getLoaderManager().initLoader(SCORE_LOADER, null, this);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete all your MoodScore?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteHistory();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteHistory() {
        int rowsDeleted = 0;

        rowsDeleted = getContentResolver().delete(Contract.tableEntry.CONTENT_URI,null,null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "delete history failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "delete history successful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Contract.tableEntry._ID, Contract.tableEntry.COLUMN_DATE,
                Contract.tableEntry.COLUMN_SCORE};
        return new CursorLoader(this, Contract.tableEntry.CONTENT_URI, projection,
                null, null, COLUMN_DATE+" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
