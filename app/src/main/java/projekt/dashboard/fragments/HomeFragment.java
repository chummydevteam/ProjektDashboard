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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.ui.MainActivity;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    public SharedPreferences prefs;

    final public static String checkRomSupported(Context context) {
        if (getProp("ro.aicp.device") != "") {
            return "AICP ✓";
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
        Snackbar snack = Snackbar.make(themeSwitch, getResources().getString(R.string.homepage_dashboard_app_developpement_status),
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
