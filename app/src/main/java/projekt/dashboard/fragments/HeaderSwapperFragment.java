package projekt.dashboard.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
            free_crop_mode;
    public boolean is_debugging_mode_enabled = true;
    public CropImageView cropImageView;
    public ImageView croppedImageView;
    public Bitmap croppedBitmap;
    public Spinner spinner, spinner1;
    public String theme_dir, package_name;
    public FloatingActionButton apply_fab;
    public Button saveButton;
    public int spinner_current = 0;
    public int folder_directory = 1;
    public TextView checkBoxInstructions;
    public CheckBox autoClearSystemUICache, freeCropMode;

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

        apply_fab = (FloatingActionButton) inflation.findViewById(R.id.apply_fab);
        apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.primary_1_dark)));
        apply_fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore
                                .Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        apply_fab.setClickable(false);

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
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.themes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set On Item Selected Listener
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos == 0) {
                    apply_fab.setClickable(false);
                }
                if (pos == 1) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.materialdark.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.materialdark.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.materialdark.donate";
                        spinner_current = 1;
                        apply_fab.setClickable(true);
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                "Please install dark material // akZent before using!",
                                Toast.LENGTH_LONG);
                        toast.show();
                        spinner1.setSelection(spinner_current); // reset position
                        apply_fab.setClickable(false);
                    }
                }
                if (pos == 2) {
                    if (checkCurrentThemeSelection("com.chummy.jezebel.blackedout.donate")) {
                        theme_dir = "/data/app/com.chummy.jezebel.blackedout.donate" + "-"
                                + folder_directory + "/base.apk";
                        package_name = "com.chummy.jezebel.blackedout.donate";
                        spinner_current = 2;
                        apply_fab.setClickable(true);
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                "Please install blacked out // blakZent before using!",
                                Toast.LENGTH_LONG);
                        toast.show();
                        spinner1.setSelection(spinner_current); // reset position
                        apply_fab.setClickable(false);
                    }
                }
                if (pos == 3) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    final EditText edittext = new EditText(getContext());
                    alert.setMessage("please type the package identifier for your theme");
                    alert.setTitle("custom theme selector");
                    alert.setView(edittext);
                    alert.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (checkCurrentThemeSelection(edittext.getText().toString())) {
                                Log.e("TAG SUCCESS", edittext.getText().toString() +
                                        " has been chosen!");
                                theme_dir = "/data/app/" + edittext.getText().toString() +
                                        "-" + folder_directory + "/base.apk";
                                package_name = edittext.getText().toString();
                                apply_fab.setClickable(true);
                                Snackbar snackbar = Snackbar.make(apply_fab, "you are tweaking '" +
                                                edittext.getText().toString() + "'...",
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("REVERT", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        theme_dir = ""; // reset the theme directory
                                        package_name = ""; // also reset the package name
                                        resetImageViews();
                                        spinner1.setSelection(0); // reset position
                                    }
                                });
                                snackbar.show();
                            } else {
                                Log.e("TAG ERROR", edittext.getText().toString() +
                                        " does not exist.");
                                spinner1.setSelection(spinner_current);
                                apply_fab.setClickable(false);
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        "Unfortunately, the package identifier is not " +
                                                "properly found!",
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            spinner1.setSelection(spinner_current);
                            apply_fab.setClickable(false);
                        }
                    });
                    alert.setCancelable(false);

                    alert.show();
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
                        } else {
                            are_we_clearing_cache_after = false;
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
                        } else {
                            free_crop_mode = false;
                        }
                    }
                });

        checkBoxInstructions = (TextView) inflation.findViewById(R.id.textView2);


        return inflation;
    }

    public void changeFABaction() {
        if (!is_picture_selected) {
            apply_fab.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_photo_library_24dp));
            apply_fab.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_dark)));
            apply_fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore
                                    .Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
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

    public void letsGetStarted() {
        String[] secondPhaseCommands = {theme_dir};
        Log.e("COLORIZATION CUSTOMS", secondPhaseCommands[0]);
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            is_picture_selected = true;
            changeFABaction();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            final ImageView image_to_crop = (ImageView) inflation.findViewById(R.id.cropImageView);
            image_to_crop.setVisibility(View.VISIBLE);

            cropImageView = (CropImageView) inflation.findViewById(R.id.cropImageView);

            checkBoxInstructions.setVisibility(View.GONE);
            autoClearSystemUICache.setVisibility(View.GONE);
            freeCropMode.setVisibility(View.GONE);

            if (!free_crop_mode) {
                cropImageView.setCustomRatio(4, 1);
            }

            croppedImageView = (ImageView) inflation.findViewById(R.id.croppedImageView);
            cropImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

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

            saveButton = (Button) inflation.findViewById(R.id.save_button);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);

                    File directory = new File(getActivity().getFilesDir(),
                            "/res/drawable-xxhdpi-v23/");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File selected = new File(getActivity().getFilesDir() +
                            "/res/drawable-xxhdpi-v23/",
                            spinner.getSelectedItem().toString());
                    File f = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_afternoon.png");
                    File g = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_christmas.png");
                    File h = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_morning.png");
                    File i = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_newyearseve.png");
                    File j = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_night.png");
                    File k = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_noon.png");
                    File l = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunrise.png");
                    File m = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunset_hdpi.png");
                    File n = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
                            "notifhead_sunset_xhdpi.png");
                    File o = new File(getActivity().getFilesDir() + "/res/drawable-xxhdpi-v23/",
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
                        Log.e("ImageSaver", "Unable to save new file");
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
        return R.string.theme_utils;
    }

    private class secondPhaseAsyncTasks extends AsyncTask<String, String, Void> {

        private ProgressDialog pd;

        @Override
        protected Void doInBackground(String... params) {
            String theme_dir = params[0];
            try {
                copyCommonsFile(theme_dir);

            } catch (Exception e) {
                Log.e("performAAPTonCommonsAPK", "Could not process file.");
            }
            pd.setProgress(60);
            return null;
        }

        protected void onPreExecute() {
            String[] responses = {
                    "Please wait, while your phone gets beautified!",
                    "Injecting beautiful photos into your notification panel~",
                    "Sprinkling some magic over here...and over there....",
                    "OMG, am I broken?",
                    "I hope you did your reading, because you need to get ready for the " +
                            "amount of awesomeness this gives!",
                    "I hope you don't have to report bugs......please no.",
                    "That header is simply gorgeous!",
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

        private void copyCommonsFile(String theme_dir) {
            String sourcePath = theme_dir;
            File source = new File(sourcePath);
            String destinationPath = getActivity().getFilesDir().getAbsolutePath() +
                    "/new_header_apk.apk";
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
            try {
                performAAPTonCommonsAPK();
            } catch (Exception e) {
                Log.e("performAAPTonCommonsAPK", "Could not process file.");
            }


        }

        private void performAAPTonCommonsAPK() throws Exception {
            Log.e("performAAPTonCommonsAPK",
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
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_afternoon.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_christmas.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_morning.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_newyearseve.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_night.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_noon.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunrise.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset_hdpi.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset_xhdpi.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + "notifhead_sunset.png" +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                Log.e("performAAPTonCommonsAPK",
                        "Successfully copied all drawables into the root folder.");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + getActivity().getFilesDir().getAbsolutePath() +
                                "/res/drawable-xxhdpi-v23/" + spinner.getSelectedItem().toString() +
                                " /assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                Log.e("performAAPTonCommonsAPK",
                        "Successfully copied drawable into the root folder.");
            }

            Log.e("performAAPTonCommonsAPK",
                    "Preparing for clean up on resources...");

            if (is_all_selected) {
                Process nativeApp1 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                nativeApp1.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp2 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                nativeApp2.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp3 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                nativeApp3.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp4 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                nativeApp4.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp5 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                nativeApp5.waitFor();
                Process nativeApp6 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                nativeApp6.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp7 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                nativeApp7.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp8 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                nativeApp8.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp9 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                nativeApp9.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
                Process nativeApp10 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                nativeApp10.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted all drawable files!");
            } else {
                Process nativeApp1 = Runtime.getRuntime().exec(
                        "aapt remove " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                nativeApp1.waitFor();
                Log.e("performAAPTonCommonsAPK", "Deleted drawable file!");
            }

            // Adding all the new files in
            if (is_all_selected) {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_afternoon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_christmas.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_morning.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_newyearseve.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_night.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_noon.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunrise.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_hdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset_xhdpi.png");
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                "notifhead_sunset.png");
                Log.e("performAAPTonCommonsAPK", "Added freshly created photo files...ALL DONE!");
            } else {
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "aapt add " + getActivity().getFilesDir().getAbsolutePath() +
                                "/new_header_apk.apk " +
                                "assets/overlays/com.android.systemui/res/drawable-xxhdpi-v23/" +
                                spinner.getSelectedItem().toString());
                Log.e("AAPT ADD", spinner.getSelectedItem().toString());
                if (is_debugging_mode_enabled)
                    Log.e("performAAPTonCommonsAPK",
                            "Added freshly created photo file...ALL DONE!");
            }

            // Copy the modified APK to the directory
            eu.chainfire.libsuperuser.Shell.SU.run("cp " +
                    getActivity().getFilesDir().getAbsolutePath() +
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
