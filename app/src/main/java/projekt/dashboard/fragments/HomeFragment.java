package projekt.dashboard.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    final public static String checkRomSupported(Context context) {
        if (getProp("ro.cm.device") != "") {
            return "CyanogenMod ✓";
        }
        if (getProp("ro.du.device") != "") {
            return "Dirty Unicorns ✓";
        }
        if (getProp("ro.purenexus.version") != "") {
            return "Pure Nexus ✓";
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

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (checkRomSupported(getActivity()) == null) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText("ROM NOT SUPPORTED ✘");
        }
        if (checkRomSupported(getActivity()) == "cm_based_rom") {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_orange));
            status_message.setText("ROM NOT OFFICIALLY SUPPORTED (?)");
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(checkRomSupported(getActivity()));
        }
        Snackbar.make(inflation, "dashboard developer preview - internal beta test program",
                Snackbar.LENGTH_INDEFINITE).show();
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