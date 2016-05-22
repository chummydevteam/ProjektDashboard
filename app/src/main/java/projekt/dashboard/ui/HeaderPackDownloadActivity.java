package projekt.dashboard.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import projekt.dashboard.R;
import projekt.dashboard.adapters.DataAdapter;
import projekt.dashboard.util.HeaderParser;
import projekt.dashboard.util.ReadCloudXMLFile;

/**
 * Created by Nicholas on 2016-03-31.
 */
public class HeaderPackDownloadActivity extends AppCompatActivity {

    public String current_source_pack;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public Boolean is_dialog_open = false;
    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    private ArrayList<String> headerNames, headerPreviews;
    private String[] headerNamesArray, headerPreviewsArray;

    void DeleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.abandon_dashboard_work_area:
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/" + getString(R.string.dashboard_header_directory) + "/");
                DeleteRecursive(f);
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.download_menu, menu);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloader_activity);

        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        mProgressDialog = new ProgressDialog(HeaderPackDownloadActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.downloader_dialog_one));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        final String[] headerPackSources = getResources().getStringArray(R.array.header_pack_urls);
        final Spinner headerPackSourcePicker = (Spinner) findViewById(R.id.sourcePickerHeaderPacks);
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.header_pack_sources));
        headerPackSourcePicker.setAdapter(spinnerCountShoesArrayAdapter);
        headerPackSourcePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ImageButton restartActivity = (ImageButton) findViewById(R.id.restartDownloadSources);
        if (restartActivity != null) {
            restartActivity.setOnClickListener((new View.OnClickListener() {
                public void onClick(View v) {
                    if (!current_source_pack.equals(headerPackSources[headerPackSourcePicker.getSelectedItemPosition()])) {
                        current_source_pack = headerPackSources[headerPackSourcePicker.getSelectedItemPosition()];
                        downloadResources downloadTask = new downloadResources();
                        downloadTask.execute(
                                headerPackSources[headerPackSourcePicker.getSelectedItemPosition()],
                                "addons.xml");
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_item);
                        TextView noDownloadsAvailable = (TextView) findViewById(R.id.NoDownloadsAvailable);
                        noDownloadsAvailable.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("DownloadActivity", "There is no need to restart the activity's sources!");
                    }
                }
            }));
        }

        current_source_pack = headerPackSources[headerPackSourcePicker.getSelectedItemPosition()];
        downloadResources downloadTask = new downloadResources();
        downloadTask.execute(
                headerPackSources[headerPackSourcePicker.getSelectedItemPosition()],
                "addons.xml");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerView = (RecyclerView) findViewById(R.id.list_item);
        recyclerView.setHasFixedSize(true);
        if (recyclerView.getAdapter() != null) {
            Log.d("refreshLayout", "The RecyclerView is not null, setting it to null...");
            recyclerView.setAdapter(null);
        }
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private ArrayList<HeaderParser> prepareData() {

        ArrayList<HeaderParser> headers = new ArrayList<>();
        for (int i = 0; i < headerNamesArray.length; i++) {
            HeaderParser headerParser = new HeaderParser();
            headerParser.setHeaderPackName(headerNamesArray[i]);
            headerParser.setHeaderPackURL(headerPreviewsArray[i]);
            headers.add(headerParser);
        }
        return headers;
    }

    public void refreshLayout() {
        int counter = 0;

        if (recyclerView.getAdapter() != null) {
            Log.d("refreshLayout", "The RecyclerView is not null, setting it to null...");
            recyclerView.setAdapter(null);
        }

        // Defined Array values to show in ListView
        headerNames = new ArrayList<>();
        headerPreviews = new ArrayList<>();

        String[] checkerCommands = {getApplicationContext().getFilesDir() + "/addons.xml"};
        final Map<String, String> newArray = ReadCloudXMLFile.main(checkerCommands);
        for (String key : newArray.keySet()) {
            if (!key.toLowerCase().contains("-preview".toLowerCase()))
                System.out.println("Loading Header Pack : " + key);

            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/" + getString(R.string.dashboard_header_directory) + "/" + key + ".zip");
            if (!f.exists()) {
                if (key.toLowerCase().contains("-preview".toLowerCase())) {
                    String checker = key.substring(0, key.length() - 8);

                    // Unlike the previous implementation, we have to filter out already installed
                    // header packs including their preview images.

                    Boolean checkIfExist = new File(Environment.getExternalStorageDirectory().
                            getAbsolutePath() +
                            "/" + getString(R.string.dashboard_header_directory) + "/" + checker + ".zip").exists();
                    if (!checkIfExist) {
                        headerPreviews.add(newArray.get(key));
                    }
                } else {
                    headerNames.add(key);
                    counter += 1;
                }
            } else {
                System.out.println("Header Pack Located : " + f.getAbsolutePath());
            }
        }
        if (counter == 0) {
            TextView noDownloadsAvailable = (TextView) findViewById(R.id.NoDownloadsAvailable);
            if (noDownloadsAvailable != null) {
                noDownloadsAvailable.setVisibility(View.VISIBLE);
            }
            recyclerView.setVisibility(View.GONE);
        }

        headerNamesArray = headerNames.toArray(new String[headerNames.size()]);
        Arrays.sort(headerNamesArray);
        headerPreviewsArray = headerPreviews.toArray(new String[headerPreviews.size()]);

        ArrayList<HeaderParser> headerParsers = prepareData();
        DataAdapter adapter = new DataAdapter(getApplicationContext(), headerParsers);
        // Assign adapter to RecyclerView
        recyclerView.setAdapter(adapter);
        // RecyclerView Item Click Listener
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                    });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    // RecyclerView Clicked item value
                    try {
                        int position = rv.getChildAdapterPosition(child);
                        final String itemValue = headerNames.get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                HeaderPackDownloadActivity.this);
                        builder.setPositiveButton(getResources().getString(
                                R.string.downloader_dialog_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // execute this when the downloader must be fired
                                final downloadResources downloadTask = new downloadResources();
                                downloadTask.execute(newArray.get(itemValue), itemValue);
                                is_dialog_open = false;
                            }
                        }).setNegativeButton(
                                getResources().getString(R.string.downloader_dialog_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        is_dialog_open = false;
                                    }
                                });
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.header_preview_dialog, null);
                        ImageView i = (ImageView) dialogLayout.findViewById(R.id.dialogImage);
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
                                newArray.get(itemValue + "-preview")).getContent());
                        i.setImageBitmap(bitmap);
                        final AlertDialog dialog = builder.create();
                        dialog.setView(dialogLayout);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        if (!dialog.isShowing() && !is_dialog_open) {
                            dialog.show();
                            is_dialog_open = true;
                            dialog.getButton(dialog.BUTTON_NEGATIVE).
                                    setTextColor(getColor(android.R.color.white));
                            dialog.getButton(dialog.BUTTON_POSITIVE).
                                    setTextColor(getColor(android.R.color.white));
                        }
                    } catch (MalformedURLException mue) {
                    } catch (IOException ioe) {
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mProgressDialog.setMessage(getResources().getString(R.string.downloader_dialog_two));
    }

    private class downloadResources extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager)
                    getApplicationContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            mWakeLock.release();
            mProgressDialog.dismiss();

            refreshLayout();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            File myDir = new File(Environment.getExternalStorageDirectory(), getString(R.string.dashboard_header_directory));
            if (!myDir.exists()) {
                myDir.mkdir();
            }

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                if (sUrl[1].equals("addons.xml")) {
                    output = new FileOutputStream(
                            getApplicationContext().getFilesDir().getAbsolutePath() +
                                    "/" + sUrl[1]);
                } else {
                    output = new FileOutputStream(
                            Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    "/" + getString(R.string.dashboard_header_directory) + "/" + sUrl[1] + ".zip");
                }
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}


