package projekt.dashboard.layers.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;
import projekt.dashboard.layers.R;

/**
 * Created by Nicholas on 2016-03-31.
 */
public class SplashScreenActivity extends Activity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public TextView tv;
    /**
     * Called when the activity is first created.
     */
    Thread splashThread;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // But check permissions first - download will be started in the callback
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // permission already granted, allow the program to continue running
            File directory = new File(Environment.getExternalStorageDirectory(),
                    "/dashboard./");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            cleanTempFolder();
            StartAnimations();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission already granted, allow the program to continue running
                    File directory = new File(Environment.getExternalStorageDirectory(),
                            "/dashboard./");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    cleanTempFolder();
                    StartAnimations();
                } else {
                    // permission was not granted, show closing dialog
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.permission_not_granted_dialog_title)
                            .setMessage(R.string.permission_not_granted_dialog_message)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface
                                    .OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SplashScreenActivity.this.finish();
                                }
                            })
                            .show();
                    return;
                }
                break;
            }
        }
    }

    private void StartAnimations() {
        tv = (TextView) findViewById(R.id.loadingText);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.spin);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.spinnerWheel);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashThread = new Thread() {
            @Override
            public void run() {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext());
                try {
                    sleep(500);
                    if (Shell.SU.available()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.root_verified));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.root_unverified));
                            }
                        });
                    }
                    sleep(1000);
                    if (prefs.getBoolean("first_run", true)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.initial_run));
                            }
                        });
                        sleep(2000);
                        startActivity(new Intent(SplashScreenActivity.this, AppIntroduction.class));
                    } else {
                        if (prefs.getBoolean("advanced_mode_enabled", true)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv.setText(getResources().getString(
                                            R.string.advanced_mode_enabled));
                                }
                            });
                            sleep(1200);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.welcome_back) +
                                        " " + prefs.getString("dashboard_username", getResources().
                                        getString(R.string.welcome_back_default_username)) + "!");
                            }
                        });
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                }
            }
        };
        splashThread.start();

    }

    public void cleanTempFolder() {
        File dir = getApplicationContext().getCacheDir();
        deleteRecursive(dir);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

}