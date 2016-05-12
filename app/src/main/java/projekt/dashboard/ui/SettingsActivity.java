package projekt.dashboard.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

import projekt.dashboard.R;

/**
 * Created by Nicholas on 2016-03-31.
 */
public class SettingsActivity extends AppCompatActivity {

    public boolean has_modified_anything = false;

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
                if (has_modified_anything) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (has_modified_anything) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_activity);

        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        Switch color_switcher = (Switch) findViewById(R.id.enable_color_switcher);
        if (prefs.getBoolean("color_switcher_enabled", true)) {
            color_switcher.setChecked(true);
        } else {
            color_switcher.setChecked(false);
        }
        color_switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("color_switcher_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("color_switcher_enabled", false).commit();
                }
            }
        });

        Switch header_swapper = (Switch) findViewById(R.id.enable_header_swapper);
        if (prefs.getBoolean("header_swapper_enabled", true)) {
            header_swapper.setChecked(true);
        } else {
            header_swapper.setChecked(false);
        }
        header_swapper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("header_swapper_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("header_swapper_enabled", false).commit();
                }
            }
        });

        Switch header_importer = (Switch) findViewById(R.id.enable_header_importer);
        if (prefs.getBoolean("header_importer_enabled", true)) {
            header_importer.setChecked(true);
        } else {
            header_importer.setChecked(false);
        }
        header_importer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("header_importer_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("header_importer_enabled", false).commit();
                }
            }
        });

        Switch theme_debugger = (Switch) findViewById(R.id.enable_theme_utilities);
        if (prefs.getBoolean("theme_debugging_enabled", true)) {
            theme_debugger.setChecked(true);
        } else {
            theme_debugger.setChecked(false);
        }
        theme_debugger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("theme_debugging_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("theme_debugging_enabled", false).commit();
                }
            }
        });

        Switch wallpapers = (Switch) findViewById(R.id.enable_wallpapers);
        if (prefs.getBoolean("wallpapers_enabled", true)) {
            wallpapers.setChecked(true);
        } else {
            wallpapers.setChecked(false);
        }
        wallpapers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("theme_debugging_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("theme_debugging_enabled", false).commit();
                }
            }
        });

        Switch blacked_out = (Switch) findViewById(R.id.enable_blacked_out);
        if (prefs.getBoolean("blacked_out_enabled", true)) {
            blacked_out.setChecked(true);
        } else {
            blacked_out.setChecked(false);
        }
        blacked_out.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("blacked_out_enabled", true).commit();
                } else {
                    has_modified_anything = true;
                    prefs.edit().putBoolean("blacked_out_enabled", false).commit();
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.clear_dashboard_folder);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/dashboard./");
                DeleteRecursive(f);
                has_modified_anything = true;
            }
        });

        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.clear_cache);
        final TextView textView = (TextView) findViewById(R.id.cache);
        java.io.File file = new java.io.File(getCacheDir().getAbsolutePath());
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        textView.setText(Long.toString(fileSizeInMB) + " MB");
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File f = new File(getCacheDir().getAbsolutePath() + "/");
                DeleteRecursive(f);
                java.io.File file = new java.io.File(getCacheDir().getAbsolutePath());
                long fileSizeInBytes = file.length();
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;
                textView.setText(Long.toString(fileSizeInMB) + " MB");
            }
        });


    }

}


