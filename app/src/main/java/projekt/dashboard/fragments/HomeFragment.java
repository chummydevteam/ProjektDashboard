package projekt.dashboard.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.widgets.AnimatedEditText;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.ui.MainActivity;
import projekt.dashboard.ui.SplashScreenActivity;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    public SharedPreferences prefs;
    public int current_pressed_count = 0;
    public boolean clicked_after_seventh = false;
    @Bind(R.id.tapBarMenu)
    TapBarMenu tapBarMenu;

    final public static String checkRomSupported(Context context) {
        if (getProp("ro.aicp.device") != "") {
            return "AICP ✓";
        }
        if (getProp("ro.aosip.version") != "") {
            return "AOSiP ✓";
        }
        if (getProp("ro.bliss.device") != "") {
            return "Bliss ✓";
        }
        if (getProp("ro.cm.device") != "") {
            return "CyanogenMod ✓";
        }
        if (getProp("ro.du.device") != "") {
            return "Dirty Unicorns ✓";
        }
        if (getProp("ro.purenexus.version") != "") {
            return "Pure Nexus ✓";
        }
        if (getProp("ro.rr.device") != "") {
            return "Resurrection Remix ✓";
        }
        if (getProp("ro.screwd.device") != "") {
            return "Screw'd Android ✓";
        }
        if (getProp("ro.validus.version") != "") {
            return "Validus ✓";
        } else {
            if (isAppInstalled(context, "org.cyanogenmod.theme.chooser")) {
                return "cm_based_rom";
            } else {
                return null;
            }
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getProp(String propName) {
        Process p = null;
        String result = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop",
                    propName).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                result = line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @OnClick(R.id.tapBarMenu)
    public void onMenuButtonClick() {
        tapBarMenu.toggle();
    }

    @OnClick({R.id.item1, R.id.item2})
    public void onMenuItemClick(View view) {
        switch (view.getId()) {
            case R.id.item1:
                if (prefs.getBoolean("blacked_out_enabled", true)) {
                    prefs.edit().putBoolean("blacked_out_enabled", false).commit();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    prefs.edit().putBoolean("blacked_out_enabled", true).commit();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                }
                break;
            case R.id.item2:
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                        getContext());
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.name_picker_dialog, null);
                alert.setView(dialogView);
                final AnimatedEditText textBox = (AnimatedEditText) dialogView.findViewById(R.id.editText);

                alert.setMessage(getResources().getString(R.string.change_name_dialog_message));
                alert.setTitle(getResources().getString(R.string.change_name_dialog_title));

                alert.setCancelable(false);
                alert.setPositiveButton(getResources().getString(
                                R.string.change_name_dialog_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String string_processor = textBox.getText().toString();
                                if (string_processor.endsWith(" ")) {
                                    string_processor = string_processor.substring(
                                            0, string_processor.length() - 1);
                                }
                                prefs.edit().putString(
                                        "dashboard_username", string_processor).commit();
                                startActivity(new Intent(
                                        getActivity(), SplashScreenActivity.class));
                                getActivity().finish();
                            }
                        });
                alert.setNegativeButton(getResources().getString(
                                R.string.downloader_dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //
                            }
                        });
                alert.show();
                break;
            default:
                tapBarMenu.close();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_homepage, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Animation anim2;

        final ImageView iv2 = (ImageView) inflation.findViewById(R.id.spinnerWheel);
        if (prefs.getBoolean("advanced_mode_enabled", true)) {
            iv2.setImageDrawable(getResources().getDrawable(R.drawable.rainbow_logo));
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.spin_faster);
        } else {
            iv2.setImageDrawable(getResources().getDrawable(R.drawable.splashscreen_spinner));
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.spin);
        }
        anim2.reset();

        iv2.clearAnimation();
        iv2.startAnimation(anim2);

        final ImageView mainImage = (ImageView) inflation.findViewById(R.id.landingIconFirst);
        mainImage.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                if (!prefs.getBoolean("advanced_mode_enabled", true)) {
                    if (current_pressed_count < 9000) {
                        current_pressed_count += 1;
                    } else {
                        if (!clicked_after_seventh) {
                            Toast toast = Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.secret_feature_enabled),
                                    Toast.LENGTH_LONG);

                            iv2.clearAnimation();

                            Animation anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                            anim2.reset();
                            ImageView iv = (ImageView) inflation.findViewById(R.id.spinnerWheel);
                            iv.clearAnimation();
                            iv.startAnimation(anim2);

                            toast.show();
                            clicked_after_seventh = true;
                            prefs.edit().putBoolean("advanced_mode_enabled", true).commit();
                        } else {
                            Intent i = getActivity().getPackageManager()
                                    .getLaunchIntentForPackage(getActivity().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }

                    }
                } else {
                    if (current_pressed_count < 9000) {
                        current_pressed_count += 1;
                    } else {
                        if (!clicked_after_seventh) {
                            Toast toast = Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.secret_feature_disabled),
                                    Toast.LENGTH_LONG);

                            iv2.clearAnimation();
                            iv2.setImageDrawable(getResources().getDrawable(R.drawable.splashscreen_spinner));
                            Animation anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.spin);
                            anim2.reset();
                            ImageView iv = (ImageView) inflation.findViewById(R.id.spinnerWheel);
                            iv.clearAnimation();
                            iv.startAnimation(anim2);

                            toast.show();
                            clicked_after_seventh = true;
                            prefs.edit().putBoolean("advanced_mode_enabled", false).commit();
                        } else {
                            Intent i = getActivity().getPackageManager()
                                    .getLaunchIntentForPackage(getActivity().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }

                    }
                }
            }
        }));

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (checkRomSupported(getActivity()) == null) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_supported));
        }
        if (checkRomSupported(getActivity()) == "cm_based_rom") {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_orange));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_officially_supported));
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(checkRomSupported(getActivity()));
        }

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
        return R.string.home;
    }
}
