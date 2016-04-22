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
    public boolean akzent, blakzent, projektklar, all_color_switch, all_cdt_themes, all_themes;
    public boolean did_i_run;

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
                    eu.chainfire.libsuperuser.Shell.SU.run("killall com.android.systemui");
                }
                if (softreboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("killall zygote");
                }
                if (reboot) {
                    eu.chainfire.libsuperuser.Shell.SU.run("reboot");
                }
            }
        });


        final CheckBox rebuildAkzent = (CheckBox) inflation.findViewById(R.id.switch1);
        rebuildAkzent.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            akzent = true;
                        } else {
                            akzent = false;
                        }
                    }
                });

        final CheckBox rebuildBlakzent = (CheckBox) inflation.findViewById(R.id.switch2);
        rebuildBlakzent.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            blakzent = true;
                        } else {
                            blakzent = false;
                        }
                    }
                });

        final CheckBox rebuildProjektKlar = (CheckBox) inflation.findViewById(R.id.switch3);
        rebuildProjektKlar.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            projektklar = true;
                        } else {
                            projektklar = false;
                        }
                    }
                });

        final CheckBox rebuildAllColorSwitchThemes = (CheckBox) inflation.
                findViewById(R.id.switch4);
        rebuildAllColorSwitchThemes.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (rebuildAkzent.isChecked()) {
                                rebuildAkzent.setChecked(false);
                            }
                            if (rebuildBlakzent.isChecked()) {
                                rebuildBlakzent.setChecked(false);
                            }
                            if (rebuildProjektKlar.isChecked()) {
                                rebuildProjektKlar.setChecked(false);
                            }

                            rebuildAkzent.setClickable(false);
                            rebuildBlakzent.setClickable(false);
                            rebuildProjektKlar.setClickable(false);

                            all_color_switch = true;
                        } else {
                            rebuildAkzent.setClickable(true);
                            rebuildBlakzent.setClickable(true);
                            rebuildProjektKlar.setClickable(true);
                            all_color_switch = false;
                        }
                    }
                });

        final CheckBox rebuildCDTthemes = (CheckBox) inflation.findViewById(R.id.switch5);
        rebuildCDTthemes.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (rebuildAkzent.isChecked()) {
                                rebuildAkzent.setChecked(false);
                            }
                            if (rebuildBlakzent.isChecked()) {
                                rebuildBlakzent.setChecked(false);
                            }
                            if (rebuildProjektKlar.isChecked()) {
                                rebuildProjektKlar.setChecked(false);
                            }
                            if (rebuildAllColorSwitchThemes.isChecked()) {
                                rebuildAllColorSwitchThemes.setChecked(false);
                            }

                            rebuildAkzent.setClickable(false);
                            rebuildBlakzent.setClickable(false);
                            rebuildProjektKlar.setClickable(false);
                            rebuildAllColorSwitchThemes.setClickable(false);

                            all_cdt_themes = true;
                        } else {
                            rebuildAkzent.setClickable(true);
                            rebuildBlakzent.setClickable(true);
                            rebuildProjektKlar.setClickable(true);
                            rebuildAllColorSwitchThemes.setClickable(true);

                            all_cdt_themes = false;
                        }
                    }
                });

        CheckBox rebuildAllThemes = (CheckBox) inflation.findViewById(R.id.switch6);
        rebuildAllThemes.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (rebuildAkzent.isChecked()) {
                                rebuildAkzent.setChecked(false);
                            }
                            if (rebuildBlakzent.isChecked()) {
                                rebuildBlakzent.setChecked(false);
                            }
                            if (rebuildProjektKlar.isChecked()) {
                                rebuildProjektKlar.setChecked(false);
                            }
                            if (rebuildAllColorSwitchThemes.isChecked()) {
                                rebuildAllColorSwitchThemes.setChecked(false);
                            }
                            if (rebuildCDTthemes.isChecked()) {
                                rebuildCDTthemes.setChecked(false);
                            }

                            rebuildAkzent.setClickable(false);
                            rebuildBlakzent.setClickable(false);
                            rebuildProjektKlar.setClickable(false);
                            rebuildAllColorSwitchThemes.setClickable(false);
                            rebuildCDTthemes.setClickable(false);

                            all_themes = true;
                        } else {
                            rebuildAkzent.setClickable(true);
                            rebuildBlakzent.setClickable(true);
                            rebuildProjektKlar.setClickable(true);
                            rebuildAllColorSwitchThemes.setClickable(true);
                            rebuildCDTthemes.setClickable(true);

                            all_themes = false;
                        }
                    }
                });

        final Button debuggingUtilitiesButton = (Button) inflation.findViewById(R.id.button2);
        debuggingUtilitiesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (did_i_run) {
                    did_i_run = false;
                    eu.chainfire.libsuperuser.Shell.SU.run("reboot");
                }
                if (akzent) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.materialdark.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (blakzent) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.blackedout.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (projektklar) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/projekt.klar");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (all_color_switch) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.materialdark.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.blackedout.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/projekt.klar");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (all_cdt_themes) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.material.dark");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/" +
                                    "com.chummy.jezebel.material.dark.regression");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.materialdark.beta");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.materialdark.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.blacked.out");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/" +
                                    "com.chummy.jezebel.blacked.out.regression");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/com.chummy.jezebel.blackedout.donate");
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r /data/resource-cache/projekt.klar");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (all_themes) {
                    did_i_run = true;
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,rw /");
                    eu.chainfire.libsuperuser.Shell.SU.run("rm -r /data/resource-cache");
                    eu.chainfire.libsuperuser.Shell.SU.run("mount -o remount,ro /");
                }
                if (did_i_run) {
                    debuggingUtilitiesButton.setBackgroundColor(
                            getResources().getColor(R.color.attention_color));
                    debuggingUtilitiesButton.setText("Reboot Now!");
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