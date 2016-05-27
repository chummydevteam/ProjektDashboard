package projekt.dashboard.layers.fragments;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
import projekt.dashboard.layers.util.LayersFunc;
import projekt.dashboard.layers.util.ReadXMLFile;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HeaderImportFragment extends BasePageFragment {

    public ViewGroup inflation;
    public boolean is_zip_spinner_activated, is_radio_selected;
    public Spinner spinner, spinner2;
    public String theme_dir, package_name;
    public FloatingActionButton apply_fab;
    public int counter = 0;
    public int folder_directory = 1;
    public int current_hour;
    public TextView currentTimeVariable;
    public SharedPreferences prefs;
    public boolean xhdpi = false;
    public boolean xxhdpi = true;
    public boolean xxxhdpi = false;

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

            if (is_zip_spinner_activated && is_radio_selected) {
                apply_fab.show();
            } else {
                apply_fab.hide();
            }

        } catch (ZipException e) {
            Log.e("Unzip",
                    "Failed to unzip the file the corresponding directory. (EXCEPTION)");
            e.printStackTrace();
            is_zip_spinner_activated = false;

            if (is_zip_spinner_activated && is_radio_selected) {
                apply_fab.show();
            } else {
                apply_fab.hide();
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

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.getBoolean("dialog", true)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Header Importer :)");
            ad.setMessage("Woah Welcome to Header Importer,a place where you can actually use " +
                    "your favourite moments,your memories, right next to your notifications.\n" +
                    "So How to use it:-\n1. Easy AF,Just Select and Download a Header Pack from " +
                    "the Online DataBase\n2. Select the zip \n3. Select a Device DPI\n4. Click " +
                    "The Floating Button and Wait for the Magic !!!");
            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com.chummy" +
                            ".aditya.materialdark.layers.donate")) {
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
                    prefs.edit().putBoolean("dialog", false).commit();
                }
            });
        }

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

        RadioGroup radioGroup = (RadioGroup) inflation.findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio:
                        xhdpi = true;
                        xxhdpi = false;
                        xxxhdpi = false;
                        Log.e("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        is_radio_selected = true;
                        break;
                    case R.id.radio2:
                        xhdpi = false;
                        xxhdpi = true;
                        xxxhdpi = false;
                        Log.e("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        is_radio_selected = true;
                        break;
                    case R.id.radio3:
                        xhdpi = false;
                        xxhdpi = false;
                        xxxhdpi = true;
                        Log.e("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        is_radio_selected = true;
                        break;
                }
                if (is_zip_spinner_activated && is_radio_selected) {
                    apply_fab.show();
                } else {
                    apply_fab.hide();
                }
            }
        });

        apply_fab = (FloatingActionButton) inflation.findViewById(R.id.apply_fab);
        if (prefs.getBoolean("blacked_out_enabled", true)) {
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_blacked_out)));
        } else {
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_dark_material)));
        }
        apply_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] secondPhaseCommands = {
                        LayersFunc.getvendor() + "/" + LayersFunc.themesystemui + ".apk",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/dashboard./" + spinner2.getSelectedItem().toString()};
                new secondPhaseAsyncTasks().execute(secondPhaseCommands);
            }
        });

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
                    is_zip_spinner_activated = true;
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

                    if (is_zip_spinner_activated && is_radio_selected) {
                        apply_fab.show();
                    } else {
                        apply_fab.hide();
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
                    "/" + LayersFunc.themesystemui + ".apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                unzip(header_zip);
                Log.e("CopyAkzent_SystemUIFile",
                        "Successfully copied " + LayersFunc.themesystemui + " apk from overlays " +
                                "folder to work directory");
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
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xhdpi-v4/");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xxxhdpi-v4/");


            // Copy the files over
            for (int i = 0; i < source.size(); i++) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/headers/" + source.get(i) +
                                " res/drawable-xxhdpi-v4/" +
                                source.get(i));
                Log.e("xxhdpi cp", source.get(i) + "");
                try {
                    Process nativeApp2 = Runtime.getRuntime().exec(
                            "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/" + LayersFunc.themesystemui + ".apk " +
                                    "res/drawable-xxhdpi-v4/" +
                                    source.get(i) + "");
                    Log.e("xxhdpi rm", source.get(i) + "");
                    nativeApp2.waitFor();
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/" + LayersFunc.themesystemui + ".apk " +
                                    "res/drawable-xxhdpi-v4/" +
                                    source.get(i));
                    Log.e("xxhdpi ad", source.get(i) + "");

                } catch (Exception e) {
                    //
                }
            }
            if (xhdpi) {
                for (int i = 0; i < source.size(); i++) {
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/headers/" + source.get(i) +
                                    " res/drawable-xhdpi-v4/" +
                                    source.get(i));
                    Log.e("xhdpi cp", source.get(i) + "");
                    try {
                        Process nativeApp2 = Runtime.getRuntime().exec(
                                "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                        "/" + LayersFunc.themesystemui + ".apk " +
                                        "res/drawable-xhdpi-v4/" +
                                        source.get(i));
                        Log.e("xhdpi rm", source.get(i) + "");
                        nativeApp2.waitFor();
                        eu.chainfire.libsuperuser.Shell.SU.run(
                                "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                        "/" + LayersFunc.themesystemui + ".apk " +
                                        "res/drawable-xhdpi-v4/" +
                                        source.get(i));
                        Log.e("xhdpi ad", source.get(i) + "");

                    } catch (Exception e) {
                        //
                    }
                }
            }
            if (xxxhdpi) {
                for (int i = 0; i < source.size(); i++) {
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                    "/headers/" + source.get(i) +
                                    " res/drawable-xxxhdpi-v4/" +
                                    source.get(i));
                    Log.e("xxxhdpi cp", source.get(i) + "");
                    try {
                        Process nativeApp2 = Runtime.getRuntime().exec(
                                "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                        "/" + LayersFunc.themesystemui + ".apk " +
                                        "res/drawable-xxxhdpi-v4/" +
                                        source.get(i));
                        Log.e("xxxhdpi rm", source.get(i) + "");
                        nativeApp2.waitFor();
                        eu.chainfire.libsuperuser.Shell.SU.run(
                                "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                        "/" + LayersFunc.themesystemui + ".apk " +
                                        "res/drawable-xxxhdpi-v4/" +
                                        source.get(i));
                        Log.e("xxxhdpi ad", source.get(i) + "");

                    } catch (Exception e) {
                        //
                    }
                }
            }
            Log.e("performAAPTonCommonsAPK",
                    "Successfully performed all AAPT commands.");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xxhdpi-v4/");

            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xxxhdpi-v4/");

            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xhdpi-v4/");

            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
            if (LayersFunc.checkBitPhone()) {
                LayersFunc.copyFABFinalizedAPK(getActivity(), LayersFunc.themesystemui, false);
            } else {
                LayersFunc.copyFinalizedAPK(getActivity(), LayersFunc.themesystemui, false);
            }
            eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");

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
            eu.chainfire.libsuperuser.Shell.SU.run("busybox killall com.android.systemui");
        }
    }

}