package projekt.dashboard.layers.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import butterknife.ButterKnife;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HeaderSwapperFragment extends BasePageFragment {

    private static int RESULT_LOAD_IMAGE = 1;
    public ViewGroup inflation;
    public boolean is_all_selected, is_picture_selected, are_we_clearing_Files_after,
            free_crop_mode, swap_contextual_header;
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
    public CheckBox freeCropMode,swapcontext;
    public SharedPreferences prefs;
    public String vendor = "/system/vendor/overlay";
    public String mount = "/system";


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_headerswapper, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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
                            Log.e("CheckBox",
                                    "Free crop mode for Image Cropper has been ENABLED.");
                        } else {
                            free_crop_mode = false;
                             Log.e("CheckBox",
                                    "Free crop mode for Image Cropper has been DISABLED.");
                        }
                    }
                });

        swapcontext = (CheckBox) inflation.findViewById(R.id.checkBox3);
        swapcontext.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            swap_contextual_header = true;
                            Log.e("CheckBox", "Universal variable to advanced log ENABLED.");
                        } else {
                            swap_contextual_header = false;
                            Log.e("CheckBox", "Universal variable to advanced log DISABLED.");
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
        boolean phone = checkbitphone();
        if (phone == true) {
            Log.e("colorswatch", "Found 64,Setting Vendor");
            vendor = "/vendor/overlay";
            mount = "/vendor";
        }
        String[] secondPhaseCommands = {vendor + "/Akzent_Framework.apk"};
        Log.e("letsGetStarted", secondPhaseCommands[0]);

        new secondPhaseAsyncTasks().execute(secondPhaseCommands);

        Button softReboot = (Button) inflation.findViewById(R.id.softreboot);
        softReboot.setVisibility(View.VISIBLE);
        softReboot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
            }
        });
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
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

                        File f = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_afternoon.png");
                        File g = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_christmas.png");
                        File h = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_morning.png");
                        File i = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_newyearseve.png");
                        File j = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_night.png");
                        File k = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_noon.png");
                        File l = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_sunrise.png");
                        File m = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_sunset_hdpi.png");
                        File n = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_sunset_xhdpi.png");
                        File o = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v4/",
                                "notifhead_sunset.png");

                    try {
                        selected.createNewFile();
                        FileOutputStream so = new FileOutputStream(selected);
                        so.write(bytes.toByteArray());
                        so.close();

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

                    } catch (IOException e) {
                        Log.e("ImageSaver",
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
            String theme_dir = params[0];
            try {
                copyCommonsFile(theme_dir);
            } catch (Exception e) {
                Log.e("performAAPTonCommonsAPK",
                        "Caught the exception.");
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

        private void copyCommonsFile(String theme_dir) {
            Log.e("CopyFrameworkFile", "Function Called");
            Log.e("CopyFrameworkFile", "Function Started");
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getFilesDir().getAbsolutePath() +
                    "/Akzent_Framework.apk";
            File destination = new File(destinationPath);

            String sourcePathsys = vendor+"/Akzent_SystemUI";
            File sourcesys = new File(sourcePathsys);
            String destinationPathsys = getActivity().getFilesDir().getAbsolutePath() +
                    "/Akzent_SystemUI.apk";
            File destinationsys = new File(destinationPathsys);
            
            try {
                FileUtils.copyFile(source, destination);
                if(swap_contextual_header){
                    FileUtils.copyFile(sourcesys, destinationsys);
                }
                Log.e("CopyFrameworkFile",
                        "Successfully copied framework apk from overlays folder to work directory");
                Log.e("CopyFrameworkFile", "Function Stopped");
            } catch (IOException e) {
                Log.e("CopyFrameworkFile",
                        "Failed to copy framework apk from resource-Files to work directory");
                Log.e("CopyFrameworkFile", "Function Stopped");
                e.printStackTrace();
            }
            try {
                performAAPTonCommonsAPK();
            } catch (Exception e) {
                 Log.e("performAAPTonCommonsAPK",
                        "Could not process file.");
            }
            

        }

        private void performAAPTonCommonsAPK() throws Exception {
            Log.e("performAAPTonCommonsAPK",
                    "Mounting system as read-write as we prepare for some commands...");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /res/drawable");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " + getActivity().getFilesDir().getAbsolutePath() +
                            "/res/drawable/menuitem_background.png " +
                            "/res/drawable/menuitem_background.png");
            if (swap_contextual_header) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_afternoon.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_christmas.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_morning.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_newyearseve.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_night.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_noon.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_sunrise.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_sunset_hdpi.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_sunset_xhdpi.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v4/" + "notifhead_sunset.png" +
                                " /res/drawable-xxhdpi-v4/" +
                                "notifhead_sunset.png");
                 Log.e("performAAPTonCommonsAPK",
                        "Successfully copied all drawables into the root folder.");
            }
            Log.e("performAAPTonCommonsAPK",
                    "Successfully copied drawable into the root folder.");

            Log.e("performAAPTonCommonsAPK",
                    "Preparing for clean up on resources...");
            Process nativeApp3 = Runtime.getRuntime().exec(
                    "aapt remove " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk res/drawable/menuitem_background.png");
            Log.e("performAAPTonCommonsAPK",
                    "Deleted main drawable file!");
            nativeApp3.waitFor();
            if (swap_contextual_header) {
                Process nativeApp1 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_afternoon.png");
                nativeApp1.waitFor();
                 Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp2 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_christmas.png");
                nativeApp2.waitFor();
                 Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeAp3 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_morning.png");
                nativeAp3.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp4 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_newyearseve.png");
                nativeApp4.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp5 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_night.png");
                nativeApp5.waitFor();
                Process nativeApp6 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_noon.png");
                nativeApp6.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp7 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunrise.png");
                nativeApp7.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp8 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset_hdpi.png");
                nativeApp8.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp9 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset_xhdpi.png");
                nativeApp9.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp10 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset.png");
                nativeApp10.waitFor();
                Log.e("performAAPTonCommonsAPK",
                        "Deleted all drawable files!");
            }
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk res/drawable/menuitem_background.png");
            if (swap_contextual_header) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " +
                                "res/drawable-xxhdpi-v4" +
                                "notifhead_sunset.png");
                Log.e("performAAPTonCommonsAPK",
                        "Added freshly created photo files...ALL DONE!");
            }
            Log.e("performAAPTonCommonsAPK",
                    "Added freshly created drawable file...ALL DONE!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /res/drawable");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
            Log.e("performAAPTonCommonsAPK",
                    "Cleaned up root directory and remounted system as read-only.");
            if (checkbitphone()) {
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
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk " + "/system/vendor/overlay/Akzent_Framework.apk");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/Akzent_Framework.apk");
            if(swap_contextual_header){
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " + "/system/vendor/overlay/Akzent_SystemUI.apk");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/system/vendor/overlay/Akzent_SystemUI.apk");
            }
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard.layers/files");
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
            if(swap_contextual_header){
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " +
                                getActivity().getFilesDir().getAbsolutePath() +
                                "/Akzent_SystemUI.apk " + "/vendor/overlay/Akzent_SystemUI.apk");
                eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + "/vendor/overlay/Akzent_SystemUI.apk");
            }
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyFinalizedAPK",
                    "Successfully copied the modified resource APK into " +
                            "/system/vendor/overlay/ and modified the permissions!");
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard.layers/files");
            Log.e("copyFinalizedAPK",
                    "Successfully Deleted Files ");

        }
    }
}
