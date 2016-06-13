package projekt.dashboard.layers.fragments;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.ButterKnife;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.colorpicker.ColorPickerDialog;
import projekt.dashboard.layers.colorpicker.ColorPickerPreference;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.ui.MainActivity;
import projekt.dashboard.layers.util.LayersFunc;

import static projekt.dashboard.layers.util.LayersFunc.LayersColorSwitch;
import static projekt.dashboard.layers.util.LayersFunc.isNetworkAvailable;


/**
 * @author Nicholas Chum (nicholaschum) feat. Aditya Gupta
 */

public class ColorChangerFragment extends BasePageFragment {

    static String File = "Akzent_Framework";
    public String color_picked = "#ff0000";
    public ViewGroup inflation;
    SharedPreferences prefs;
    FloatingActionButton fab;
    ImageButton imageButton;
    TextView accentcolor;
    String SettLink = "https://github.com/adityaakadynamo/GG/raw/master/res.zip";
    static Context context;

    public static String getFile() {
        return File;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_colorpicker, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        File aa = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/dashboard./settings/res.zip");
        if (aa.exists()) {

        } else {
            if (isNetworkAvailable(getActivity())) {
                String[] downloadCommands = {SettLink,
                        "res.zip"};
                new downloadResources().execute(downloadCommands);
            }
        }
        try {
            LayersFunc.createSettManifest(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (prefs.getBoolean("dialog", true)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("ColorSwapper :)");
            ad.setMessage("Lets get started with switching colors without reboot.So what you need" +
                    " to basically do is :-\n1.Click on the Accent Color and Change it to " +
                    "whatever color you want.\n2. Click on the fab and wait for the color to get " +
                    "applied.\n3.BOOM !! MAGIC !!\n\n\t\t\t\t\tHave Fun,Enjoy!!");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (LayersFunc.isAppInstalled(getActivity(), "com.chummy.aditya.materialdark" +
                            ".layers.donate")) {
                        startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777" +
                                ".rroandlayersmanager", "com.lovejoy777.rroandlayersmanager" +
                                ".MainActivity")));
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play" +
                                ".google.com/store/apps/details?id=com.chummy.aditya.materialdark" +
                                ".layers.donate")));
                    }
                }
            });
            ad.setNeutralButton("Dont Show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    prefs.edit().putBoolean("dialog", false).apply();
                }
            });
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
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color_picked)));
                colorswatch();
            }
        });

        return inflation;
    }

    private static class downloadResources extends AsyncTask<String, Integer, String> {

        private ProgressDialog pd = new ProgressDialog(context);

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
            Log.e("File Downloaded Found", "Copying File");
            Log.e("copyAAPT", "Calling Function");
            copyAAPT();
            pd.dismiss();
            Log.e("Downloadind Resources", "Function Stoppped");
        }

        public void copyAAPT() {

        }


        @Override
        protected String doInBackground(String... sUrl) {
            try {
                Log.e("File download", "Started from :" + sUrl[0]);
                URL url = new URL(sUrl[0]);
                //URLConnection connection = url.openConnection();
                File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/");
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
        LayersFunc.copyFileToApp(getActivity(), LayersFunc.getvendor() + "/" + File + ".apk", File + ".apk");
        File source = new File(LayersFunc.getvendor() + "/Akzent_Settings.apk");
        String destinationPath = getActivity().getFilesDir().getAbsolutePath() + "/settings/Akzent_Settings.apk";
        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            Log.e("Settings",
                    "Failed to copy settings apk to work directory");
            Log.e("Settings", "Function Stopped");
            e.printStackTrace();
        }
        Log.d("Progress", "1");
        LayersFunc.createSettColXML("colors.xml", color_picked);
        Log.d("Settings", "Colors-Y");
        LayersFunc.createSettDimXML("dimens.xml");
        Log.d("Settings", "Dimens-Y");
        LayersFunc.createSettStyXML("styles.xml", color_picked);
        Log.d("Settings", "Styles-Y");
        pickColor(LayersFunc.getvendor() + "/" + File + ".apk");
    }

    public void pickColor(final String directory) {
        new secondPhaseAsyncTasks().execute(directory);

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
            eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@" + File + ".apk@idmap /data/resource-cache/vendor@overlay@" + File + ".apk@idmap.bak");
            if (LayersFunc.checkBitPhone()) {
                String mount = "mount -o remount,rw /";
                String mountsys = "mount -o remount,rw /vendor";
                String remount = "mount -o remount,ro /";
                String remountsys = "mount -o remount,ro /vendor";
                eu.chainfire.libsuperuser.Shell.SU.run(mount);
                eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getFilesDir().getAbsolutePath() +
                                "/" + File + ".apk " + "/vendor/overlay/" + File + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getFilesDir()
                                .getAbsolutePath() + " into " +
                                "/vendor/overlay/ and modified the permissions!");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/" + File + ".apk");
                eu.chainfire.libsuperuser.Shell.SU.run(remount);
                eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK into " +
                                "/vendor/overlay/ and modified the permissions!");
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                        ".layers/files");
                Log.e("copyFinalizedAPK",
                        "Successfully Deleted Files ");
            } else {
                String mount = "mount -o remount,rw /";
                String mountsys = "mount -o remount,rw /system";
                String remount = "mount -o remount,ro /";
                String remountsys = "mount -o remount,ro /system";
                eu.chainfire.libsuperuser.Shell.SU.run(mount);
                eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getFilesDir().getAbsolutePath() +
                                "/" + File + ".apk " + "/system/vendor/overlay/" + File + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getFilesDir()
                                .getAbsolutePath() + " into " +
                                "/system/vendor/overlay/ and modified the permissions!");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/" + File +
                        ".apk");
                eu.chainfire.libsuperuser.Shell.SU.run(remount);
                eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                        ".layers/files");
                Log.e("copyFinalizedAPK",
                        "Successfully Deleted Files ");
            }
            eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@" + File + ".apk@idmap.bak /data/resource-cache/vendor@overlay@" + File + ".apk@idmap");
            pd.dismiss();
            Log.d("Progress", "10");
            eu.chainfire.libsuperuser.Shell.SU.run("busybox killall com.android.systemui");
        }

        private void createXMLfile(String string, String theme_dir) {
            LayersFunc.createXML(string, getActivity(), color_picked);

            Log.d("Progress", "2");
            if (string.equals("tertiary_text_dark.xml")) {
                try {
                    compileDummyAPK();
                } catch (Exception e) {
                    Log.d("CreateXMLFileException",
                            "Could not create Dummy APK (EXCEPTION)");
                }
            }
        }

        private void compileDummyAPK() throws Exception {

            Log.d("CompileDummyAPK", "Beginning to compile dummy APK...");

            // Create AndroidManifest.xml first, cutting down the assets file transfer!
            LayersFunc.createManifest(getActivity());

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
            Log.d("CompileDummyAPK",
                    "Successfully compiled dummy apk!");
            Log.d("Progress", "3");
            unzip();
        }

        public void unzip() {
            String source =
                    getActivity().getFilesDir().getAbsolutePath() + "/color-resources.apk";
            String destination =
                    getActivity().getFilesDir().getAbsolutePath() + "/color-resources/";
            String sourcesett =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res.zip";
            String destinationsett =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res";
            try {
                ZipFile zipFile = new ZipFile(source);
                ZipFile zipFilesett = new ZipFile(sourcesett);
                Log.d("Unzip", "The ZIP has been located and will now be unzipped...");
                Log.d("Progress", "4");
                zipFile.extractAll(destination);
                zipFilesett.extractAll(destinationsett);
                Log.d("Unzip",
                        "Successfully unzipped the file to the corresponding directory!");
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    performAAPTonCommonsAPK();
                } catch (Exception e) {
                    //
                }
            }

        }

        private void performAAPTonCommonsAPK() {
            try {
                Log.d("performAAPTonCommonsAPK",
                        "Mounting system as read-write as we prepare for some commands...");
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                Log.d("Progress", "5");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res");
                Log.d("Progress", "6");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/color");
                Log.d("Progress", "7");

                LayersFunc.LayersColorSwitch(getActivity(), File, "tertiary_text_dark", "color");
                LayersFunc.LayersSettingsSwitch(getActivity());
                LayersFunc.signApk(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/dashboard./settings/Akzent_Settings.apk",
                        Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/dashboard./settings/Akzent_Settings_signed.apk");
                Log.d("Progress", "8");

                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/color");
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                Log.d("Progress", "9");
                Log.d("performAAPTonCommonsAPK",
                        "Cleaned up root directory and remounted system as read-only.");

                // Finally, let's make sure the directories are pushed to the last command

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
