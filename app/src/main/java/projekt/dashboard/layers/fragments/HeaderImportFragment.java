package projekt.dashboard.layers.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import butterknife.ButterKnife;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.ui.HeaderPackDownloadActivity;
import projekt.dashboard.layers.util.ReadXMLFile;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HeaderImportFragment extends BasePageFragment {

    public ViewGroup inflation;
    public boolean is_zip_spinner_activated, is_theme_selected;
    public Spinner spinner, spinner1, spinner2;
    public String theme_dir, package_name;
    public FloatingActionButton apply_fab;
    public int counter = 0;
    public int folder_directory = 1;
    public int current_hour;
    public TextView currentTimeVariable;
    public SharedPreferences prefs;
    public String vendor = "/system/vendor/overlay";
    public String mount = "/system";

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

    public int countPNGs() {
        int count = 0;

        List<String> filenamePNGs = Arrays.asList(
                "notifhead_afternoon.png", "notifhead_christmas.png", "notifhead_morning.png",
                "notifhead_newyearseve.png", "notifhead_night.png", "notifhead_noon.png",
                "notifhead_sunrise.png", "notifhead_sunset_hdpi.png",
                "notifhead_sunset_xhdpi.png", "notifhead_sunset.png");

        File f2 = new File(
                getActivity().getCacheDir().getAbsolutePath() + "/headers/");
        File[] files2 = f2.listFiles();
        if (files2 != null) {
            for (File inFile2 : files2) {
                if (inFile2.isFile()) {
                    // Filter out filenames of which were unzipped earlier
                    String filenameParse[] = inFile2.getAbsolutePath().split("/");
                    String filename = filenameParse[filenameParse.length - 1];

                    if (filenamePNGs.contains(filename)) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    public void checkWhetherZIPisValid(String source, String destination) {
        try {
            net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(source);
            Log.e("Unzip", "The ZIP has been located and will now be unzipped...");
            zipFile.extractAll(destination);
            Log.e("Unzip",
                    "Successfully unzipped the file to the corresponding directory!");

            String[] checkerCommands = {destination + "/headers.xml"};
            String[] newArray = ReadXMLFile.main(checkerCommands);

            TextView headerPackName = (TextView) inflation.findViewById(R.id.themeName);
            headerPackName.setText(newArray[0]);

            TextView headerPackAuthor = (TextView) inflation.findViewById(R.id.themeAuthor);
            headerPackAuthor.setText(newArray[1]);

            TextView headerPackDevTeam = (TextView) inflation.findViewById(R.id.themeDevTeam);
            headerPackDevTeam.setText(newArray[2]);

            TextView headerPackVersion = (TextView) inflation.findViewById(R.id.themeVersion);
            headerPackVersion.setText(newArray[3]);

            TextView headerPackCount = (TextView) inflation.findViewById(R.id.themeCount);
            int how_many_themed = countPNGs();
            if (how_many_themed == 10) {
                headerPackCount.setText(getResources().getString(
                        R.string.contextualheaderimporter_all_themed));
            } else {
                if (how_many_themed == 1) {
                    headerPackCount.setText(
                            how_many_themed + " " + getResources().getString(
                                    R.string.contextualheaderimporter_only_one_themed));
                } else {
                    headerPackCount.setText(
                            how_many_themed + " " + getResources().getString(
                                    R.string.contextualheaderimporter_not_all_themed));
                }
            }

            cleanTempFolder();

            is_zip_spinner_activated = true;

            if (is_zip_spinner_activated && is_theme_selected) {
                apply_fab.show();
            } else {

            }

        } catch (ZipException e) {
            Log.e("Unzip",
                    "Failed to unzip the file the corresponding directory. (EXCEPTION)");
            e.printStackTrace();
            is_zip_spinner_activated = false;

            if (is_zip_spinner_activated && is_theme_selected) {
                apply_fab.show();
            } else {

            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_headersimporter, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Button downloadButton = (Button) inflation.findViewById(R.id.downloadButton);
        if (isNetworkAvailable()) {
            downloadButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), HeaderPackDownloadActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            downloadButton.setVisibility(View.GONE);
        }

        apply_fab = (FloatingActionButton) inflation.findViewById(R.id.apply_fab);
        if (prefs.getBoolean("blacked_out_enabled", true)) {
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_blacked_out)));
        } else {
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_dark_material)));
        }
        boolean phone = ColorChangerFragment.checkbitphone();
        if (phone == true) {
            Log.e("Main", "Found 64,Setting Vendor");
            vendor = "/vendor/overlay";
            mount = "/vendor";
        }
        apply_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] secondPhaseCommands = {
                        vendor + "/Akzent_SystemUI.apk",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/dashboard./" + spinner2.getSelectedItem().toString()};
                new secondPhaseAsyncTasks().execute(secondPhaseCommands);
            }
        });

        spinner1 = (Spinner) inflation.findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        List<String> list = new ArrayList<String>();

        list.add(getResources().getString(R.string.contextualheaderswapper_select_theme));
        list.add("dark material // akZent");
        list.add("blacked out // blakZent");

        // Now lets add all the located themes found that aren't cdt themes
        File f = new File("/data/resource-cache/");
        File[] files = f.listFiles();
        if (files != null) {
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    if (!inFile.getAbsolutePath().substring(21).equals(
                            "com.chummy.jezebel.blackedout.donate")) {
                        if (!inFile.getAbsolutePath().substring(21).equals(
                                "com.chummy.jezebel.materialdark.donate")) {
                            if (!inFile.getAbsolutePath().substring(21).equals("projekt.klar")) {
                                list.add(inFile.getAbsolutePath().substring(21));
                                counter += 1;
                            }
                        } else {
                            counter += 1;
                        }
                    } else {
                        counter += 1;
                    }
                }
            }
        }
        if (counter == 0) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    getResources().getString(
                            R.string.contextualheaderswapper_toast_cache_empty_reboot_first),
                    Toast.LENGTH_LONG);
            toast.show();
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set On Item Selected Listener
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos == 0) {
                    is_theme_selected = false;

                    if (is_zip_spinner_activated && is_theme_selected) {
                        apply_fab.show();
                    } else {

                    }
                }
                if (pos == 1) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.materialdark.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.materialdark.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.materialdark.donate";
                        is_theme_selected = true;

                        if (is_zip_spinner_activated && is_theme_selected) {
                            apply_fab.show();
                        } else {

                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(
                                        R.string.akzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                        is_theme_selected = false;

                        if (is_zip_spinner_activated && is_theme_selected) {
                            apply_fab.show();
                        } else {
                            spinner1.setSelection(0);
                        }
                    }
                }
                if (pos == 2) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.blackedout.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.blackedout.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.blackedout.donate";
                        is_theme_selected = true;

                        if (is_zip_spinner_activated && is_theme_selected) {
                            apply_fab.show();
                        } else {
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(
                                        R.string.blakzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                        spinner1.setSelection(0);
                        is_theme_selected = false;

                        if (is_zip_spinner_activated && is_theme_selected) {
                            apply_fab.show();
                        } else {
                            spinner1.setSelection(0);
                        }
                    }
                } else {
                    String packageIdentifier = spinner1.getSelectedItem().toString();
                    if (checkCurrentThemeSelection(packageIdentifier)) {
                        theme_dir = "/data/app/" + packageIdentifier + "-"
                                + folder_directory + "/base.apk";
                        package_name = packageIdentifier;
                        is_theme_selected = true;

                        if (is_zip_spinner_activated && is_theme_selected) {
                            apply_fab.show();
                        } else {
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);

        spinner2 = (Spinner) inflation.findViewById(R.id.zipSpinner);

        List<String> zipsFound = new ArrayList<String>();
        zipsFound.add(getResources().getString(R.string.contextualheaderswapper_select_zip));

        // Function that filters out all zip files within /storage/0/dashboard., but not only that,
        // it checks the zip file and sees if there is headers.xml found inside so that it's a
        // filter.

        File f2 = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./");
        File[] files2 = f2.listFiles();
        if (files2 != null) {
            for (File inFile2 : files2) {
                if (inFile2.isFile()) {
                    String filenameArray[] = inFile2.toString().split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    if (extension.equals("zip")) {
                        try {
                            String filenameParse[] = inFile2.getAbsolutePath().split("/");
                            String filename = filenameParse[filenameParse.length - 1];

                            ZipFile zipFile = new ZipFile(
                                    Environment.getExternalStorageDirectory().
                                            getAbsolutePath() + "/dashboard./" + filename);
                            ZipEntry entry = zipFile.getEntry("headers.xml");
                            if (entry != null) {
                                // headers.xml was found in the file, so add it into the spinner
                                zipsFound.add(filename);
                            }
                        } catch (IOException e) {
                            System.out.println(
                                    "There was an IOException within the filter function");
                        }
                    }
                }
            }
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, zipsFound);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos != 0) {
                    checkWhetherZIPisValid(Environment.getExternalStorageDirectory().
                                    getAbsolutePath() +
                                    "/dashboard./" + spinner2.getSelectedItem(),
                            getActivity().getCacheDir().getAbsolutePath() + "/headers");
                } else {
                    TextView headerPackName = (TextView)
                            inflation.findViewById(R.id.themeName);
                    headerPackName.setText(getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackAuthor = (TextView)
                            inflation.findViewById(R.id.themeAuthor);
                    headerPackAuthor.setText(getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackDevTeam = (TextView)
                            inflation.findViewById(R.id.themeDevTeam);
                    headerPackDevTeam.setText(getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackVersion = (TextView)
                            inflation.findViewById(R.id.themeVersion);
                    headerPackVersion.setText(getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackCount = (TextView)
                            inflation.findViewById(R.id.themeCount);
                    headerPackCount.setText(getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    is_zip_spinner_activated = false;

                    if (is_zip_spinner_activated && is_theme_selected) {
                        apply_fab.show();
                    } else {
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);

        return inflation;
    }

    public boolean checkCurrentThemeSelection(String packageName) {
        try {
            getContext().getPackageManager().getApplicationInfo(packageName, 0);
            File directory1 = new File("/data/app/" + packageName + "-1/base.apk");
            if (directory1.exists()) {
                folder_directory = 1;
                return true;
            } else {
                File directory2 = new File("/data/app/" + packageName + "-2/base.apk");
                if (directory2.exists()) {
                    folder_directory = 2;
                    return true;
                } else {
                    File directory3 = new File("/data/app/" + packageName + "-3/base.apk");
                    if (directory3.exists()) {
                        folder_directory = 3;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
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
        return R.string.contextualheaderimporter;
    }

    private class secondPhaseAsyncTasks extends AsyncTask<String, String, Void> {

        private ProgressDialog pd;

        @Override
        protected Void doInBackground(String... params) {
            String theme_dir = params[0];
            String header_zip = params[1];
            try {
                copyCommonsFile(theme_dir, header_zip);
            } catch (Exception e) {
                Log.e("performAAPTonCommonsAPK",
                        "Caught the exception.");
            }
            return null;
        }

        private void copyCommonsFile(String theme_dir, String header_zip) {
            Log.e("CopyAkzent_SystemUIFile", "Function Called");
            Log.e("CopyAkzent_SystemUIFile", "Function Started");
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getCacheDir().getAbsolutePath() +
                    "/Akzent_SystemUI.apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                unzip(header_zip);
                Log.e("CopyAkzent_SystemUIFile",
                        "Successfully copied Akzent_SystemUI apk from overlays folder to work directory");
                Log.e("CopyAkzent_SystemUIFile", "Function Stopped");
            } catch (IOException e) {
                Log.e("CopyAkzent_SystemUIFile",
                        "Failed to copy Akzent_SystemUI apk from resource-cache to work directory");
                Log.e("CopyAkzent_SystemUIFile", "Function Stopped");
                e.printStackTrace();
            }
        }

        public void unzip(String source) {
            try {
                String destination = getActivity().getCacheDir().getAbsolutePath() + "/headers/";

                net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(source);
                Log.e("Unzip", "The ZIP has been located and will now be unzipped...");
                zipFile.extractAll(destination);
                Log.e("Unzip",
                        "Successfully unzipped the file to the corresponding directory!");
                performAAPTonCommonsAPK(processor());
            } catch (ZipException e) {
                Log.e("Unzip",
                        "Failed to unzip the file the corresponding directory. (EXCEPTION)");
                e.printStackTrace();
            }
        }

        public List processor() {
            List<String> filenamePNGs = Arrays.asList(
                    "notifhead_afternoon.png", "notifhead_christmas.png", "notifhead_morning.png",
                    "notifhead_newyearseve.png", "notifhead_night.png", "notifhead_noon.png",
                    "notifhead_sunrise.png", "notifhead_sunset_hdpi.png",
                    "notifhead_sunset_xhdpi.png", "notifhead_sunset.png");

            List<String> list = new ArrayList<String>();

            File f2 = new File(
                    getActivity().getCacheDir().getAbsolutePath() + "/headers/");
            File[] files2 = f2.listFiles();
            if (files2 != null) {
                for (File inFile2 : files2) {
                    if (inFile2.isFile()) {
                        // Filter out filenames of which were unzipped earlier
                        String filenameParse[] = inFile2.getAbsolutePath().split("/");
                        String filename = filenameParse[filenameParse.length - 1];

                        if (filenamePNGs.contains(filename)) {
                            list.add(filename);
                        }
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(list.get(i));
                }
                return list;
            }
            return null;
        }

        public void performAAPTonCommonsAPK(List source) {

            // Create the res/drawable-xxhdpi-v23 directory

            Log.e("postProcess",
                    "Mounting system as read-write as we prepare for some commands...");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xxhdpi-v4/");

            // Copy the files over
            for (int i = 0; i < source.size(); i++) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/headers/" + source.get(i) +
                                " res/drawable-xxhdpi-v4/" +
                                source.get(i));
                try {
                    Process nativeApp2 = Runtime.getRuntime().exec(
                            "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/Akzent_SystemUI.apk " +
                                    "res/drawable-xxhdpi-v4/" +
                                    source.get(i));
                    nativeApp2.waitFor();
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/Akzent_SystemUI.apk " +
                                    "res/drawable-xxhdpi-v4/" +
                                    source.get(i));

                } catch (Exception e) {
                    //
                }
            }

            Log.e("performAAPTonCommonsAPK",
                    "Successfully performed all AAPT commands.");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xxhdpi-v4/");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
            if (ColorChangerFragment.checkbitphone()) {
                copyFABFinalizedAPK();
            } else {
                copyFinalizedAPK();
            }
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
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/Akzent_SystemUI.apk " + "/system/vendor/overlay/Akzent_SystemUI.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/Akzent_SystemUI.apk");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard.layers/cache");
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
                            getActivity().getCacheDir().getAbsolutePath() +
                            "/Akzent_SystemUI.apk " + "/vendor/overlay/Akzent_SystemUI.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/Akzent_SystemUI.apk");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard.layers/cache");
            Log.e("copyFinalizedAPK",
                    "Successfully Deleted Files ");

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
    }

}
