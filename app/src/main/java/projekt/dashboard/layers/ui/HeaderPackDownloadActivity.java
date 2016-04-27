package projekt.dashboard.layers.ui;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projekt.dashboard.layers.R;
import projekt.dashboard.layers.util.ReadCloudXMLFile;

/**
 * Created by Nicholas on 2016-03-31.
 */
public class HeaderPackDownloadActivity extends AppCompatActivity {

    public boolean has_downloaded_anything = false;
    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;

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
                if (has_downloaded_anything) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                this.finish();
                return true;
            case R.id.abandon_dashboard_work_area:
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/dashboard./");
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
        if (has_downloaded_anything) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
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
        setContentView(R.layout.downloader);

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

        downloadResources downloadTask = new downloadResources();
        downloadTask.execute(
                "http://pastebin.com/raw/eCmQhEwM",
                "addons.xml");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void refreshLayout() {
        int counter = 0;

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.list_item);

        // Defined Array values to show in ListView
        List<String> values = new ArrayList<String>();

        String[] checkerCommands = {getApplicationContext().getFilesDir() + "/addons.xml"};
        final Map<String, String> newArray = ReadCloudXMLFile.main(checkerCommands);
        for (String key : newArray.keySet()) {
            System.out.println("key : " + key);
            System.out.println("value : " + newArray.get(key));

            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/dashboard./" + key + ".zip");
            if (!f.exists()) {
                if (key.toLowerCase().contains("-preview".toLowerCase())) {
                    System.out.println("Preview file detected, ignoring it from the TreeMap");
                } else {
                    values.add(key);
                    counter += 1;
                }
            }
        }

        if (counter == 0) {
            TextView noDownloadsAvailable = (TextView) findViewById(R.id.NoDownloadsAvailable);
            noDownloadsAvailable.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.list_row, R.id.title, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        // ListView Item Click Listener
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {
                        // ListView Clicked item value
                        final String itemValue = (String) listView.getItemAtPosition(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                HeaderPackDownloadActivity.this);
                        builder.setPositiveButton(getResources().getString(
                                R.string.downloader_dialog_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // execute this when the downloader must be fired
                                final downloadResources downloadTask = new downloadResources();
                                downloadTask.execute(newArray.get(itemValue), itemValue);
                            }
                        }).setNegativeButton(
                                getResources().getString(R.string.downloader_dialog_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.header_preview_dialog, null);
                        dialog.setView(dialogLayout);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        try {
                            ImageView i = (ImageView) dialogLayout.findViewById(R.id.dialogImage);
                            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
                                    newArray.get(itemValue + "-preview")).getContent());
                            i.setImageBitmap(bitmap);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.setCancelable(false);
                        dialog.show();
                        dialog.getButton(dialog.BUTTON_NEGATIVE).
                                setTextColor(getResources().getColor(android.R.color.white));
                        dialog.getButton(dialog.BUTTON_POSITIVE).
                                setTextColor(getResources().getColor(android.R.color.white));
                    }


                }

        );
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
                                    "/dashboard./" + sUrl[1] + ".zip");
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

                if (!sUrl[1].equals("addons.xml")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(
                                    HeaderPackDownloadActivity.this.getApplicationContext(),
                                    getResources().getString(R.string.downloader_toast_exit),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    has_downloaded_anything = true;
                }
            }
            return null;
        }
    }
}


