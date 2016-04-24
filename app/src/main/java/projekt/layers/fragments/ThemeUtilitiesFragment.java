package projekt.layers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.ButterKnife;
import projekt.layers.R;
import projekt.layers.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class ThemeUtilitiesFragment extends BasePageFragment {

    public boolean sysui, softreboot, reboot;

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
                    eu.chainfire.libsuperuser.Shell.SU.run("busybox pkill com.android.systemui");
                }
                if (softreboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("busybox killall system_server");
                }
                if (reboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("reboot");
                }
            }
        });
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
        return R.string.theme_utils;
    }
}