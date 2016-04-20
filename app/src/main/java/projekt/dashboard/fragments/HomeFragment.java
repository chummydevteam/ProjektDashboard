package projekt.dashboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import projekt.dashboard.BuildConfig;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.ui.MainActivity;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    public SharedPreferences prefs;
    public int current_pressed_count = 0;
    public boolean clicked_after_seventh = false;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_homepage, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        FloatingActionButton themeSwitch = (FloatingActionButton)
                inflation.findViewById(R.id.changeTheme);

        Animation anim2;

        if (prefs.getBoolean("advanced_mode_enabled", true)) {
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        } else {
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.spin);
        }
        anim2.reset();
        final ImageView iv2 = (ImageView) inflation.findViewById(R.id.spinnerWheel);
        iv2.clearAnimation();
        iv2.startAnimation(anim2);

        final ImageView mainImage = (ImageView) inflation.findViewById(R.id.landingIconFirst);
        mainImage.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                if (!prefs.getBoolean("advanced_mode_enabled", true)) {
                    if (current_pressed_count < 6) {
                        switch (current_pressed_count) {
                            case 0:
                                Toast toast = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_one),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                current_pressed_count += 1;
                                break;
                            case 1:
                                Toast toast1 = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_two),
                                        Toast.LENGTH_SHORT);
                                toast1.show();
                                current_pressed_count += 1;
                                break;
                            case 2:
                                Toast toast2 = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_three),
                                        Toast.LENGTH_SHORT);
                                toast2.show();
                                current_pressed_count += 1;
                                break;
                            case 3:
                                Toast toast3 = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_four),
                                        Toast.LENGTH_SHORT);
                                toast3.show();
                                current_pressed_count += 1;
                                break;
                            case 4:
                                Toast toast4 = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_five),
                                        Toast.LENGTH_SHORT);
                                toast4.show();
                                current_pressed_count += 1;
                                break;
                            case 5:
                                Toast toast5 = Toast.makeText(
                                        getContext(),
                                        getResources().getString(R.string.secret_feature_six),
                                        Toast.LENGTH_SHORT);
                                toast5.show();
                                current_pressed_count += 1;
                                break;
                        }
                    } else {
                        if (!clicked_after_seventh) {
                            Toast toast = Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.secret_feature_seven),
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
                    if (current_pressed_count < 6) {
                        switch (current_pressed_count) {
                            case 0:
                                current_pressed_count += 1;
                                break;
                            case 1:
                                current_pressed_count += 1;
                                break;
                            case 2:
                                current_pressed_count += 1;
                                break;
                            case 3:
                                current_pressed_count += 1;
                                break;
                            case 4:
                                current_pressed_count += 1;
                                break;
                            case 5:
                                current_pressed_count += 1;
                                break;
                        }
                    } else {
                        if (!clicked_after_seventh) {
                            Toast toast = Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.secret_feature_disabled),
                                    Toast.LENGTH_LONG);

                            iv2.clearAnimation();

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

        if (prefs.getBoolean("blacked_out_enabled", true)) {
            themeSwitch.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_blacked_out)));
        } else {
            themeSwitch.setBackgroundTintList(ColorStateList.valueOf(
                    getResources().getColor(R.color.primary_1_dark_material)));
        }

        themeSwitch.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                if (prefs.getBoolean("blacked_out_enabled", true)) {
                    prefs.edit().putBoolean("blacked_out_enabled", false).commit();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    prefs.edit().putBoolean("blacked_out_enabled", true).commit();
                    getActivity().finish();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
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
        Snackbar snack = Snackbar.make(themeSwitch, prefs.getString("dashboard_username",
                        getResources().
                                getString(R.string.
                                        homepage_dashboard_app_development_status_default_username))
                        + getResources().
                        getString(R.string.homepage_dashboard_app_development_status)
                        + " (" + BuildConfig.VERSION_NAME + ")",
                Snackbar.LENGTH_INDEFINITE);
        ViewGroup group = (ViewGroup) snack.getView();
        if (prefs.getBoolean("blacked_out_enabled", true)) {
            group.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.primary_1_blacked_out));
        } else {
            group.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.primary_1_dark_material));
        }
        snack.show();

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
