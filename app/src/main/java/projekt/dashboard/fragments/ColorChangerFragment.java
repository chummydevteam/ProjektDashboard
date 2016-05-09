package projekt.dashboard.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.colorpicker.ColorPickerDialog;
import projekt.dashboard.colorpicker.ColorPickerPreference;
import projekt.dashboard.fragments.base.BasePageFragment;


/**
 * @author Nicholas Chum (nicholaschum)
 */

public class ColorChangerFragment extends BasePageFragment {

    public String color_picked, saved_color;
    public boolean is_autorestart_enabled, is_hotreboot_enabled, is_debugging_mode_enabled,
            is_force_update_enabled;
    public SharedPreferences prefs;
    public ViewGroup inflation;

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_colorpicker, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        final CheckBox autorestartSystemUI = (CheckBox) inflation.findViewById(R.id.switch1);
        autorestartSystemUI.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            is_autorestart_enabled = true;
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Universal variable to auto restart ENABLED.");
                        } else {
                            is_autorestart_enabled = false;
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Universal variable to auto restart DISABLED.");
                        }
                    }
                });

        CheckBox hotreboot = (CheckBox) inflation.findViewById(R.id.switch2);
        hotreboot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_hotreboot_enabled = true;
                    autorestartSystemUI.setChecked(false);
                    autorestartSystemUI.setClickable(false);
                    if (is_debugging_mode_enabled) Log.e("CheckBox",
                            "Universal variable to hot reboot ENABLED.");
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(
                                    R.string.colorpicker_toast_disable_hot_reboot_switch_systemui_restart),
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    is_hotreboot_enabled = false;
                    autorestartSystemUI.setClickable(true);
                    if (is_debugging_mode_enabled) Log.e("CheckBox",
                            "Universal variable to hot reboot DISABLED.");
                }
            }
        });

        CheckBox debugmode = (CheckBox) inflation.findViewById(R.id.switch3);
        debugmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_debugging_mode_enabled = true;
                    Log.e("CheckBox", "Universal variable to advanced log ENABLED.");
                } else {
                    is_debugging_mode_enabled = false;
                    Log.e("CheckBox", "Universal variable to advanced log DISABLED.");
                }
            }
        });

        CheckBox forceUpdateResource = (CheckBox) inflation.findViewById(R.id.switch4);
        forceUpdateResource.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            is_force_update_enabled = true;
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Force update has been ENABLED.");
                        } else {
                            is_force_update_enabled = false;
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Force update has been DISABLED.");
                        }
                    }
                });

        CardView akzent = (CardView) inflation.findViewById(R.id.akzent);
        akzent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.materialdark.donate")) {

                    File resourceFile = new File(getActivity().getFilesDir(),
                            "materialdark-resources.apk");
                    if (!resourceFile.exists() || is_force_update_enabled) {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "File not found, attempting to download...");
                        if (isNetworkAvailable()) {
                            if (is_debugging_mode_enabled) Log.e("Initialization",
                                    "Network found, downloading...");
                            String[] downloadCommands = {"https://github.com/nicholaschum/" +
                                    "ProjektDashboard/raw/resources/materialdark-resources.apk",
                                    "materialdark"};
                            new downloadResources().execute(downloadCommands);
                            String[] firstPhaseCommands = {"materialdark"};
                            new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                            launchColorPicker("akzent",
                                    "/data/resource-cache/com.chummy.jezebel.materialdark.donate" +
                                            "/common/resources.apk");
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("patch required")
                                    .content("to patch the system cache for color swapping " +
                                            "capabilities, we must download a small 36kb apk " +
                                            "resource cache file used by theme engine, " +
                                            "however an internet connection is required.")
                                    .positiveText("Okay")
                                    .negativeText("Cancel")
                                    .show();
                        }
                    } else {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "Download not required, using stored cache...");
                        String[] firstPhaseCommands = {"materialdark"};
                        new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                        launchColorPicker("akzent",
                                "/data/resource-cache/com.chummy.jezebel.materialdark.donate" +
                                        "/common/resources.apk");
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("akZent not found!")
                            .content("Please install akZent before using this feature!")
                            .positiveText("Okay")
                            .negativeText("Cancel")
                            .show();
                }
            }
        });
        akzent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.materialdark.donate")) {

                    File resourceFile = new File(getActivity().getFilesDir(),
                            "stock-materialdark-resources.apk");
                    if (!resourceFile.exists() || is_force_update_enabled) {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "File not found, attempting to download...");
                        if (isNetworkAvailable()) {
                            if (is_debugging_mode_enabled) Log.e("Initialization",
                                    "Network found, downloading...");
                            String[] downloadCommands = {"https://github.com/nicholaschum/" +
                                    "ProjektDashboard/raw/resources/" +
                                    "stock-materialdark-resources.apk",
                                    "stock-materialdark"};
                            new downloadResources().execute(downloadCommands);
                            String[] firstPhaseCommands = {"stock-materialdark",
                                    "/data/resource-cache/com.chummy.jezebel." +
                                            "materialdark.donate/common/resources.apk", "akZent"};
                            new restorePhaseAsyncTasks().execute(firstPhaseCommands);
                            SharedPreferences settings = PreferenceManager.
                                    getDefaultSharedPreferences(getContext());
                            settings.edit().remove("akzent").commit();
                            return true;
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("download required")
                                    .content("to unpatch the system cache from color swapping " +
                                            "capabilities, we must download a small 36kb apk " +
                                            "resource cache file used by theme engine, " +
                                            "however an internet connection is required.")
                                    .positiveText("Okay")
                                    .negativeText("Cancel")
                                    .show();
                            return false;
                        }
                    } else {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "Download not required, using stored cache...");
                        String[] firstPhaseCommands = {"stock-materialdark",
                                "/data/resource-cache/com.chummy.jezebel." +
                                        "materialdark.donate/common/resources.apk"};
                        new restorePhaseAsyncTasks().execute(firstPhaseCommands);
                        SharedPreferences settings = PreferenceManager.
                                getDefaultSharedPreferences(getContext());
                        settings.edit().remove("akzent").commit();
                        return true;
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("akZent not found!")
                            .content("Please install akZent before using this feature!")
                            .positiveText("Okay")
                            .negativeText("Cancel")
                            .show();
                    return false;
                }
            }
        });


        CardView blakzent = (CardView) inflation.findViewById(R.id.blakzent);
        blakzent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.blackedout.donate")) {

                    File directory = new File(getActivity().getFilesDir(),
                            "blackedout-resources.apk");
                    if (!directory.exists() || is_force_update_enabled) {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "File not found, attempting to download...");
                        if (isNetworkAvailable()) {
                            if (is_debugging_mode_enabled) Log.e("Initialization",
                                    "Network found, downloading...");
                            String[] downloadCommands = {"https://github.com/nicholaschum/" +
                                    "ProjektDashboard/raw/resources/blackedout-resources.apk",
                                    "blackedout"};
                            new downloadResources().execute(downloadCommands);
                            String[] firstPhaseCommands = {"blackedout"};
                            new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                            launchColorPicker("blakzent",
                                    "/data/resource-cache/com.chummy.jezebel.blackedout.donate" +
                                            "/common/resources.apk");
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("patch required")
                                    .content("to patch the system cache for color swapping " +
                                            "capabilities, we must download a small 36kb apk " +
                                            "resource cache file used by theme engine, " +
                                            "however an internet connection is required.")
                                    .positiveText("Okay")
                                    .negativeText("Cancel")
                                    .show();
                        }
                    } else {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "Download not required, using stored cache...");
                        String[] firstPhaseCommands = {"blackedout"};
                        new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                        launchColorPicker("blakzent",
                                "/data/resource-cache/com.chummy.jezebel.blackedout.donate" +
                                        "/common/resources.apk");
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("blakZent not found!")
                            .content("Please install blakZent before using this feature!")
                            .positiveText("Okay")
                            .negativeText("Cancel")
                            .show();
                }
            }
        });
        blakzent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.blackedout.donate")) {

                    File resourceFile = new File(getActivity().getFilesDir(),
                            "stock-blackedout-resources.apk");
                    if (!resourceFile.exists() || is_force_update_enabled) {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "File not found, attempting to download...");
                        if (isNetworkAvailable()) {
                            if (is_debugging_mode_enabled) Log.e("Initialization",
                                    "Network found, downloading...");
                            String[] downloadCommands = {"https://github.com/nicholaschum/" +
                                    "ProjektDashboard/raw/resources/stock-blackedout-resources.apk",
                                    "stock-blackedout"};
                            new downloadResources().execute(downloadCommands);
                            String[] firstPhaseCommands = {"stock-blackedout",
                                    "/data/resource-cache/com.chummy.jezebel." +
                                            "blackedout.donate/common/resources.apk", "blakZent"};
                            new restorePhaseAsyncTasks().execute(firstPhaseCommands);
                            SharedPreferences settings = PreferenceManager.
                                    getDefaultSharedPreferences(getContext());
                            settings.edit().remove("blakzent").commit();
                            return true;
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("download required")
                                    .content("to unpatch the system cache from color swapping " +
                                            "capabilities, we must download a small 36kb apk " +
                                            "resource cache file used by theme engine, " +
                                            "however an internet connection is required.")
                                    .positiveText("Okay")
                                    .negativeText("Cancel")
                                    .show();
                            return false;
                        }
                    } else {
                        if (is_debugging_mode_enabled) Log.e("Initialization",
                                "Download not required, using stored cache...");
                        String[] firstPhaseCommands = {"stock-blackedout",
                                "/data/resource-cache/com.chummy.jezebel." +
                                        "blackedout.donate/common/resources.apk"};
                        new restorePhaseAsyncTasks().execute(firstPhaseCommands);
                        SharedPreferences settings = PreferenceManager.
                                getDefaultSharedPreferences(getContext());
                        settings.edit().remove("blakzent").commit();
                        return true;
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title("blakZent not found!")
                            .content("Please install akZent before using this feature!")
                            .positiveText("Okay")
                            .negativeText("Cancel")
                            .show();
                    return false;
                }
            }
        });

        CardView projektklar = (CardView) inflation.findViewById(R.id.projektklar);
        projektklar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing here yet :)
            }
        });

        if (isAppInstalled(getContext(), "com.chummy.jezebel.materialdark.donate")) {
            akzent.setVisibility(View.VISIBLE);
        }
        if (isAppInstalled(getContext(), "com.chummy.jezebel.blackedout.donate")) {
            blakzent.setVisibility(View.VISIBLE);
        }
        if (!isAppInstalled(getContext(), "com.chummy.jezebel.materialdark.donate") &&
                !isAppInstalled(getContext(), "com.chummy.jezebel.blackedout.donate")) {
            TextView installedTitle = (TextView) inflation.findViewById(R.id.installed_title);
            installedTitle.setText("no cdt color switch themes installed");
        }
        projektklar.setVisibility(View.GONE); // disable projekt klar functionality for now
        return inflation;
    }


    public void launchColorPicker(String theme_name, String theme_dir) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        saved_color = settings.getString(theme_name, "0");

        if (saved_color != "0") {
            launchColorPickerPrivate(theme_dir, Color.parseColor(saved_color), theme_name);
        } else {
            int white = Color.argb(255, 255, 255, 255);
            launchColorPickerPrivate(theme_dir, white, theme_name);
        }
    }

    private void launchColorPickerPrivate(final String theme_dir, int color,
                                          final String theme_name) {

        final ColorPickerDialog cpd = new ColorPickerDialog(getActivity(), color);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor editor = settings.edit();

        cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                color_picked = ColorPickerPreference.convertToRGB(color);
                editor.putString(theme_name, color_picked).commit();
                String[] secondPhaseCommands = {theme_dir};
                new secondPhaseAsyncTasks().execute(secondPhaseCommands);
            }
        });
        cpd.show();
    }

    public void cleanTempFolder() {
        File dir = getActivity().getCacheDir();
        deleteRecursive(dir);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public int getTitle() {
        return R.string.color_changer;
    }

    private class restorePhaseAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String prefix = params[0];
            String directory = params[1];
            String themename = "";
            if (prefix.equals("stock-materialdark")) {
                themename = "akZent";
            }
            if (prefix.equals("stock-blackedout")) {
                themename = "blakZent";
            }
            copyCommonsFile(prefix, directory, themename);
            return null;
        }

        private void copyCommonsFile(String prefix, String directory, String themename) {
            File source = new File(getActivity().getFilesDir().getAbsolutePath() + "/"
                    + prefix + "-resources.apk");
            String destinationPath = getActivity().getCacheDir().getAbsolutePath() + "/"
                    + prefix + "-resources.apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                if (is_debugging_mode_enabled) Log.e("copyCommonsFile",
                        "Successfully copied commons apk from resource-cache to work directory");
                copyFinalizedAPK(prefix, directory, themename);
            } catch (IOException e) {
                if (is_debugging_mode_enabled) Log.e("copyCommonsFile",
                        "Failed to copy commons apk from resource-cache to work directory");
                e.printStackTrace();
            }
        }

        public void copyFinalizedAPK(String prefix, String directory, String themename) {
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/" + prefix + "-resources.apk " + directory);
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + directory);
            if (is_debugging_mode_enabled) Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/data/resource-cache and modified the permissions!");
            cleanTempFolder();
            Snackbar snack = Snackbar.make(inflation,
                    "patched resource for " + themename + " has been removed successfully!",
                    Snackbar.LENGTH_SHORT);
            ViewGroup group = (ViewGroup) snack.getView();
            if (prefs.getBoolean("blacked_out_enabled", true)) {
                group.setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.primary_1_blacked_out));
            } else {
                group.setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.primary_1_dark_material));
            }
            snack.show();
            if (is_autorestart_enabled) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.systemui");
                eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.settings");
            }
            if (is_hotreboot_enabled) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
            }
        }
    }

    private class firstPhaseAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String prefix = params[0];
            copyCommonsFile(prefix);
            return null;
        }

        private void copyCommonsFile(String prefix) {
            File source = new File(getActivity().getFilesDir().getAbsolutePath() + "/"
                    + prefix + "-resources.apk");
            String destinationPath = getActivity().getCacheDir().getAbsolutePath() +
                    "/common-resources.apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                if (is_debugging_mode_enabled) Log.e("copyCommonsFile",
                        "Successfully copied commons apk from resource-cache to work directory");
            } catch (IOException e) {
                if (is_debugging_mode_enabled) Log.e("copyCommonsFile",
                        "Failed to copy commons apk from resource-cache to work directory");
                e.printStackTrace();
            }
        }
    }

    private class secondPhaseAsyncTasks extends AsyncTask<String, String, Void> {

        private ProgressDialog pd;

        @Override
        protected Void doInBackground(String... params) {
            String theme_dir = params[0];
            createXMLfile("accent_color_dark.xml", theme_dir);
            pd.setProgress(60);
            return null;
        }

        protected void onPreExecute() {
            String[] responses = getResources().getStringArray(R.array.dialog_responses);

            int idx = new Random().nextInt(responses.length);
            String random = (responses[idx]);

            pd = new ProgressDialog(getActivity());
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage(random);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        protected void onPostExecute(Void result) {
            pd.dismiss();
        }

        private void createXMLfile(String string, String theme_dir) {
            try {
                // Create the working directory
                File directory = new File(getActivity().getCacheDir(), "/res/color-v14/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // Create the files
                File root = new File(getActivity().getCacheDir(), "/res/color-v14/" + string);
                if (!root.exists()) {
                    root.createNewFile();
                }
                FileWriter fw = new FileWriter(root);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
                String xmlRes1 = ("<selector" + "\n");
                String xmlRes2 = ("  xmlns:android=\"http://schemas.android.com/apk/res/android\">"
                        + "\n");
                String xmlRes3 = ("    <item android:color=" + "\"" + color_picked + "\"" + " />"
                        + "\n");
                String xmlRes4 = ("</selector>");
                pw.write(xmlTags);
                pw.write(xmlRes1);
                pw.write(xmlRes2);
                pw.write(xmlRes3);
                pw.write(xmlRes4);
                pw.close();
                bw.close();
                fw.close();
                if (string == "accent_color.xml") {
                    try {
                        compileDummyAPK(theme_dir);
                    } catch (Exception e) {
                        if (is_debugging_mode_enabled) Log.e("CreateXMLFileException",
                                "Could not create Dummy APK (EXCEPTION)");
                    }
                }
                if (string == "accent_color_light.xml") {
                    createXMLfile("accent_color.xml", theme_dir);
                }
                if (string == "accent_color_dark.xml") {
                    createXMLfile("accent_color_light.xml", theme_dir);
                }
            } catch (IOException e) {
                if (is_debugging_mode_enabled) Log.e("CreateXMLFileException",
                        "Failed to create new file (IOEXCEPTION).");
            }

        }

        private void compileDummyAPK(String theme_dir) throws Exception {
            if (is_debugging_mode_enabled)
                Log.e("CompileDummyAPK", "Beginning to compile dummy APK...");

            // Create AndroidManifest.xml first, cutting down the assets file transfer!

            File manifest = new File(getActivity().getCacheDir(), "AndroidManifest.xml");
            if (!manifest.exists()) {
                manifest.createNewFile();
            }
            FileWriter fw = new FileWriter(manifest);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" " +
                    "standalone=\"no\"?>" + "\n");
            String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/" +
                    "apk/res/android\" package=\"common\"/>" + "\n");
            pw.write(xmlTags);
            pw.write(xmlRes1);
            pw.close();
            bw.close();
            fw.close();

            Process nativeApp = Runtime.getRuntime().exec(
                    "aapt p -M " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/AndroidManifest.xml -S " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/res/ -I " +
                            "system/framework/framework-res.apk -F " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/color-resources.apk\n");
            IOUtils.toString(nativeApp.getInputStream());
            IOUtils.toString(nativeApp.getErrorStream());
            nativeApp.waitFor();
            if (is_debugging_mode_enabled) Log.e("CompileDummyAPK",
                    "Successfully compiled dummy apk!");
            unzip(theme_dir);
        }

        public void unzip(String theme_dir) {
            String source =
                    getActivity().getCacheDir().getAbsolutePath() + "/color-resources.apk";
            String destination =
                    getActivity().getCacheDir().getAbsolutePath() + "/color-resources/";

            try {
                ZipFile zipFile = new ZipFile(source);
                if (is_debugging_mode_enabled)
                    Log.e("Unzip", "The ZIP has been located and will now be unzipped...");
                zipFile.extractAll(destination);
                if (is_debugging_mode_enabled) Log.e("Unzip",
                        "Successfully unzipped the file to the corresponding directory!");
            } catch (ZipException e) {
                if (is_debugging_mode_enabled) Log.e("Unzip",
                        "Failed to unzip the file the corresponding directory. (EXCEPTION)");
                e.printStackTrace();
            } finally {
                try {
                    performAAPTonCommonsAPK(theme_dir);
                } catch (Exception e) {
                    //
                }
            }
        }

        private void performAAPTonCommonsAPK(String theme_dir)
                throws Exception {
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Mounting system as read-write as we prepare for some commands...");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/color-v14");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getCacheDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color_dark.xml " +
                            "/res/color-v14/accent_color_dark.xml");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getCacheDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color_light.xml " +
                            "/res/color-v14/accent_color_light.xml");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getCacheDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color.xml " +
                            "/res/color-v14/accent_color.xml");

            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Successfully copied all modified accent XMLs into the root folder.");

            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Preparing for clean up on common-resources...");

            Process nativeApp1 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_dark.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted dark accent file!");
            nativeApp1.waitFor();
            Process nativeApp2 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_light.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted light accent file!");
            nativeApp2.waitFor();
            Process nativeApp3 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted main accent file!");
            nativeApp3.waitFor();

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_dark.xml");
            if (is_debugging_mode_enabled)
                Log.e("performAAPTonCommonsAPK", "Added freshly created dark accent file...");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_light.xml");
            if (is_debugging_mode_enabled)
                Log.e("performAAPTonCommonsAPK", "Added freshly created light accent file...");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color.xml");
            if (is_debugging_mode_enabled)
                Log.e("performAAPTonCommonsAPK",
                        "Added freshly created main accent file...ALL DONE!");

            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/color-v14");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Cleaned up root directory and remounted system as read-only.");

            // Finally, let's make sure the directories are pushed to the last command
            copyFinalizedAPK(theme_dir);
        }

        public void copyFinalizedAPK(String directory) {
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/common-resources.apk " + directory);
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + directory);
            if (is_debugging_mode_enabled) Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/data/resource-cache and modified the permissions!");
            cleanTempFolder();
            if (is_debugging_mode_enabled) Log.e("cleanTempFolder",
                    "Successfully cleaned up the whole work area!");

            if (is_autorestart_enabled) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.systemui");
                eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.settings");
            }
            if (is_hotreboot_enabled) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
            }
        }
    }

    private class downloadResources extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                if (is_debugging_mode_enabled) Log.e("File download", "Started from :" + sUrl[0]);
                URL url = new URL(sUrl[0]);
                //URLConnection connection = url.openConnection();
                File myDir = new File(getActivity().getFilesDir().getAbsolutePath());
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(sUrl[0]);
                request.setHeader("User-Agent", sUrl[0]);

                HttpResponse response = client.execute(request);
                // create the directory if it doesnt exist
                if (!myDir.exists()) myDir.mkdirs();

                File outputFile = new File(myDir, sUrl[1] + "-resources.apk");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int fileLength = connection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                if (is_debugging_mode_enabled) Log.e("File download", "complete");
            } catch (Exception e) {
                if (is_debugging_mode_enabled) Log.e("File download", "error: " + e.getMessage());
            }
            return null;
        }
    }
}
