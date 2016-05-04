package projekt.dashboard.layers.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import butterknife.ButterKnife;
import eu.chainfire.libsuperuser.Shell;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class ThemeUtilitiesFragment extends BasePageFragment {

    public boolean sysui, softreboot, reboot;
    public String vendor = "/system/vendor/overlay";
    public String mount = "/system";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_themeutilities, container, false);

        final CheckBox restartSystemUI = (CheckBox) inflation.findViewById(R.id.check1);
        restartSystemUI.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            sysui = true;
                        } else {
                            sysui = false;
                        }
                    }
                });

        final CheckBox softReboot = (CheckBox) inflation.findViewById(R.id.check2);
        softReboot.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (restartSystemUI.isChecked()) {
                                restartSystemUI.setChecked(false);
                            }
                            restartSystemUI.setClickable(false);

                            softreboot = true;
                        } else {
                            restartSystemUI.setClickable(true);

                            softreboot = false;
                        }
                    }
                });

        CheckBox systemReboot = (CheckBox) inflation.findViewById(R.id.check3);
        systemReboot.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (restartSystemUI.isChecked()) {
                                restartSystemUI.setChecked(false);
                            }
                            if (softReboot.isChecked()) {
                                softReboot.setChecked(false);
                            }

                            restartSystemUI.setClickable(false);
                            softReboot.setClickable(false);

                            reboot = true;
                        } else {
                            restartSystemUI.setClickable(true);
                            softReboot.setClickable(true);

                            reboot = false;
                        }
                    }
                });


        Button basicUtilitiesButton = (Button) inflation.findViewById(R.id.button1);
        basicUtilitiesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sysui) {
                    eu.chainfire.libsuperuser.Shell.SU.run("busybox killall com.android.systemui");
                }
                if (softreboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
                }
                if (reboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("reboot");
                }
            }
        });

        Button layersmanager = (Button) inflation.findViewById(R.id.button2);
        layersmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Changing Activity", "Layers Manager");
                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.MainActivity")));
            }
        });

        Button clear = (Button) inflation.findViewById(R.id.button3);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cleaning", "cleaning folder");
                cleanTempFolder();
            }
        });
        return inflation;
    }

    public void cleanTempFolder() {
        if (ColorChangerFragment.checkbitphone()) {
            vendor = "/vendor/overlay/";
            mount = "/vendor";
        }
        String mountsys = new String("mount -o remount,rw "+mount);
        String remountsys = new String("mount -o remount,ro "+mount);

        String command =new String("rm -r "+vendor);
        eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
        Log.e("clear",mountsys);
        eu.chainfire.libsuperuser.Shell.SU.run(command);
        Log.e("clear",command);
        eu.chainfire.libsuperuser.Shell.SU.run("mkdir "+vendor);
        Log.e("clear","mkdir");
        eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
        Log.e("clear",remountsys);
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
}