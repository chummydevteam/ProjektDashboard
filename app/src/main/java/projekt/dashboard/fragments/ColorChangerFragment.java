package projekt.dashboard.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.azeesoft.lib.colorpicker.ColorPickerDialog;

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

import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;


/**
 * @author Nicholas Chum (nicholaschum)
 */
public class ColorChangerFragment extends BasePageFragment {


    public String color_picked;
    public boolean is_autorestart_enabled;

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean copyAssetFolder(AssetManager assetManager,
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
            Log.e("CopyAssets", "All assets were moved to the app's file directory.");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CopyAssets", "Temporary folder creation failed (EXCEPTION).");
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
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
            Log.e("CopyAssets", "Temporary folder creation failed (FILEEXCEPTION).");
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup inflation = (ViewGroup) inflater.inflate(R.layout.fragment_colorpicker, container, false);

        Switch autorestartSystemUI = (Switch) inflation.findViewById(R.id.switch1);
        autorestartSystemUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_autorestart_enabled = true;
                    Log.e("Switch", "Universal variable to auto restart ENABLED.");
                } else {
                    is_autorestart_enabled = false;
                    Log.e("Switch", "Universal variable to auto restart DISABLED.");
                }
            }
        });

        CardView akzent = (CardView) inflation.findViewById(R.id.akzent);
        akzent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled(getActivity(), "com.chummy.jezebel.materialdark.donate")) {
                    createTempFolder(); // Create the temp folder
                    copyCommonsFile("/data/resource-cache/com.chummy.jezebel.materialdark.donate/common/resources.apk", "akzent"); // Then take the common resource file out

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
                    createTempFolder(); // Create the temp folder
                    copyCommonsFile("/data/resource-cache/com.chummy.jezebel.blackedout.donate/common/resources.apk", "blakzent"); // Then take the common resource file first

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

        return inflation;
    }

    public void launchColorPicker(final String theme_name) {
        final ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getActivity(), ColorPickerDialog.DARK_THEME);
        colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#ffffff")); // Keep this white so that the hex colors aren't yellow
        colorPickerDialog.hideOpacityBar(); // Disable alpha because accents shouldn't be transparent
        colorPickerDialog.setOnClosedListener(new ColorPickerDialog.OnClosedListener() {
            @Override
            public void onClosed() {
                cleanTempFolder();
            }
        });
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                color_picked = colorPickerDialog.getCurrentColorAsHexa();
                createXMLfile("accent_color_dark.xml", theme_name);
                createXMLfile("accent_color_light.xml", theme_name);
                createXMLfile("accent_color.xml", theme_name);
            }
        });
        colorPickerDialog.show();
    }

    public void copyCommonsFile(String string, String theme_name) {
        String sourcePath = string;
        File source = new File(sourcePath);
        String destinationPath = getActivity().getFilesDir().getAbsolutePath() + "/common-resources.apk";
        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            Log.e("Start up", "Failed to copy commons apk from resource-cache to work directory");
            e.printStackTrace();
        } finally {
            Log.e("Start up", "Successfully copied commons apk from resource-cache to work directory");
            Log.e("Start up", "Launching color picker...");
            launchColorPicker(theme_name); // Afterwards, launch the color picker
        }
    }

    private void compileDummyAPK(String theme_name) throws Exception {
        Log.e("CompileDummyAPK", "Beginning to compile dummy APK...");
        String commands = new String("aapt p -M /data/data/projekt.dashboard/files/AndroidManifest.xml -S /data/data/projekt.dashboard/files/res/ -I /data/data/projekt.dashboard/files/builder.jar -F /data/data/projekt.dashboard/files/color-resources.apk\n");
        Process nativeApp = Runtime.getRuntime().exec(commands);
        IOUtils.toString(nativeApp.getInputStream());
        IOUtils.toString(nativeApp.getErrorStream());
        nativeApp.waitFor();
        Log.e("CompileDummyAPK", "Successfully compiled dummy apk!");

        if (theme_name == "akzent")
            unzip("akzent");
        if (theme_name == "blakzent")
            unzip("blakzent");
    }

    public void unzip(String theme_name) {
        String source = "/data/data/projekt.dashboard/files/color-resources.apk";
        String destination = "/data/data/projekt.dashboard/files/color-resources";
        String password = "password";

        try {
            ZipFile zipFile = new ZipFile(source);
            Log.e("Unzip", "The ZIP has been located and will now be unzipped...");
            if (zipFile.isEncrypted()) {
                Log.e("Unzip", "Just for your information, the ZIP is encrypted, attempting to use default password...");
                zipFile.setPassword(password);
            }
            Log.e("Unzip", "Great! The ZIP is ready to be opened so we can continue...");
            zipFile.extractAll(destination);
            Log.e("Unzip", "Successfully unzipped the file to the corresponding directory!");
        } catch (ZipException e) {
            Log.e("Unzip", "Failed to unzip the file the corresponding directory. (EXCEPTION)");
            e.printStackTrace();
        } finally {
            try {
                if (theme_name == "akzent")
                    performAAPTonCommonsAPK("akzent");
                if (theme_name == "blakzent")
                    performAAPTonCommonsAPK("blakzent");
            } catch (Exception e) {
                //
            }
        }
    }

    private void performAAPTonCommonsAPK(String theme_name) throws Exception {
        Log.e("performAAPTonCommonsAPK", "Mounting system as read-write as we prepare for some commands...");
        eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
        eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/color-v14");
        eu.chainfire.libsuperuser.Shell.SU.run("cp " + getActivity().getFilesDir().getAbsolutePath() + "/color-resources/res/color-v14/accent_color_dark.xml /res/color-v14/accent_color_dark.xml");
        eu.chainfire.libsuperuser.Shell.SU.run("cp " + getActivity().getFilesDir().getAbsolutePath() + "/color-resources/res/color-v14/accent_color_light.xml /res/color-v14/accent_color_light.xml");
        eu.chainfire.libsuperuser.Shell.SU.run("cp " + getActivity().getFilesDir().getAbsolutePath() + "/color-resources/res/color-v14/accent_color.xml /res/color-v14/accent_color.xml");

        Log.e("performAAPTonCommonsAPK", "Successfully copied all modified accent XMLs into the root folder.");

        String commands1 = new String("aapt remove /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color_dark.xml");
        String commands2 = new String("aapt remove /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color_light.xml");
        String commands3 = new String("aapt remove /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color.xml");

        Log.e("performAAPTonCommonsAPK", "Preparing for clean up on common-resources...");

        Process nativeApp1 = Runtime.getRuntime().exec(commands1);
        Log.e("performAAPTonCommonsAPK", "Deleted dark accent file!");
        nativeApp1.waitFor();
        Process nativeApp2 = Runtime.getRuntime().exec(commands2);
        Log.e("performAAPTonCommonsAPK", "Deleted light accent file!");
        nativeApp2.waitFor();
        Process nativeApp3 = Runtime.getRuntime().exec(commands3);
        Log.e("performAAPTonCommonsAPK", "Deleted main accent file!");
        nativeApp3.waitFor();

        eu.chainfire.libsuperuser.Shell.SU.run("aapt add /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color_dark.xml");
        Log.e("performAAPTonCommonsAPK", "Added freshly created dark accent file...");
        eu.chainfire.libsuperuser.Shell.SU.run("aapt add /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color_light.xml");
        Log.e("performAAPTonCommonsAPK", "Added freshly created light accent file...");
        eu.chainfire.libsuperuser.Shell.SU.run("aapt add /data/data/projekt.dashboard/files/common-resources.apk res/color-v14/accent_color.xml");
        Log.e("performAAPTonCommonsAPK", "Added freshly created main accent file...ALL DONE!");

        eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/color-v14");
        eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
        Log.e("performAAPTonCommonsAPK", "Cleaned up root directory and remounted system as read-only.");

        // Finally, let's make sure the directories are pushed to the last command
        if (theme_name == "akzent")
            copyFinalizedAPK("/data/resource-cache/com.chummy.jezebel.materialdark.donate/common/resources.apk");
        if (theme_name == "blakzent")
            copyFinalizedAPK("/data/resource-cache/com.chummy.jezebel.blackedout.donate/common/resources.apk");
    }

    public void copyFinalizedAPK(String directory) {
        eu.chainfire.libsuperuser.Shell.SU.run("cp /data/data/projekt.dashboard/files/common-resources.apk " + directory);
        eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + directory);
        Log.e("copyFinalizedAPK", "Successfully copied the modified resource APK into /data/resource-cache and modified the permissions!");
        cleanTempFolder();
        Log.e("cleanTempFolder", "Successfully cleaned up the whole work area!");

        if (is_autorestart_enabled){
            eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.systemui");
            eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.settings");
        }
    }

    private void createTempFolder() {
        Log.e("createTempFolder", "Creating temporary folder....");
        copyAssetFolder(getActivity().getAssets(), "aapt", getActivity().getFilesDir().getAbsolutePath());
    }

    private void createXMLfile(String string, String theme_name) {
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
            String xmlRes2 = ("  xmlns:android=\"http://schemas.android.com/apk/res/android\">" + "\n");
            String xmlRes3 = ("    <item android:color=" + "\"" + color_picked + "\"" + " />" + "\n");
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
                    if (theme_name == "akzent")
                        compileDummyAPK("akzent");
                    if (theme_name == "blakzent")
                        compileDummyAPK("blakzent");
                } catch (Exception e) {
                    Log.e("CreateXMLFileException", "Could not create Dummy APK (EXCEPTION)");
                }
            }
        } catch (IOException e) {
            Log.e("CreateXMLFileException", "Failed to create new file (IOEXCEPTION).");
        }

    }

    private void cleanTempFolder() {
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


}