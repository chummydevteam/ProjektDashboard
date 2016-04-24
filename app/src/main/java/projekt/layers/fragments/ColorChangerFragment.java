package projekt.layers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import projekt.layers.R;
import projekt.layers.colorpicker.ColorPickerDialog;
import projekt.layers.colorpicker.ColorPickerPreference;
import projekt.layers.fragments.base.BasePageFragment;


/**
 * @author Nicholas Chum (nicholaschum)
 */

public class ColorChangerFragment extends BasePageFragment {


    SharedPreferences prefs;
    final String PREFS_NAME = "MyPrefsFile";
    String link64 = "https://dl.dropboxusercontent.com/u/" +
            "2429389/dashboard.%20files/aapt-64";
    String link = "https://dl.dropboxusercontent.com/u/" +
            "2429389/dashboard.%20files/aapt";
    public String vendor = "/system/vendor/overlay";
    public String mount = "/system";
    FloatingActionButton fab;
    ImageButton imageButton;
    TextView accentcolor;
    public String color_picked ="#ff0000";
    public ViewGroup inflation;

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

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            if (isNetworkAvailable()) {

                Log.e("Switcher", "First time");
                Log.e("DownloadAAPT", "Calling Function");
                downloadAAPT();
                // record the fact that the app has been started at least once
                prefs.edit().putString("color_saved", color_picked).commit();
                settings.edit().putBoolean("my_first_time", false).commit();
            }
        }


        accentcolor = (TextView) inflation.findViewById(R.id.accentcolor);
        imageButton = (ImageButton) inflation.findViewById(R.id.preview);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int black = Color.argb(255, 255, 255, 255);
                final ColorPickerDialog cpd = new ColorPickerDialog(getActivity(), black);
                cpd.setAlphaSliderVisible(false);
                cpd.setHexValueEnabled(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        color_picked = ColorPickerPreference.convertToARGB(color);
                        imageButton.setColorFilter(color);
                        accentcolor.setText(color_picked);
                    }
                });
                cpd.show();
            }
        });

        fab = (FloatingActionButton) inflation.findViewById(R.id.changeTheme);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color_picked)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("colorswatch", "Calling Function");
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color_picked)));
                colorswatch();
            }
        });

        return inflation;
    }

    public void downloadAAPT() {
        Log.e("DownloadAAPT", "Function Called");
        Log.e("DownloadAAPT", "Function Started");
        Log.e("Checkbitphone", "Calling Function");
        boolean flag = checkbitphone();
        if (flag) {
            Log.e("DownloadAAPT", "64 Bit Active");
            Log.e("64 bit Device ", Build.DEVICE + " Found,now changing the vendor and mount");
            vendor = "/vendor/overlay";
            mount = "/vendor";
            Log.e("64 bit Device ", Build.DEVICE + " changed the vendor and mount");
            String[] downloadCommands = {link64,
                    "aapt"};
            Log.e("DownloadindResources", "Calling Function");
            new downloadResources().execute(downloadCommands);
            Log.e("DownloadAAPT", "Function Stopped");
        } else

        {
            Log.e("DownloadAAPT", "32 Bit Active");
            Log.e("32 bit Device ", Build.DEVICE + " Found,now changing the vendor and mount");
            vendor = "/system/vendor/overlay";
            mount = "/system";
            Log.e("32 bit Device ", Build.DEVICE + " changed the vendor and mount");
            String[] downloadCommands = {link,
                    "aapt"};
            new downloadResources().execute(downloadCommands);
            Log.e("DownloadAAPT", "Function Stopped");
        }
    }

    public boolean checkbitphone() {
        Log.e("Checkbitphone", "Function Called");
        Log.e("Checkbitphone", "Function Started");
        String[] bit = Build.SUPPORTED_32_BIT_ABIS;
        String[] bit64 = Build.SUPPORTED_64_BIT_ABIS;
        int flag = 0;
        try {
            if (bit64[0] != null) {
                Log.e("Checkbitphone", "64 Found");
                Log.e("Checkbitphone", "Checking if its one from FAB");
                if (Build.DEVICE.equals("flounder") || Build.DEVICE.equals("flounder_lte") || Build.DEVICE.equals("angler") || Build.DEVICE.equals("bullhead")) {
                    Log.e("64 bit Device ", Build.DEVICE + " Found,now returning");
                    Log.e("Checkbitphone", "Function Stopped");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (flag == 0) {
                if (bit[0] != null) {
                    Log.e("Checkbitphone", "32 Bit Active");
                    Log.e("Checkbitphone", "Normal Phone Overlay Folder found");
                    Log.e("Checkbitphone", "Function Stopped");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class downloadResources extends AsyncTask<String, Integer, String> {

        private ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            Log.e("Downloadind Resources", "Function Called");
            Log.e("Downloadind Resources", "Function Started");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Downloading Resources");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            pd.setMessage("Download Complete,Getting Things Finalised");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    File File = new File(getActivity().getFilesDir().getAbsolutePath(), "/aapt");
                    if (File.exists()) {
                        Log.e("File Downloaded Found", "Copying File");
                        Log.e("copyAAPT", "Calling Function");
                        copyAAPT();
                    }
                    pd.dismiss();
                }
            }, 3000);
            Log.e("Downloadind Resources", "Function Stoppped");
        }

        public void copyAAPT() {
            Log.e("copyAAPT", "Function Called");
            Log.e("copyAAPT", "Function Started");
            Log.e("copyAAPT", "Start");
            String mount = new String("mount -o remount,rw /");
            String mountsys = new String("mount -o remount,rw /system");
            String remount = new String("mount -o remount,ro /");
            String remountsys = new String("mount -o remount,ro /system");
            eu.chainfire.libsuperuser.Shell.SU.run(mount);
            Log.e("copyAAPT", "Mounted /");
            eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
            Log.e("copyAAPT", "Mounted " + mount);

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/aapt" + " /system/bin/aapt");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 777 /system/bin/aapt");
            Log.e("copyAAPT", "Copied AAPT");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            Log.e("copyAAPT", "ReMounted /");
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyAAPT", "ReMounted " + mount);
            Log.e("copyAAPT", "End");
            Log.e("copyAAPT", "Function Stopped");
        }


        @Override
        protected String doInBackground(String... sUrl) {
            try {
                Log.e("File download", "Started from :" + sUrl[0]);
                URL url = new URL(sUrl[0]);
                //URLConnection connection = url.openConnection();
                File myDir = new File(getActivity().getFilesDir().getAbsolutePath());
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(sUrl[0]);
                request.setHeader("User-Agent", sUrl[0]);

                HttpResponse response = client.execute(request);
                // create the directory if it doesnt exist
                if (!myDir.exists()) myDir.mkdirs();

                File outputFile = new File(myDir, sUrl[1]);

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

                Log.e("File download", "complete");
            } catch (Exception e) {
                Log.e("File download", "error: " + e.getMessage());
            }
            return null;
        }
    }
    public void colorswatch() {
        Log.e("colorswatch", "Function Called");
        Log.e("colorswatch", "Function Started");
        boolean phone = checkbitphone();
        if (phone == true) {
            Log.e("colorswatch", "Found 64,Setting Vendor");
            vendor = "/vendor/overlay";
            mount = "/vendor";
        }
        String[] location = {vendor + "/Akzent_Framework.apk"};
        Log.e("FirstSyncTasks", "Calling Function");
        new firstPhaseAsyncTasks().execute(location);
        Log.e("PickColors", "Calling Function");
        pickColor(vendor + "/Akzent_Framework.apk");
        Log.e("colorswatch", "Function Stopped");
    }
    public void pickColor(final String directory) {
        Log.e("PickColors", "Function Called");
        Log.e("PickColors", "Function Started");
        Log.e("SecondPhaseTasks", "Calling Function");
        new secondPhaseAsyncTasks().execute(directory);
        Log.e("PickColors", "Function Stopped");

    }
    private class firstPhaseAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.e("FirstSyncTasks", "Function Called");
            Log.e("FirstSyncTasks", "Function Started");
            String theme_dir = params[0];
            Log.e("CopyFrameworkFile", "Calling Function");
            copyFrameworkFile(theme_dir);
            Log.e("FirstSyncTasks", "Function Stopped");
            return null;
        }

        private void copyFrameworkFile(String theme_dir) {
            Log.e("CopyFrameworkFile", "Function Called");
            Log.e("CopyFrameworkFile", "Function Started");
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getFilesDir().getAbsolutePath() +
                    "/Akzent_Framework.apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                Log.e("CopyFrameworkFile",
                        "Successfully copied framework apk from overlays folder to work directory");
                Log.e("CopyFrameworkFile", "Function Stopped");
            } catch (IOException e) {
                Log.e("CopyFrameworkFile",
                        "Failed to copy framework apk from resource-cache to work directory");
                Log.e("CopyFrameworkFile", "Function Stopped");
                e.printStackTrace();
            }
        }
    }
    private class secondPhaseAsyncTasks extends AsyncTask<String, String, Void> {

        private ProgressDialog pd;

        @Override
        protected Void doInBackground(String... params) {
            String theme_dir = params[0];
            createXMLfile("tertiary_text_dark.xml", theme_dir);
            pd.setProgress(60);
            return null;
        }

        protected void onPreExecute() {
            Log.e("SecondPhaseTasks", "Function Called");
            Log.e("SecondPhaseTasks", "Function Started");
            String[] responses = {
                    "Please wait, while your phone gets beautified!",
                    "Injecting beautiful accents all over the place~",
                    "Sprinkling some magic over here...and over there....",
                    "Are you ready for some rainbow-licious fun?",
                    "OMG, am I broken?",
                    "I hope you did your reading, because you need to get ready for the " +
                            "amount of awesomeness this gives!",
                    "I hope you don't have to report bugs......please no.",
                    "That color is simply gorgeous!",
                    "I don't have a library card, but do you mind if I check you out?",
                    "I seem to have lost my phone number. Can I have yours?",
                    "Are you religious? Because you're the answer to all my prayers.",
                    "Did you sit in a pile of sugar? Cause you have a pretty sweet ass.",
                    "Do you live in a corn field, cause I'm stalking you.",
                    "You look cold. Want to use me as a blanket?",
                    "Can I take your picture to prove to all my friends that angels do exist?",
                    "My doctor says I'm lacking Vitamin U.",
                    "If I were a cat I'd spend all 9 lives with you."};
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
            Log.e("SecondPhaseTasks", "Function Stopped");
        }

        private void createXMLfile(String string, String theme_dir) {
            try {
                // Create the working directory
                File directory = new File(getActivity().getFilesDir(), "/res/color/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // Create the files
                File root = new File(getActivity().getFilesDir(), "/res/color/" + string);
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
                String xmlRes3 = ("    <item android:state_enabled=\"false\" android:color=\"" + color_picked + "\" />"
                        + "\n");
                String xmlRes4 = ("    <item android:state_window_focused=\"false\" android:color=\"" + color_picked + "\" />"
                        + "\n");
                String xmlRes5 = ("    <item android:state_pressed=\"true\" android:color=\"" + color_picked + "\" />"
                        + "\n");
                String xmlRes6 = ("    <item android:state_selected=\"true\" android:color=\"" + color_picked + "\" />"
                        + "\n");
                String xmlRes7 = ("    <item android:color=\"" + color_picked + "\" />"
                        + "\n");
                String xmlRes8 = ("</selector>");
                pw.write(xmlTags);
                pw.write(xmlRes1);
                pw.write(xmlRes2);
                pw.write(xmlRes3);
                pw.write(xmlRes4);
                pw.write(xmlRes5);
                pw.write(xmlRes6);
                pw.write(xmlRes7);
                pw.write(xmlRes8);
                pw.close();
                bw.close();
                fw.close();
                Log.e("CreateXMLFileException",
                        string + " Created");
                if (string == "tertiary_text_dark.xml") {
                    try {
                        compileDummyAPK(theme_dir);
                    } catch (Exception e) {
                        Log.e("CreateXMLFileException",
                                "Could not create Dummy APK (EXCEPTION)");
                    }
                }
       /*         if (string == "accent_color_light.xml") {
                    createXMLfile("accent_color.xml", theme_dir);
                }
                if (string == "accent_color_dark.xml") {
                    createXMLfile("accent_color_light.xml", theme_dir);
                }
      */
            } catch (IOException e) {
                Log.e("CreateXMLFileException",
                        "Failed to create new file (IOEXCEPTION).");
            }

        }

        private void compileDummyAPK(String theme_dir) throws Exception {

            Log.e("CompileDummyAPK", "Beginning to compile dummy APK...");

            // Create AndroidManifest.xml first, cutting down the assets file transfer!

            File manifest = new File(getActivity().getFilesDir(), "AndroidManifest.xml");
            if (!manifest.exists()) {
                manifest.createNewFile();
            }
            FileWriter fw = new FileWriter(manifest);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            //String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" " +
            //          "standalone=\"no\"?>" + "\n");
            String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/" +
                    "apk/res/android\" package=\"common\" android:versionCode=\"1\"" +
                    " android:versionName=\"1.0\">" + "\n");
            String xmlRes2 = ("<overlay android:targetPackage=\"android\" android:priority=\"100\"/>" + "\n");
            String xmlRes3 = ("</manifest>" + "\n");
            //  pw.write(xmlTags);
            pw.write(xmlRes1);
            pw.write(xmlRes2);
            pw.write(xmlRes3);
            pw.close();
            bw.close();
            fw.close();

            Process nativeApp = Runtime.getRuntime().exec(
                    "aapt p -M " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/AndroidManifest.xml -S " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/res/ -I " +
                            "system/framework/framework-res.apk -F " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources.apk\n");
            IOUtils.toString(nativeApp.getInputStream());
            IOUtils.toString(nativeApp.getErrorStream());
            nativeApp.waitFor();
            Log.e("CompileDummyAPK",
                    "Successfully compiled dummy apk!");
            unzip(theme_dir);
        }

        public void unzip(String theme_dir) {
            String source =
                    getActivity().getFilesDir().getAbsolutePath() + "/color-resources.apk";
            String destination =
                    getActivity().getFilesDir().getAbsolutePath() + "/color-resources/";

            try {
                ZipFile zipFile = new ZipFile(source);
                Log.e("Unzip", "The ZIP has been located and will now be unzipped...");
                zipFile.extractAll(destination);
                Log.e("Unzip",
                        "Successfully unzipped the file to the corresponding directory!");
            } catch (ZipException e) {
                Log.e("Unzip",
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
            Log.e("performAAPTonCommonsAPK",
                    "Mounting system as read-write as we prepare for some commands...");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/color");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color/tertiary_text_dark.xml " +
                            "/res/color/tertiary_text_dark.xml");
         /*   eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color/accent_color_light.xml " +
                            "/res/color/accent_color_light.xml");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color/accent_color.xml " +
                            "/res/color/accent_color.xml");
*/
            Log.e("performAAPTonCommonsAPK",
                    "Successfully copied all modified accent XMLs into the root folder.");

            Log.e("performAAPTonCommonsAPK",
                    "Preparing for clean up on resources...");
/*
            Process nativeApp1 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/framework.apk res/color/accent_color_dark.xml");
            Log.e("performAAPTonCommonsAPK",
                    "Deleted dark accent file!");
            nativeApp1.waitFor();
            Process nativeApp2 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/framework.apk res/color/accent_color_light.xml");
            Log.e("performAAPTonCommonsAPK",
                    "Deleted light accent file!");
            nativeApp2.waitFor();
  */
            Process nativeApp3 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk res/color/tertiary_text_dark.xml");
            Log.e("performAAPTonCommonsAPK",
                    "Deleted main tertiary_text_dark file!");
            nativeApp3.waitFor();
/*
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/framework.apk res/color/accent_color_dark.xml");

            Log.e("performAAPTonCommonsAPK", "Added freshly created dark accent file...");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/framework.apk res/color/accent_color_light.xml");

            Log.e("performAAPTonCommonsAPK", "Added freshly created light accent file...");
  */
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk res/color/tertiary_text_dark.xml");

            Log.e("performAAPTonCommonsAPK",
                    "Added freshly created main tertiary_text_dark file...ALL DONE!");

            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/color");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
            Log.e("performAAPTonCommonsAPK",
                    "Cleaned up root directory and remounted system as read-only.");

            // Finally, let's make sure the directories are pushed to the last command
            if (checkbitphone()) {
                copyFABFinalizedAPK();
            } else {
                copyFinalizedAPK();
            }
            eu.chainfire.libsuperuser.Shell.SU.run("busybox pkill com.android.systemui");
        }

        public void copyFinalizedAPK() {
            String mount = "mount -o remount,rw /";
            String mountsys = "mount -o remount,rw /system";
            String remount = "mount -o remount,ro /";
            String remountsys = "mount -o remount,ro /system";
            eu.chainfire.libsuperuser.Shell.SU.run(mount);
            eu.chainfire.libsuperuser.Shell.SU.run(mountsys);

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk " + "/system/vendor/overlay/Akzent_Framework.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/Akzent_Framework.apk");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.layers/files");
            Log.e("copyFinalizedAPK",
                    "Successfully Deleted Files ");

        }

        public void copyFABFinalizedAPK() {
            String mount = "mount -o remount,rw /";
            String mountsys = "mount -o remount,rw /vendor";
            String remount = "mount -o remount,ro /";
            String remountsys = "mount -o remount,ro /vendor";
            eu.chainfire.libsuperuser.Shell.SU.run(mount);
            eu.chainfire.libsuperuser.Shell.SU.run(mountsys);

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk " + "/vendor/overlay/Akzent_Framework.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/Akzent_Framework.apk");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.layers/files");
            Log.e("copyFinalizedAPK",
                    "Successfully Deleted Files ");

        }

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

}
