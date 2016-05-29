package projekt.dashboard.layers.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.util.LayersFunc;

/**
 * @author Adityata
 */
public class HeaderSwapperFragment extends BasePageFragment {

    private static int RESULT_LOAD_IMAGE = 1;
    public ViewGroup inflation;
    public boolean is_all_selected, is_picture_selected, are_we_clearing_Files_after,
            free_crop_mode, swap_contextual_header, is_radio_selected;
    public CropImageView cropImageView;
    public ImageView croppedImageView;
    public Bitmap croppedBitmap;
    public Spinner spinner, spinner1;
    public String theme_dir, package_name;
    public FloatingActionButton apply_fab;
    public Button saveButton;
    public int folder_directory = 1;
    public int current_hour;
    public TextView checkBoxInstructions, currentTimeVariable;
    public CheckBox freeCropMode, swapcontext;
    public SharedPreferences prefs;
    public boolean xhdpi = false;
    public boolean xxhdpi = true;
    public boolean xxxhdpi = false;
    boolean log = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_headerswapper, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.getBoolean("dialog", true)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Header Swapper :)");
            ad.setMessage("Woah Welcome to Header Swapper,a place where you can actually use your" +
                    " favourite moments,your memories, right next to your notifications.\nSo How " +
                    "to use it:-\n1. Lets Click on the Click on the Floating button\n2. And yes " +
                    "if your ROM supports contextual headers you can always click on swap " +
                    "contextual headers and swap them too.\n3. OK, So Now After clicking the " +
                    "Floating Button just choose and crop the image.\n4. Now click on Save Button" +
                    " and Let the Magic Begin.\n\nIMP :- The Phone Should Automatically " +
                    "Softreboot to make changes");
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
                startActivityForResult(getImageSelectionIntent(), RESULT_LOAD_IMAGE);
            }
        });

        freeCropMode = (CheckBox) inflation.findViewById(R.id.checkBox2);
        freeCropMode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            free_crop_mode = true;
                            Log.d("CheckBox",
                                    "Free crop mode for Image Cropper has been ENABLED.");
                        } else {
                            free_crop_mode = false;
                            Log.d("CheckBox",
                                    "Free crop mode for Image Cropper has been DISABLED.");
                        }
                    }
                });

        final LinearLayout ln = (LinearLayout) inflation.findViewById(R.id.dpi);
        ln.setVisibility(View.GONE);

        swapcontext = (CheckBox) inflation.findViewById(R.id.checkBox3);
        swapcontext.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            swap_contextual_header = true;
                            apply_fab.hide();
                            ln.setVisibility(View.VISIBLE);
                            Log.d("CheckBox", "Universal variable to advanced log ENABLED.");
                        } else {
                            swap_contextual_header = false;
                            apply_fab.show();
                            ln.setVisibility(View.GONE);
                            Log.d("CheckBox", "Universal variable to advanced log DISABLED.");
                        }
                    }
                });
        RadioGroup radioGroup = (RadioGroup) inflation.findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio:
                        xhdpi = true;
                        xxhdpi = false;
                        xxxhdpi = false;
                        is_radio_selected = true;
                        Log.d("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        break;
                    case R.id.radio2:
                        xhdpi = false;
                        xxhdpi = true;
                        xxxhdpi = false;
                        is_radio_selected = true;
                        Log.d("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        break;
                    case R.id.radio3:
                        xhdpi = false;
                        xxhdpi = false;
                        xxxhdpi = true;
                        is_radio_selected = true;
                        Log.d("Devices Selected", xhdpi + " ," + xxhdpi + " ," + xxxhdpi);
                        break;
                }
                if (swap_contextual_header && is_radio_selected) {
                    apply_fab.show();
                } else {
                    apply_fab.hide();
                }
            }
        });
        checkBoxInstructions = (TextView) inflation.findViewById(R.id.textView2);
        saveButton = (Button) inflation.findViewById(R.id.save_button);

        return inflation;
    }

    public void changeFABaction() {
        if (!is_picture_selected) {
            apply_fab.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_photo_library_24dp));
            if (prefs.getBoolean("blacked_out_enabled", true)) {
                apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.primary_1_blacked_out)));
            } else {
                apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.primary_1_dark_material)));
            }
            apply_fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivityForResult(getImageSelectionIntent(), RESULT_LOAD_IMAGE);
                    is_picture_selected = true;
                }
            });
        } else {
            apply_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_cached_24dp));
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.resetButton)));
            apply_fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resetImageViews();
                }
            });
        }
    }

    private Intent getImageSelectionIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return intent;
    }

    public void letsGetStarted() {
        String[] secondPhaseCommands = {LayersFunc.vendor + "/" + LayersFunc.themeframework + "" +
                ".apk"};
        String sourcePath = LayersFunc.vendor + "/" + LayersFunc.themeframework + "" +
                ".apk";
        File source = new File(sourcePath);
        String destinationPath = getActivity().getFilesDir().getAbsolutePath() +
                "/" + LayersFunc.themeframework + ".apk";
        File destination = new File(destinationPath);

        String sourcePathsys = LayersFunc.getvendor() + "/" + LayersFunc.themesystemui + ".apk";
        File sourcesys = new File(sourcePathsys);
        String destinationPathsys = getActivity().getFilesDir().getAbsolutePath() +
                "/" + LayersFunc.themesystemui + ".apk";
        File destinationsys = new File(destinationPathsys);

        try {
            if (swap_contextual_header) {
                FileUtils.copyFile(sourcesys, destinationsys);
            } else {
                FileUtils.copyFile(source, destination);
            }
            Log.d("Progress", "1");
            Log.d("CopyFrameworkFile",
                    "Successfully copied framework apk from overlays folder to work directory");
        } catch (IOException e) {
            Log.d("CopyFrameworkFile",
                    "Failed to copy framework apk from resource-Files to work directory");
            e.printStackTrace();
        }


        Log.d("letsGetStarted", secondPhaseCommands[0]);

        new secondPhaseAsyncTasks().execute(secondPhaseCommands);

    }

    public void resetImageViews() {
        ImageView image_to_crop = (ImageView) inflation.findViewById(R.id.cropImageView);
        image_to_crop.setVisibility(View.GONE);
        Button cropButton = (Button) inflation.findViewById(R.id.crop_button);
        cropButton.setVisibility(View.GONE);
        ImageView croppedImage = (ImageView) inflation.findViewById(R.id.croppedImageView);
        croppedImage.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        checkBoxInstructions.setVisibility(View.VISIBLE);
        freeCropMode.setVisibility(View.VISIBLE);
        is_picture_selected = false;
        changeFABaction();
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE &&
                resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                        selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            is_picture_selected = true;
            changeFABaction();
            final ImageView image_to_crop = (ImageView) inflation.findViewById(R.id.cropImageView);
            image_to_crop.setVisibility(View.VISIBLE);
            cropImageView = (CropImageView) inflation.findViewById(R.id.cropImageView);
            checkBoxInstructions.setVisibility(View.GONE);
            freeCropMode.setVisibility(View.GONE);
            if (!free_crop_mode) {
                cropImageView.setCustomRatio(4, 1);
            }
            croppedImageView = (ImageView) inflation.findViewById(R.id.croppedImageView);
            cropImageView.setImageBitmap(bitmap);
            //https://github.com/IsseiAoki/SimpleCropView/issues/45
            //cropImageView.setImageURI(selectedImage);
            final Button cropButton = (Button) inflation.findViewById(R.id.crop_button);
            cropButton.setVisibility(View.VISIBLE);
            cropButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView croppedImage = (ImageView) inflation.findViewById(
                            R.id.croppedImageView);
                    croppedImage.setVisibility(View.VISIBLE);
                    croppedBitmap = cropImageView.getCroppedBitmap();
                    croppedImageView.setImageBitmap(cropImageView.getCroppedBitmap());
                    saveButton.setVisibility(View.VISIBLE);
                    cropButton.setVisibility(View.GONE);
                    image_to_crop.setVisibility(View.GONE);
                }
            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
                    File directory = new File(getActivity().getFilesDir(),
                            "/res/drawable/");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File selected = new File(getActivity().getFilesDir() +
                            "/res/drawable/",
                            "menuitem_background.png");
                    Log.d("swap", swap_contextual_header + "");
                    File f = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_afternoon.png");
                    File g = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_christmas.png");
                    File h = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_morning.png");
                    File i = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_newyearseve.png");
                    File j = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_night.png");
                    File k = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_noon.png");
                    File l = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_sunrise.png");
                    File m = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_sunset_hdpi.png");
                    File n = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_sunset_xhdpi.png");
                    File o = new File(getActivity().getFilesDir() + "/res/drawable/",
                            "notifhead_sunset.png");

                    try {
                        if (swap_contextual_header) {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                            fo.close();

                            g.createNewFile();
                            FileOutputStream go = new FileOutputStream(g);
                            go.write(bytes.toByteArray());
                            go.close();

                            h.createNewFile();
                            FileOutputStream ho = new FileOutputStream(h);
                            ho.write(bytes.toByteArray());
                            ho.close();

                            i.createNewFile();
                            FileOutputStream io = new FileOutputStream(i);
                            io.write(bytes.toByteArray());
                            io.close();

                            j.createNewFile();
                            FileOutputStream jo = new FileOutputStream(j);
                            jo.write(bytes.toByteArray());
                            jo.close();

                            k.createNewFile();
                            FileOutputStream ko = new FileOutputStream(k);
                            ko.write(bytes.toByteArray());
                            ko.close();

                            l.createNewFile();
                            FileOutputStream lo = new FileOutputStream(l);
                            lo.write(bytes.toByteArray());
                            lo.close();

                            m.createNewFile();
                            FileOutputStream mo = new FileOutputStream(m);
                            mo.write(bytes.toByteArray());
                            mo.close();

                            n.createNewFile();
                            FileOutputStream no = new FileOutputStream(n);
                            no.write(bytes.toByteArray());
                            no.close();

                            o.createNewFile();
                            FileOutputStream oo = new FileOutputStream(o);
                            oo.write(bytes.toByteArray());
                            oo.close();
                        } else {
                            selected.createNewFile();
                            FileOutputStream so = new FileOutputStream(selected);
                            so.write(bytes.toByteArray());
                            so.close();
                        }
                    } catch (IOException e) {
                        e.getStackTrace();
                        Log.d("ImageSaver",
                                "Unable to save new file");
                    }


                    resetImageViews();
                    letsGetStarted();

                }
            });
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
        return R.string.contextualheaderswapper;
    }

    private class secondPhaseAsyncTasks extends AsyncTask<String, String, Void> {

        private ProgressDialog pd;

        @Override
        protected Void doInBackground(String... params) {
            try {
                Log.d("Progress", "2");
                performAAPTonCommonsAPK(processor());
            } catch (Exception e) {
                Log.d("performAAPTonCommonsAPK",
                        "Could not process file.");
            }
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
            eu.chainfire.libsuperuser.Shell.SU.run("busybox killall com.android.systemui");

        }

        public List processor() {
            List<String> filenamePNGs = Arrays.asList(
                    "notifhead_afternoon.png", "notifhead_christmas.png", "notifhead_morning.png",
                    "notifhead_newyearseve.png", "notifhead_night.png", "notifhead_noon.png",
                    "notifhead_sunrise.png", "notifhead_sunset_hdpi.png",
                    "notifhead_sunset_xhdpi.png", "notifhead_sunset.png");

            List<String> list = new ArrayList<String>();

            File f2 = new File(
                    getActivity().getFilesDir().getAbsolutePath() + "/res/drawable/");
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

        private void performAAPTonCommonsAPK(List source) {
            Log.d("performAAPTonCommonsAPK",
                    "Mounting system as read-write as we prepare for some commands...");
            try {
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xhdpi-v4");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xxhdpi-v4");
                eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable-xxxhdpi-v4");
                Log.d("Progress", "3");
                Log.d("Made Directory", "Made");
                if (swap_contextual_header) {
                    for (int i = 0; i < source.size(); i++) {
                        eu.chainfire.libsuperuser.Shell.SU.run(
                                "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                        "/res/drawable/" + source.get(i) + " " +
                                        " res/drawable-xxhdpi-v4/" +
                                        source.get(i));
                        Log.d("xxhdpi cp", source.get(i) + "");
                    }
                    if (xhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                            "/res/drawable/" + source.get(i) + " " +
                                            " res/drawable-xhdpi-v4/" +
                                            source.get(i));
                            Log.d("xhdpi cp", source.get(i) + "");
                        }
                    }
                    if (xxxhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                            "/res/drawable/" + source.get(i) + " " +
                                            " res/drawable-xxxhdpi-v4/" +
                                            source.get(i));
                            Log.d("xxxhdpi cp", source.get(i) + "");
                        }
                    }
                    Log.d("performAAPTonCommonsAPK",
                            "Successfully copied all drawables into the root folder.");
                } else {
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                    "/res/drawable/menuitem_background.png " +
                                    "/res/drawable/menuitem_background.png");
                    Log.d("drawable", "dr");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                    "/res/drawable/menuitem_background.png " +
                                    "/res/drawable-xhdpi-v4/menuitem_background.png");
                    Log.d("drawable", "x");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                    "/res/drawable/menuitem_background.png " +
                                    "/res/drawable-xxhdpi-v4/menuitem_background.png");
                    Log.d("drawable", "xx");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                    "/res/drawable/menuitem_background.png " +
                                    "/res/drawable-xxxhdpi-v4/menuitem_background.png");
                    Log.d("drawable", "xxx");
                    Log.d("Progress", "4");
                }
                Log.d("Progress", "5");
                Log.d("performAAPTonCommonsAPK",
                        "Preparing for clean up on resources...");
                if (swap_contextual_header) {
                    for (int i = 0; i < source.size(); i++) {
                        try {
                            Process nativeApp2 = Runtime.getRuntime().exec(
                                    "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                            "/" + LayersFunc.themesystemui + ".apk " +
                                            "res/drawable-xxhdpi-v4/" +
                                            source.get(i) + "");
                            Log.d("xxhdpi rm", source.get(i) + "");
                            nativeApp2.waitFor();
                        } catch (Exception e) {
                            //
                        }
                    }
                    if (xhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            try {
                                Process nativeApp2 = Runtime.getRuntime().exec(
                                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                                "/" + LayersFunc.themesystemui + ".apk " +
                                                "res/drawable-xhdpi-v4/" +
                                                source.get(i) + "");
                                Log.d("xhdpi rm", source.get(i) + "");
                                nativeApp2.waitFor();
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                    if (xxxhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            try {
                                Process nativeApp2 = Runtime.getRuntime().exec(
                                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                                "/" + LayersFunc.themesystemui + ".apk " +
                                                "res/drawable-xxxhdpi-v4/" +
                                                source.get(i) + "");
                                Log.d("xxxhdpi rm", source.get(i) + "");
                                nativeApp2.waitFor();
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                } else {
                    Process nativeApp3 = Runtime.getRuntime().exec(
                            "aapt remove " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable/menuitem_background.png");
                    nativeApp3.waitFor();
                    Process nativeAppx = Runtime.getRuntime().exec(
                            "aapt remove " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xhdpi-v4/menuitem_background.png");
                    nativeAppx.waitFor();
                    Process nativeAppxx = Runtime.getRuntime().exec(
                            "aapt remove " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xxhdpi-v4/menuitem_background.png");
                    nativeAppxx.waitFor();
                    Process nativeAppxxx = Runtime.getRuntime().exec(
                            "aapt remove " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xxxhdpi-v4/menuitem_background.png");
                    nativeAppxxx.waitFor();
                }
                Log.d("performAAPTonCommonsAPK",
                        "Deleted main drawable file!");
                Log.d("Progress", "6");
                if (swap_contextual_header) {
                    for (int i = 0; i < source.size(); i++) {
                        try {
                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                            "/" + LayersFunc.themesystemui + ".apk " +
                                            "res/drawable-xxhdpi-v4/" +
                                            source.get(i));
                            Log.d("xxhdpi ad", source.get(i) + "");
                        } catch (Exception e) {
                            //
                        }
                    }
                    if (xhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            try {
                                eu.chainfire.libsuperuser.Shell.SU.run(
                                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                                "/" + LayersFunc.themesystemui + ".apk " +
                                                "res/drawable-xhdpi-v4/" +
                                                source.get(i));
                                Log.d("xhdpi ad", source.get(i) + "");
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                    if (xxxhdpi) {
                        for (int i = 0; i < source.size(); i++) {
                            try {
                                eu.chainfire.libsuperuser.Shell.SU.run(
                                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                                "/" + LayersFunc.themesystemui + ".apk " +
                                                "res/drawable-xxxhdpi-v4/" +
                                                source.get(i));
                                Log.d("xxxhdpi ad", source.get(i) + "");
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                } else {
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable/menuitem_background.png");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xhdpi-v4/menuitem_background.png");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xxhdpi-v4/menuitem_background.png");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "aapt add " +
                                    getActivity().getFilesDir().getAbsolutePath() +
                                    "/" + LayersFunc.themeframework + ".apk " +
                                    "res/drawable-xxxhdpi-v4/menuitem_background.png");
                }
                Log.d("Progress", "7");
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable");
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xhdpi-v4");
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xxhdpi-v4");
                eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable-xxxhdpi-v4");
                Log.d("Progress", "8");
                eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                Log.d("performAAPTonCommonsAPK",
                        "Cleaned up root directory and remounted system as read-only.");
                eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@" + LayersFunc.themeframework + ".apk@idmap /data/resource-cache/vendor@overlay@" + LayersFunc.themeframework + ".apk@idmap.bak");
                if (LayersFunc.checkBitPhone()) {
                    if (swap_contextual_header) {
                        LayersFunc.copyFABFinalizedAPK(getActivity(), LayersFunc.themesystemui,
                                true);
                        Log.d("Progress", "9");
                    } else {
                        LayersFunc.copyFABFinalizedAPK(getActivity(), LayersFunc.themeframework, true);
                        Log.d("Progress", "9");
                    }
                } else {
                    if (swap_contextual_header) {
                        LayersFunc.copyFinalizedAPK(getActivity(), LayersFunc.themesystemui, true);
                        Log.d("Progress", "9");
                    } else {
                        LayersFunc.copyFinalizedAPK(getActivity(), LayersFunc.themeframework, true);
                        Log.d("Progress", "9");
                    }
                }
                eu.chainfire.libsuperuser.Shell.SU.run("mv /data/resource-cache/vendor@overlay@" + LayersFunc.themeframework + ".apk@idmap.bak /data/resource-cache/vendor@overlay@" + LayersFunc.themeframework + ".apk@idmap");
                Log.d("Progress", "10");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
