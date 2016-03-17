package projekt.dashboard.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    public boolean is_autorestart_enabled, is_hotreboot_enabled, is_debugging_mode_enabled;

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_colorpicker, container, false);

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
                            "This feature disables you from enabling both SystemUI restart and " +
                                    "Hot Reboot. Disable Hot Reboot to switch to SystemUI Restart",
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

        CheckBox debugmode = (CheckBox) inflation.findViewById(R.id.switch4);
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

        CardView akzent = (CardView) inflation.findViewById(R.id.akzent);
        akzent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.materialdark.donate")) {
                    String[] firstPhaseCommands = {
                            "/data/resource-cache/com.chummy.jezebel.materialdark.donate" +
                                    "/common/resources.apk"};
                    new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                    launchColorPicker("akzent",
                            "/data/resource-cache/com.chummy.jezebel.materialdark.donate" +
                                    "/common/resources.apk");
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


        CardView blakzent = (CardView) inflation.findViewById(R.id.blakzent);
        blakzent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.blackedout.donate")) {
                    String[] firstPhaseCommands = {
                            "/data/resource-cache/com.chummy.jezebel.blackedout.donate" +
                                    "/common/resources.apk"};
                    new firstPhaseAsyncTasks().execute(firstPhaseCommands);
                    launchColorPicker("blakzent",
                            "/data/resource-cache/com.chummy.jezebel.blackedout.donate" +
                                    "/common/resources.apk");
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

        CardView projektklar = (CardView) inflation.findViewById(R.id.projektklar);
        projektklar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing here yet :)
            }
        });

        return inflation;
    }

    public void launchColorPicker(String theme_name, String theme_dir) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        saved_color = settings.getString(theme_name, "0");

        if (saved_color != "0"){
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
                color_picked = ColorPickerPreference.convertToARGB(color);
                editor.putString(theme_name, color_picked).commit();
                String[] secondPhaseCommands = {theme_dir};
                new secondPhaseAsyncTasks().execute(secondPhaseCommands);
            }
        });
        cpd.show();
    }

    public void cleanTempFolder() {
        File dir = getActivity().getFilesDir();
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
        return R.string.home;
    }

    private class firstPhaseAsyncTasks extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String theme_dir = params[0];
            /*createTempFolder();*/ // We don't have anything in assets at this very moment
            copyCommonsFile(theme_dir);
            return null;
        }

        private void createTempFolder() {
            if (is_debugging_mode_enabled) Log.e("createTempFolder",
                    "Creating temporary folder....");
            copyAssetFolder(getActivity().getAssets(), "aapt",
                    getActivity().getFilesDir().getAbsolutePath());
        }


        private boolean copyAssetFolder(AssetManager assetManager,
                                        String fromAssetPath, String toPath) {
            try {
                String[] files = assetManager.list(fromAssetPath);
                new File(toPath).mkdirs();
                boolean res = true;
                for (String file : files) {
                    if (file.contains(".")) {
                        res &= copyAsset(assetManager,
                                fromAssetPath + "/" + file,
                                toPath + "/" + file);
                    } else {
                        res &= copyAssetFolder(assetManager,
                                fromAssetPath + "/" + file,
                                toPath + "/" + file);
                    }
                }
                if (is_debugging_mode_enabled) Log.e("CopyAssets",
                        "All assets were moved to the app's file directory.");
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                if (is_debugging_mode_enabled) Log.e("CopyAssets",
                        "Temporary folder creation failed (EXCEPTION).");
                return false;
            }
        }

        private boolean copyAsset(AssetManager assetManager,
                                  String fromAssetPath, String toPath) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(fromAssetPath);
                new File(toPath).createNewFile();
                out = new FileOutputStream(toPath);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                if (is_debugging_mode_enabled) Log.e("CopyAssets",
                        "Temporary folder creation failed (FILEEXCEPTION).");
                return false;
            }
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        private void copyCommonsFile(String theme_dir) {
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getFilesDir().getAbsolutePath() +
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
        }

        private void createXMLfile(String string, String theme_dir) {
            try {
                // Create the working directory
                File directory = new File(getActivity().getFilesDir(), "/res/color-v14/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // Create the files
                File root = new File(getActivity().getFilesDir(), "/res/color-v14/" + string);
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

            File manifest = new File(getActivity().getFilesDir(), "AndroidManifest.xml");
            if (!manifest.exists()) {
                manifest.createNewFile();
            }
            FileWriter fw = new FileWriter(manifest);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" + "\n");
            String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"common\"/>" + "\n");
            pw.write(xmlTags);
            pw.write(xmlRes1);
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
            if (is_debugging_mode_enabled) Log.e("CompileDummyAPK",
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
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color_dark.xml " +
                            "/res/color-v14/accent_color_dark.xml");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color_light.xml " +
                            "/res/color-v14/accent_color_light.xml");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/color-resources/res/color-v14/accent_color.xml " +
                            "/res/color-v14/accent_color.xml");

            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Successfully copied all modified accent XMLs into the root folder.");

            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Preparing for clean up on common-resources...");

            Process nativeApp1 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_dark.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted dark accent file!");
            nativeApp1.waitFor();
            Process nativeApp2 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_light.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted light accent file!");
            nativeApp2.waitFor();
            Process nativeApp3 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color.xml");
            if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
                    "Deleted main accent file!");
            nativeApp3.waitFor();

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_dark.xml");
            if (is_debugging_mode_enabled)
                Log.e("performAAPTonCommonsAPK", "Added freshly created dark accent file...");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/common-resources.apk res/color-v14/accent_color_light.xml");
            if (is_debugging_mode_enabled)
                Log.e("performAAPTonCommonsAPK", "Added freshly created light accent file...");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
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
                            getActivity().getFilesDir().getAbsolutePath() +
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
                eu.chainfire.libsuperuser.Shell.SU.run("setprop ctl.restart zygote");
            }
        }
    }
}