package projekt.dashboard.layers.fragments;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.apache.commons.io.IOUtils;

import java.util.Random;

import butterknife.ButterKnife;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.colorpicker.ColorPickerDialog;
import projekt.dashboard.layers.colorpicker.ColorPickerPreference;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.util.LayersFunc;


/**
 * @author Nicholas Chum (nicholaschum)
 */

public class ColorChangerFragment extends BasePageFragment {

    static String File = "Akzent_Framework";
    public String color_picked = "#ff0000";
    public ViewGroup inflation;
    SharedPreferences prefs;
    FloatingActionButton fab;
    ImageButton imageButton;
    TextView accentcolor;

    public static String getFile() {
        return File;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_colorpicker, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                Log.e("colorswatch", "Calling Function");
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color_picked)));
                colorswatch();
            }
        });

        return inflation;
    }

    public void colorswatch() {
        Log.e("colorswatch", "Function Called");
        Log.e("colorswatch", "Function Started");
        String[] location = {LayersFunc.getvendor(), File + ".apk"};
        Log.e("FirstSyncTasks", "Calling Function");
        new copyThemeFiles().execute(location);
        Log.e("PickColors", "Calling Function");
        pickColor(LayersFunc.getvendor() + "/" + File + ".apk");
        Log.e("colorswatch", "Function Stopped");
    }

    public void pickColor(final String directory) {
        Log.e("PickColors", "Function Called");
        Log.e("PickColors", "Function Started");
        Log.e("SecondPhaseTasks", "Calling Function");
        new secondPhaseAsyncTasks().execute(directory);
        Log.e("PickColors", "Function Stopped");

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

    public class copyThemeFiles extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.e("copythemeFiles", "Calling Function");
            String theme_dir = params[0] + "/" + params[1];
            Log.e("copythemeFiles", theme_dir);
            Log.e("copythemeFiles", params[1]);
            LayersFunc.copyFileToApp(getActivity(), theme_dir, params[1]);
            return null;
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
            if (LayersFunc.checkbitphone()) {
                LayersFunc.copyFABFinalizedAPK(getActivity(), File, true);
            } else {
                LayersFunc.copyFinalizedAPK(getActivity(), File, true);
            }
            pd.dismiss();
            eu.chainfire.libsuperuser.Shell.SU.run("busybox killall com.android.systemui");
            Log.e("SecondPhaseTasks", "Function Stopped");
        }

        private void createXMLfile(String string, String theme_dir) {

            LayersFunc.createXML(string, getActivity(), color_picked);
            if (string.equals("tertiary_text_dark.xml")) {
                try {
                    compileDummyAPK();
                } catch (Exception e) {
                    Log.e("CreateXMLFileException",
                            "Could not create Dummy APK (EXCEPTION)");
                }
            }
        }

        private void compileDummyAPK() throws Exception {

            Log.e("CompileDummyAPK", "Beginning to compile dummy APK...");

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
            Log.e("CompileDummyAPK",
                    "Successfully compiled dummy apk!");
            unzip();
        }

        public void unzip() {
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
                    performAAPTonCommonsAPK();
                } catch (Exception e) {
                    //
                }
            }
        }

        private void performAAPTonCommonsAPK() {
            try {
                Log.e("performAAPTonCommonsAPK",
                        "Mounting system as read-write as we prepare for some commands...");
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/color");

                LayersFunc.LayersColorSwitch(getActivity(), File, "tertiary_text_dark", "color");

                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/color");
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                Log.e("performAAPTonCommonsAPK",
                        "Cleaned up root directory and remounted system as read-only.");

                // Finally, let's make sure the directories are pushed to the last command

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
