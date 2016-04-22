package projekt.dashboard.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HeaderSwapperFragment extends BasePageFragment {

    private static int RESULT_LOAD_IMAGE = 1;
    public ViewGroup inflation;
    public boolean is_all_selected, is_picture_selected, are_we_clearing_cache_after,
            free_crop_mode, is_debugging_mode_enabled;
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
    public CheckBox freeCropMode;
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
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Free crop mode for Image Cropper has been ENABLED.");
                        } else {
                            free_crop_mode = false;
                            if (is_debugging_mode_enabled) Log.e("CheckBox",
                                    "Free crop mode for Image Cropper has been DISABLED.");
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
        if (is_debugging_mode_enabled) Log.e("letsGetStarted", secondPhaseCommands[0]);

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

                    try {
                        selected.createNewFile();
                        FileOutputStream so = new FileOutputStream(selected);
                        so.write(bytes.toByteArray());
                        so.close();

                    } catch (IOException e) {
                        if (is_debugging_mode_enabled) Log.e("ImageSaver",
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
                if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
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
        }

        private void copyCommonsFile(String theme_dir) {
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
            try {
                performAAPTonCommonsAPK();
            } catch (Exception e) {
                if (is_debugging_mode_enabled) Log.e("performAAPTonCommonsAPK",
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
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "aapt add " +
                            getActivity().getFilesDir().getAbsolutePath() +
                            "/Akzent_Framework.apk res/drawable/menuitem_background.png");

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
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard/files");
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
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/data/projekt.dashboard/files");
            Log.e("copyFinalizedAPK",
                    "Successfully Deleted Files ");

        }
    }
}
