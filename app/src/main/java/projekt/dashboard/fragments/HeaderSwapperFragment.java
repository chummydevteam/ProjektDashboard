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
    public int counter = 0;
    public int folder_directory = 1;
    public int current_hour;
    public TextView checkBoxInstructions, currentTimeVariable;
    public CheckBox autoClearSystemUICache, freeCropMode, debugmode;
    public SharedPreferences prefs;

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

        Calendar c = Calendar.getInstance();
        current_hour = c.get(Calendar.HOUR_OF_DAY);
        currentTimeVariable = (TextView) inflation.findViewById(R.id.currentTime);

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
        apply_fab.hide();

        spinner = (Spinner) inflation.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contextual_headers, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set On Item Selected Listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos == 0) {
                    is_all_selected = true;

                } else {
                    is_all_selected = false;
                }
                if (current_hour == 6 ||
                        current_hour == 7 ||
                        current_hour == 8) {
                    currentTimeVariable.setText("notifhead_sunrise");
                }

                if (current_hour == 9 ||
                        current_hour == 10) {
                    currentTimeVariable.setText("notifhead_morning");
                }

                if (current_hour == 11 ||
                        current_hour == 12 ||
                        current_hour == 13 ||
                        current_hour == 14 ||
                        current_hour == 15 ||
                        current_hour == 16 ||
                        current_hour == 17 ||
                        current_hour == 18) {
                    currentTimeVariable.setText("notifhead_afternoon");
                }
                if (current_hour == 19) {
                    currentTimeVariable.setText("notifhead_sunset");
                }

                if (current_hour == 20 ||
                        current_hour == 21 ||
                        current_hour == 22 ||
                        current_hour == 23 ||
                        current_hour == 0 ||
                        current_hour == 1 ||
                        current_hour == 2 ||
                        current_hour == 3 ||
                        current_hour == 4 ||
                        current_hour == 5) {
                    currentTimeVariable.setText("notifhead_night");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


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
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.contextualheaderswapper_toast_cache_empty_reboot_first),
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
                    apply_fab.hide();
                }
                if (pos == 1) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.materialdark.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.materialdark.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.materialdark.donate";
                        apply_fab.show();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.akzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                        apply_fab.hide();
                    }
                }
                if (pos == 2) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.blackedout.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.blackedout.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.blackedout.donate";
                        apply_fab.show();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.blakzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                        spinner1.setSelection(0);
                        apply_fab.hide();
                    }
                } else {
                    String packageIdentifier = spinner1.getSelectedItem().toString();
                    if (checkCurrentThemeSelection(packageIdentifier)) {
                        theme_dir = "/data/app/" + packageIdentifier + "-"
                                + folder_directory + "/base.apk";
                        package_name = packageIdentifier;
                        apply_fab.show();
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


        autoClearSystemUICache = (CheckBox) inflation.findViewById(R.id.checkBox);
        autoClearSystemUICache.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            are_we_clearing_cache_after = true;
                            Log.d("CheckBox",
                                    "SystemUI theme cache will be wiped for this theme " +
                                            "after applying.");
                        } else {
                            are_we_clearing_cache_after = false;
                            Log.d("CheckBox",
                                    "SystemUI theme cache will NOT be wiped for this theme " +
                                            "after applying.");
                        }
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

        debugmode = (CheckBox) inflation.findViewById(R.id.checkBox3);
        debugmode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
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


    private Intent getImageSelectionIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return intent;
    }

    public void letsGetStarted() {
        String[] secondPhaseCommands = {theme_dir};
        Log.d("letsGetStarted", secondPhaseCommands[0]);
        new secondPhaseAsyncTasks().execute(secondPhaseCommands);

        Button softReboot = (Button) inflation.findViewById(R.id.softreboot);
        softReboot.setVisibility(View.VISIBLE);
        softReboot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
            }
        });
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
        autoClearSystemUICache.setVisibility(View.VISIBLE);
        freeCropMode.setVisibility(View.VISIBLE);
        debugmode.setVisibility(View.VISIBLE);
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
            autoClearSystemUICache.setVisibility(View.GONE);
            freeCropMode.setVisibility(View.GONE);
            debugmode.setVisibility(View.GONE);

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

                    File directory = new File(getActivity().getCacheDir(),
                            "/res/drawable-xxhdpi-v23/");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File selected = new File(getActivity().getCacheDir() +
                            "/res/drawable-xxhdpi-v23/",
                            spinner.getSelectedItem().toString());
                    File f = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_afternoon.png");
                    File g = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_christmas.png");
                    File h = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_morning.png");
                    File i = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_newyearseve.png");
                    File j = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_night.png");
                    File k = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_noon.png");
                    File l = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunrise.png");
                    File m = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunset_hdpi.png");
                    File n = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunset_xhdpi.png");
                    File o = new File(getActivity().getCacheDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunset.png");

                    try {
                        if (is_all_selected) {
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
            String theme_dir = params[0];
            try {
                copyCommonsFile(theme_dir);
            } catch (Exception e) {
                Log.d("performAAPTonCommonsAPK",
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
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getCacheDir().getAbsolutePath() +
                    "/new_header_apk.apk";
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                Log.d("copyCommonsFile",
                        "Successfully copied commons apk from resource-cache to work directory");
            } catch (IOException e) {
                Log.d("copyCommonsFile",
                        "Failed to copy commons apk from resource-cache to work directory");
                e.printStackTrace();
            }
            try {
                performAAPTonCommonsAPK();
            } catch (Exception e) {
                Log.d("performAAPTonCommonsAPK",
                        "Could not process file.");
            }


        }

        private void performAAPTonCommonsAPK() throws Exception {
            Log.d("performAAPTonCommonsAPK",
                    "Mounting system as read-write as we prepare for some commands...");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /assets");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /assets/overlays");
            eu.chainfire.libsuperuser.Shell.SU.run("mkdir /assets/overlays/com.android.systemui");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "mkdir /assets/overlays/com.android.systemui/res");
            eu.chainfire.libsuperuser.Shell.SU.run(
                    "mkdir /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23");

            // Copy the files over
            if (is_all_selected) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_afternoon.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_christmas.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_morning.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_newyearseve.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_night.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_noon.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunrise.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset_hdpi.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset_xhdpi.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                Log.d("performAAPTonCommonsAPK",
                        "Successfully copied all drawables into the root folder.");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getCacheDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + spinner.getSelectedItem().toString() +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                Log.d("performAAPTonCommonsAPK",
                        "Successfully copied drawable into the root folder.");
            }

            Log.d("performAAPTonCommonsAPK",
                    "Preparing for clean up on resources...");

            if (is_all_selected) {
                Process nativeApp1 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                nativeApp1.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp2 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                nativeApp2.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp3 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                nativeApp3.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp4 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                nativeApp4.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp5 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                nativeApp5.waitFor();
                Process nativeApp6 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                nativeApp6.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp7 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                nativeApp7.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp8 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                nativeApp8.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp9 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                nativeApp9.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
                Process nativeApp10 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                nativeApp10.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted all drawable files!");
            } else {
                Process nativeApp1 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                nativeApp1.waitFor();
                Log.d("performAAPTonCommonsAPK",
                        "Deleted drawable file!");
            }

            // Adding all the new files in
            if (is_all_selected) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                Log.d("performAAPTonCommonsAPK",
                        "Added freshly created photo files...ALL DONE!");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getCacheDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                Log.d("AAPT ADD",
                        spinner.getSelectedItem().toString());
                Log.d("performAAPTonCommonsAPK",
                            "Added freshly created photo file...ALL DONE!");
            }

            // Copy the modified APK to the directory
            eu.chainfire.libsuperuser.Shell.SU.run("cp " +
                    getActivity().getCacheDir().getAbsolutePath() +
                    "/new_header_apk.apk " + theme_dir);

            // Set Permissions for the new APK
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 644 " + theme_dir);

            // Do clean up
            cleanTempFolder();

            // Follow boolean for autoclear cache
            if (are_we_clearing_cache_after) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "rm -r /data/resource-cache/" + package_name + "/com.android.systemui");
            }

            // Close everything and make sure
            eu.chainfire.libsuperuser.Shell.SU.run("rm -r /assets");
            eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");

        }
    }
}
