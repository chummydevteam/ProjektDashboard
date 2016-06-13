package projekt.dashboard.layers.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.FileUtils;
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
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import kellinwood.security.zipsigner.ZipSigner;
import projekt.dashboard.layers.R;


/**
 * Created by Aditya on 5/10/2016.
 */
public class LayersFunc {

    final static String PREFS_NAME = "MyPrefsFile";
    public static String vendor = "/system/vendor/overlay";
    public static String mount = "/system";
    public static boolean downloaded = true;
    public static String themeframework = "Nill";
    public static String themesystemui = "Nill";
    public static String framework = "Akzent_Framework";
    static Context context;
    static String link = "https://github.com/adityaakadynamo/GG/raw/master/aapt";
    static long vendorspace = 0;
    static String vendorspaceString = "0.00";
    static boolean failed;

    public LayersFunc(Context contextxyz) {
        context = contextxyz;
    }

    public static void DownloadFirstResources(final Context context) {
        changeVendorAndMount();
        findFrameworkFile();
        findSystemUIFile();
        StatFs st = new StatFs(getvendor());
        long vendorspace = (st.getAvailableBlocksLong() * st.getBlockSizeLong());
        vendorspaceString = calculatevendorspace(vendorspace);
        checkThemeMainSupported(context);
        File aa = new File("/system/bin/aapt");
        Log.e("AAPT Size",aa.length()+"");
        if (aa.exists()&&aa.length()==709092) {

        } else {
            if (isNetworkAvailable(context)) {
                Log.e("Switcher", "First time");
                Log.e("DownloadAAPT", "Calling Function");
                downloaded = true;
                downloadAAPT(context);
            } else {
                MaterialDialog md = new MaterialDialog.Builder(context)
                        .title("We Need to Download Some Resources")
                        .content("Please connect to the internet and meet us back here")
                        .positiveText("Open Settings")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                Intent intent_rrolayers = context.getPackageManager()
                                        .getLaunchIntentForPackage("com.android.settings");
                                context.startActivity(intent_rrolayers);
                                downloaded = false;
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                downloaded = false;
                            }
                        })
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                downloaded = false;
                            }
                        })
                        .show();
            }

        }
    }

    private static String calculatevendorspace(long total) {
        Log.e("Size", "Total" + total);
        return bytesToHuman(total);
    }

    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " Gb";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

        return "???";
    }

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    private static void changeVendorAndMount() {
        if (checkBitPhone()) {
            Log.e("PhoneBitCheck", Build.DEVICE + " found, now changing the vendor and mount");
            vendor = "/vendor/overlay";
            mount = "/vendor";
        } else {
            Log.e("PhoneBitCheck", Build.DEVICE + " found, now changing the vendor and mount");
            vendor = "/system/vendor/overlay";
            mount = "/system";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean checkBitPhone() {
        String[] bit = Build.SUPPORTED_32_BIT_ABIS;
        String[] bit64 = Build.SUPPORTED_64_BIT_ABIS;
        int flag = 0;
        try {
            if (bit64[0] != null) {
                Log.e("PhoneBitCheck", "64 bit device detected");
                if (Build.DEVICE.equals("flounder") || Build.DEVICE.equals("flounder_lte") ||
                        Build.DEVICE.equals("angler") || Build.DEVICE.equals("bullhead")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (flag == 0) {
                if (bit[0] != null) {
                    Log.e("PhoneBitCheck", "32 bit device detected");
                    Log.e("PhoneBitCheck", "Default overlay folder found");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void downloadAAPT(Context context) {
        if (checkBitPhone()) {
            Log.e("downloadAAPT", "64 bit device detected");
            Log.e("downloadAAPT", Build.DEVICE + " found, now changing vendor and mount zones");
            String[] downloadCommands = {link,
                    "aapt"};
            new downloadResources().execute(downloadCommands);
            Log.e("downloadAAPT", "Download complete");
        } else {
            Log.e("downloadAAPT", "32 bit device detected");
            Log.e("downloadAAPT", Build.DEVICE + " found, now changing vendor and mount zones");
            String[] downloadCommands = {link,
                    "aapt"};
            new downloadResources().execute(downloadCommands);
            Log.e("DownloadAAPT", "Download complete");
        }
    }

    public static String getvendor() {
        return vendor;
    }

    final public static boolean checkLayersInstalled(Context context) {

        if (isAppInstalled(context, "com.lovejoy777.rroandlayersmanager")) {
            return true;
        } else {
            return false;
        }
    }

    final public static boolean checkThemeSysSupported(Context context) {

        File f2 = new File(vendor, "Akzent_SystemUI.apk");
        if (f2.exists()) {
            return true;
        }
        return false;
    }

    final public static boolean checkThemeMainSupported(Context context) {

        try {
            String array[] = context.getResources().getStringArray(R.array.themes_supported);
            for (int i = 0; i < array.length; i++) {
                File f2 = new File(vendor + "/");
                File[] files2 = f2.listFiles();
                if (files2 != null) {
                    for (File inFile2 : files2) {
                        if (inFile2.isFile()) {
                            Log.d("Processing overlay", inFile2.toString());
                            String filenameParse[] = inFile2.getAbsolutePath().split("/");
                            String last = filenameParse[filenameParse.length - 1];
                            StringTokenizer stringTokenizer = new StringTokenizer(last, ".");
                            String finalname = stringTokenizer.nextToken();
                            if (finalname.equalsIgnoreCase(array[i])) {
                                Log.d("Supported overlay", finalname);
                                framework = array[i];
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void copyFileToApp(Context context, String theme_dir, String destination_dir) {
        Log.e("CopyFrameworkFile", "Function Called");
        Log.e("CopyFrameworkFile", "Function Started");
        String sourcePath = theme_dir;
        File source = new File(sourcePath);
        Log.e("Source", sourcePath);
        String destinationPath = context.getFilesDir().getAbsolutePath() + "/" + destination_dir;
        Log.e("Destination", destinationPath);
        Log.e("CopyFrameworkFile", "Function Started");
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

    public static void createXML(String string, Context context, String color_picked) {
        try {
            // Create the working directory
            File directory = new File(context.getFilesDir(), "/res/color/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Create the files
            File root = new File(context.getFilesDir(), "/res/color/" + string);
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
            String xmlRes3 = ("    <item android:state_enabled=\"false\" android:color=\"" +
                    color_picked + "\" />"
                    + "\n");
            String xmlRes4 = ("    <item android:state_window_focused=\"false\" android:color=\""
                    + color_picked + "\" />"
                    + "\n");
            String xmlRes5 = ("    <item android:state_pressed=\"true\" android:color=\"" +
                    color_picked + "\" />"
                    + "\n");
            String xmlRes6 = ("    <item android:state_selected=\"true\" android:color=\"" +
                    color_picked + "\" />"
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
            Log.e("CreateXMLFile",
                    string + " Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSettColXML(String string, String color_picked) {
        try {
            // Create the working directory
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Create the files
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/" + string);
            if (!root.exists()) {
                root.createNewFile();
            }
            FileWriter fw = new FileWriter(root);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String xmlRes1 = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<!--You are editing Settings' Colors-->\n" +
                    "\n" +
                    "<resources>\n" +
                    "    <!-- Edits the background in settings-->\n" +
                    "    <color name=\"dashboard_background_color\">@android:color/black</color>\n" +
                    "    <color name=\"dashboard_category_background_color\">@android:color/black</color>\n" +
                    "\n" +
                    "    <!--Toggle background color-->\n" +
                    "    <color name=\"switchbar_background_color\">@android:color/transparent</color>\n" +
                    "    <color name=\"switch_accent_color\">" + color_picked + "</color>\n" +
                    "\n" +
                    "    <!--Action Bar Colors-->\n" +
                    "    <color name=\"theme_primary\">#212121</color>\n" +
                    "    <color name=\"theme_primary_dark\">#212121</color>\n" +
                    "\n" +
                    "    <!--Title text color and toggle colors-->\n" +
                    "    <color name=\"theme_accent\">" + color_picked + "</color>\n" +
                    "\n" +
                    "    <!--QS Tile Cards inside Select and Order Tiles and Icon Color-->\n" +
                    "    <color name=\"cardview_light_background\">@android:color/black</color>\n" +
                    "    <color name=\"qs_tile_tint_color\">" + color_picked + "</color>\n" +
                    "\n" +
                    "    <!--Remainder coloring for Data Usage and Battery Usage-->\n" +
                    "    <color name=\"material_empty_color_light\">#80777777</color>\n" +
                    "\n" +
                    "    <!--Running Apps Percentage Bar-->\n" +
                    "    <color name=\"running_processes_system_ram\">#ffaba3ab</color>\n" +
                    "    <color name=\"running_processes_apps_ram\">@android:color/white</color>\n" +
                    "    <color name=\"running_processes_free_ram\">@android:color/transparent</color>\n" +
                    "\n" +
                    "    <!--Pattern Lock Color-->\n" +
                    "    <color name=\"lock_pattern_view_regular_color\">#ff777777</color>\n" +
                    "\n" +
                    "    <!--Increasing Ring Icon Disabled Color-->\n" +
                    "    <color name=\"audio_ringer_disabled_tint\">#4a777777</color>\n" +
                    "\n" +
                    "    <color name=\"card_background\">@android:color/black</color>\n" +
                    "</resources>\n");
            pw.write(xmlRes1);
            pw.close();
            bw.close();
            fw.close();
            Log.e("CreateXMLFile",
                    string + " Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSettStyXML(String string, String color_picked) {
        try {
            // Create the working directory
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Create the files
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/" + string);
            if (!root.exists()) {
                root.createNewFile();
            }
            FileWriter fw = new FileWriter(root);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String xmlRes1 = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n" +
                    "\n" +
                    "    <style name=\"TextAppearance.CategoryTitle\" parent=\"@android:style/TextAppearance.Material.Body2\">\n" +
                    "        <item name=\"android:textStyle\">bold</item>\n" +
                    "        <item name=\"android:textColor\">" + color_picked + "</item>\n" +
                    "        <item name=\"android:textAllCaps\">true</item>\n" +
                    "    </style>\n" +
                    "\n" +
                    "    <style name=\"Theme.ActionBar\" parent=\"@android:style/Widget.Material.ActionBar.Solid\">\n" +
                    "        <item name=\"android:background\">#212121</item>\n" +
                    "        <item name=\"android:elevation\">0.0dp</item>\n" +
                    "    </style>\n" +
                    "\n" +
                    "    <style name=\"Theme.AlertDialog\" parent=\"@android:style/Theme.Material.Dialog.Alert\" />\n" +
                    "\n" +
                    "    <style name=\"Theme.Light.WifiDialog\" parent=\"@android:style/Theme.Material.Dialog.Alert\" />\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "    <style name=\"Theme.SubSettings\" parent=\"@style/Theme.SettingsBase\">\n" +
                    "        <item name=\"android:windowBackground\">@android:color/black</item>\n" +
                    "        <!--- <item name=\"android:popupBackground\">@android:color/background_dark</item>\n" +
                    "         <item name=\"android:statusBarColor\">@android:color/holo_blue_light</item>\n" +
                    "         <item name=\"android:colorAccent\">@android:color/white</item>\n" +
                    "         <item name=\"android:colorPrimary\">@android:color/holo_blue_light</item>-->\n" +
                    "        <item name=\"android:navigationBarColor\">@android:color/black</item>\n" +
                    "    </style>\n" +
                    "\n" +
                    "    <style name=\"Theme.SettingsBase\" parent=\"@android:style/Theme.Material\">\n" +
                    "        <!---<item name=\"android:colorBackground\">@android:color/background_dark</item>\n" +
                    "        <item name=\"android:textColorPrimary\">@android:color/white</item>\n" +
                    "        <item name=\"android:textColorSecondary\">#ffe1e1e1</item> -->\n" +
                    "        <item name=\"android:windowBackground\">@android:color/background_dark</item>\n" +
                    "        <item name=\"android:navigationBarColor\">@android:color/black</item>\n" +
                    "        <item name=\"android:statusBarColor\">#212121</item>\n" +
                    "        <item name=\"android:colorAccent\">" + color_picked + "</item>\n" +
                    "    </style>\n" +
                    "\n" +
                    "    <style name=\"Theme.DialogWhenLarge\" parent=\"@android:style/Theme.Material.DialogWhenLarge\">\n" +
                    "        <item name=\"android:colorPrimary\">#212121</item>\n" +
                    "        <item name=\"android:colorPrimaryDark\">#212121</item>\n" +
                    "        <item name=\"android:colorAccent\">" + color_picked + "</item>\n" +
                    "    </style>\n" +
                    "</resources>");
            pw.write(xmlRes1);
            pw.close();
            bw.close();
            fw.close();
            Log.e("CreateXMLFile",
                    string + " Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSettDimXML(String string) {
        try {
            // Create the working directory
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Create the files
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res/values/" + string);
            if (!root.exists()) {
                root.createNewFile();
            }
            FileWriter fw = new FileWriter(root);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String xmlRes1 = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<!--You are editing Settings' Dimensions-->\n" +
                    "\n" +
                    "<resources>\n" +
                    "    <dimen name=\"actionbar_contentInsetStart\">16.0dip</dimen>\n" +
                    "</resources>");
            pw.write(xmlRes1);
            pw.close();
            bw.close();
            fw.close();
            Log.e("CreateXMLFile",
                    string + " Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createManifest(Context context) throws Exception {
        File manifest = new File(context.getFilesDir(), "AndroidManifest.xml");
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
        String xmlRes2 = ("<overlay android:targetPackage=\"android\" android:priority=\"100\"/>"
                + "\n");
        String xmlRes3 = ("</manifest>" + "\n");
        //  pw.write(xmlTags);
        pw.write(xmlRes1);
        pw.write(xmlRes2);
        pw.write(xmlRes3);
        pw.close();
        bw.close();
        fw.close();
    }

    public static void createSettManifest(Context context) throws Exception {
        File manifest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/AndroidManifest.xml");
        if (!manifest.exists()) {
            manifest.createNewFile();
        }
        FileWriter fw = new FileWriter(manifest);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        //String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" " +
        //          "standalone=\"no\"?>" + "\n");
        String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"azkzent.com.android.settings\"\n" +
                "    android:versionCode=\"1\"\n" +
                "    android:versionName=\"1.0\" >\n" +
                "    <uses-sdk android:minSdkVersion=\"21\"></uses-sdk>\n" +
                "<!--\n" +
                "Targeting the right packageName of the 'target.apk' here!  \n" +
                "We can find the real packageName in the AndroidManifest.xml of original target.apk!\n" +
                "So the matching resources id can be set in accordance.\n" +
                "-->\n" +
                "<overlay android:targetPackage=\"com.android.settings\" android:priority=\"30\"/>\n" +
                "\n" +
                "    <meta-data android:name=\"rom\" android:value=\"All\" />\n" +
                "    <meta-data android:name=\"infoShort\" android:value=\"Settings\" />\n" +
                "    <meta-data android:name=\"infoLong\" android:value=\"Settings to KLAR\" />\n" +
                "    <meta-data android:name=\"author\" android:value=\"adityagupta\" />\n" +
                "  \n" +
                "</manifest>");
        //  pw.write(xmlTags);
        pw.write(xmlRes1);
        pw.close();
        bw.close();
        fw.close();
    }

    public static void copyFinalizedAPK(Context context, String file, boolean files, ProgressDialog pd) {
        String mount = "mount -o remount,rw /";
        String mountsys = "mount -o remount,rw /system";
        String remount = "mount -o remount,ro /";
        String remountsys = "mount -o remount,ro /system";
        eu.chainfire.libsuperuser.Shell.SU.run(mount);
        eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
        File f;
        if (files) {
            f = new File(context.getFilesDir().getAbsolutePath(), file + ".apk");
        } else {
            f = new File(context.getCacheDir().getAbsolutePath(), file + ".apk");
        }
        Log.e("Size", bytesToHuman(f.length()));
        if (vendorspace >= f.length())

        {
            if (files) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getFilesDir().getAbsolutePath() +
                                "/" + file + ".apk " + "/system/vendor/overlay/" + file + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getFilesDir()
                                .getAbsolutePath() + " into " +
                                "/system/vendor/overlay/ and modified the permissions!");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getCacheDir().getAbsolutePath() +
                                "/" + file + ".apk " + "/system/vendor/overlay/" + file + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getCacheDir()
                                .getAbsolutePath() + " into " +
                                "/system/vendor/overlay/ and modified the permissions!");
            }
        } else

        {
            failed = true;
            pd.dismiss();
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle("Insufficient Space");
            ad.setMessage("Your Vendor does not has enough space to copy the overlay \nFree:- " +
                    vendorspaceString + "\nRequired:- " + bytesToHuman(f.length()));
            ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }

        eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/" + file +
                ".apk");
        eu.chainfire.libsuperuser.Shell.SU.run(remount);
        eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
        if (files)

        {
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                    ".layers/files");
        } else

        {
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                    ".layers/cache");
        }
        if (!failed) {
            pd.dismiss();
        }else{
            failed=false;
        }
        Log.e("copyFinalizedAPK",
                "Successfully Deleted Files ");

    }

    public static void copyFABFinalizedAPK(Context context, String file, boolean files, ProgressDialog pd) {
        String mount = "mount -o remount,rw /";
        String mountsys = "mount -o remount,rw /vendor";
        String remount = "mount -o remount,ro /";
        String remountsys = "mount -o remount,ro /vendor";
        eu.chainfire.libsuperuser.Shell.SU.run(mount);
        eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
        File f;
        if (files) {
            f = new File(context.getFilesDir().getAbsolutePath(), file + ".apk");
        } else {
            f = new File(context.getCacheDir().getAbsolutePath(), file + ".apk");
        }
        Log.e("Size", bytesToHuman(f.length()));
        if (vendorspace >= f.length()) {
            if (files) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getFilesDir().getAbsolutePath() +
                                "/" + file + ".apk " + "/vendor/overlay/" + file + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getFilesDir()
                                .getAbsolutePath() + " into " +
                                "/vendor/overlay/ and modified the permissions!");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                context.getCacheDir().getAbsolutePath() +
                                "/" + file + ".apk " + "/vendor/overlay/" + file + ".apk");
                Log.e("copyFinalizedAPK",
                        "Successfully copied the modified resource APK from " + context.getCacheDir()
                                .getAbsolutePath() + " into " +
                                "/vendor/overlay/ and modified the permissions!");
            }
        } else

        {
            failed = true;
            pd.dismiss();
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle("Insufficient Space");
            ad.setMessage("Your Vendor does not has enough space to copy the overlay \nFree:- " +
                    vendorspaceString + "\nRequired:- " + bytesToHuman(f.length()));
            ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }
        eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/" + file + ".apk");
        eu.chainfire.libsuperuser.Shell.SU.run(remount);
        eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
        Log.e("copyFinalizedAPK",
                "Successfully copied the modified resource APK into " +
                        "/vendor/overlay/ and modified the permissions!");
        if (files) {
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                    ".layers/files");
        } else {
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard" +
                    ".layers/cache");
        }
        if (!failed) {
            pd.dismiss();
        }else{
            failed=false;
        }
        Log.e("copyFinalizedAPK",
                "Successfully Deleted Files ");

    }

    public static void LayersColorSwitch(Context context, String Name, String file, String
            resource) throws Exception {
        eu.chainfire.libsuperuser.Shell.SU.run(
                "cp " + context.getFilesDir().getAbsolutePath() +
                        "/color-resources/res/" + resource + "/" + file + ".xml " +
                        "/res/" + resource + "/" + file + ".xml");
        Log.e("performAAPTonCommonsAPK",
                "Successfully copied all modified accent XMLs into the root folder.");

        Log.e("performAAPTonCommonsAPK",
                "Preparing for clean up on resources...");
        Process nativeApp3 = Runtime.getRuntime().exec(
                "aapt remove " +
                        context.getFilesDir().getAbsolutePath() +
                        "/" + Name + ".apk res/" + resource + "/" + file + ".xml");
        Log.e("performAAPTonCommonsAPK",
                "Deleted main " + file + " file!");
        nativeApp3.waitFor();
        eu.chainfire.libsuperuser.Shell.SU.run(
                "aapt add " +
                        context.getFilesDir().getAbsolutePath() +
                        "/" + Name + ".apk res/" + resource + "/" + file + ".xml");

        Log.e("performAAPTonCommonsAPK",
                "Added freshly created main " + file + " file...ALL DONE!");
    }

    public static void LayersSettingsSwitch(Context context) {
        eu.chainfire.libsuperuser.Shell.SU.run("aapt p -M " +
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/AndroidManifest.xml" +
                " -S " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/res -I /system/framework/framework-res.apk -F " +
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./settings/Akzent_Settings.apk");
    }

    public static void findFrameworkFile() {
        try {
            File f2 = new File(vendor + "/");
            File[] files2 = f2.listFiles();
            if (files2 != null) {
                for (File inFile2 : files2) {
                    if (inFile2.isFile()) {
                        Log.d("Processing overlay", inFile2.toString());
                        String filenameParse[] = inFile2.getAbsolutePath().split("/");
                        String last = filenameParse[filenameParse.length - 1];
                        StringTokenizer stringTokenizer = new StringTokenizer(last, ".");
                        String finalname = stringTokenizer.nextToken();
                        if (finalname.contains("HeaderSwapperFrame")) {
                            Log.d("Supported overlay", finalname);
                            themeframework = finalname;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static void findSystemUIFile() {
        try {
            File f2 = new File(vendor + "/");
            File[] files2 = f2.listFiles();
            if (files2 != null) {
                for (File inFile2 : files2) {
                    if (inFile2.isFile()) {
                        Log.d("Processing overlay", inFile2.toString());
                        String filenameParse[] = inFile2.getAbsolutePath().split("/");
                        String last = filenameParse[filenameParse.length - 1];
                        StringTokenizer stringTokenizer = new StringTokenizer(last, ".");
                        String finalname = stringTokenizer.nextToken();
                        if (finalname.contains("HeaderSwapperSys")) {
                            Log.d("Supported overlay", finalname);
                            themesystemui = finalname;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static void findthemingframework() {

    }

    public String getframework() {
        return framework;
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
                            context.getFilesDir().getAbsolutePath() +
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
                File myDir = new File(context.getFilesDir().getAbsolutePath());
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

    public static void signApk(String unsigned, String signed) {
        try {
            ZipSigner zipSigner = new ZipSigner();
            zipSigner.setKeymode("testkey");
            zipSigner.signZip(unsigned, signed);
            eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@Akzent_Settings.apk@idmap /data/resource-cache/vendor@overlay@Akzent_Settings.apk@idmap.bak");
            if (LayersFunc.checkBitPhone()) {
                String mountsys = "mount -o remount,rw /vendor";
                String remountsys = "mount -o remount,ro /vendor";
                eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
                eu.chainfire.libsuperuser.Shell.SU.run("cp " + signed + " /vendor/overlay/Akzent_Settings.apk");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/Akzent_Settings.apk");
                eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            } else {
                String mountsys = "mount -o remount,rw /system";
                String remountsys = "mount -o remount,ro /system";
                eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
                eu.chainfire.libsuperuser.Shell.SU.run("cp " + signed + " /system/vendor/overlay/Akzent_Settings.apk");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/Akzent_Settings.apk");
                eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            }
            eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@Akzent_Settings.apk@idmap.bak /data/resource-cache/vendor@overlay@Akzent_Settings.apk@idmap");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r " + Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/dashboard./settings/Akzent_Settings_signed.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r " + Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/dashboard./settings/Akzent_Settings.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r " + Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/dashboard./settings/res");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
