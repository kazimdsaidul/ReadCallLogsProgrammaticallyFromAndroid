package saidul.com.readcalllogsprogrammaticallyfromandroid;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "CallLog";
    private static final int URL_LOADER = 1;

    private TextView callLogsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);
        initialize();
        findViewById(R.id.progressBar).setVisibility(View.GONE);



    }


    private void initialize() {
        Log.d(TAG, "initialize()");

        Button btnCallLog = (Button) findViewById(R.id.btn_call_log);

        btnCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "initialize() >> initialise loader");

                getSupportLoaderManager().initLoader(URL_LOADER, null, MainActivity.this);


            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        Log.d(TAG, "onCreateLoader() >> loaderID : " + loaderID);

        switch (loaderID) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        CallLog.Calls.CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, Cursor managedCursor) {

          new MyTask(loader,managedCursor).execute();






//        callLogsTextView.setText("test");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        // do nothing
    }
    public class MyTask  extends AsyncTask<Void, Void, String> {

        private Loader<Cursor> mLoader;
        private Cursor mManagedCursor;

        MyTask(Loader<Cursor> loader, Cursor managedCursor){
            mLoader = loader;
            mManagedCursor = managedCursor;
            Toast.makeText(getApplicationContext(),"start", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            Log.d(TAG, "onLoadFinished()");

            StringBuilder sb = new StringBuilder();

            int number = mManagedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = mManagedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = mManagedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = mManagedCursor.getColumnIndex(CallLog.Calls.DURATION);

            sb.append("Call Log Details");
            sb.append("\n");
            sb.append("\n");


            while (mManagedCursor.moveToNext()) {
                String phNumber = mManagedCursor.getString(number);
                String callType = mManagedCursor.getString(type);
                String callDate = mManagedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = mManagedCursor.getString(duration);
                String dir = null;

                int callTypeCode = Integer.parseInt(callType);
                switch (callTypeCode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "Outgoing";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "Incoming";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "Missed";
                        break;
                }

                sb.append("\n")
                        .append("Phone Number:")
                        .append(phNumber);
                sb.append("\n")
                        .append("Call Type:")
                        .append(callType);
                sb.append("\n")
                        .append("Call Duration:")
                        .append(callDuration);
                sb.append("\n");

            }

            Log.e("log data", ""+sb);
            mManagedCursor.close();


            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Finished", Toast.LENGTH_LONG).show();

            mManagedCursor.close();

            callLogsTextView = (TextView) findViewById(R.id.textView);
            callLogsTextView.setText(s.toString());

            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
