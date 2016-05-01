package projekt.dashboard.layers.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import butterknife.ButterKnife;
import projekt.dashboard.layers.BuildConfig;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {
    public static String vendor = "/system/vendor/overlay";
    public static String mount = "/system";
    SharedPreferences prefs;

    final public static boolean checkRomSupported(Context context) {

        if (isAppInstalled(context, "com.lovejoy777.rroandlayersmanager")) {
            return true;
        } else {
            return false;
        }
    }

    final public static boolean checkThemeMainSupported(Context context) {

        if (ColorChangerFragment.checkbitphone()) {
            vendor = "/vendor/overlay";
            mount = "/vendor";
        }
        File f2 = new File(vendor + "Akzent_Framework.apk");
        if (f2.exists()) {
            return true;
        }
        return false;
    }

    final public static boolean checkThemeSysSupported(Context context) {

        if (ColorChangerFragment.checkbitphone()) {
            vendor = "/vendor/overlay";
            mount = "/vendor";
        }
        File f2 = new File(vendor + "Akzent_SystemUI.apk");
        if (f2.exists()) {
            return true;
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
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
        if (checkRomSupported(getActivity())) {
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.spin);
        } else {
            anim2 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        }
        anim2.reset();
        final ImageView iv2 = (ImageView) inflation.findViewById(R.id.spinnerWheel);
        iv2.clearAnimation();
        iv2.startAnimation(anim2);

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (checkRomSupported(getActivity())) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(getResources().getString(R.string.homepage_rom_supported));
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_supported));
        }

        TextView theme_message = (TextView) inflation.findViewById(R.id.theme_message);
        if (checkThemeMainSupported(getActivity()) && checkThemeSysSupported(getActivity())) {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            theme_message.setText(getResources().getString(R.string.homepage_theme_full_supported));
        } else if (checkThemeMainSupported(getActivity())) {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_half_supported));
        } else if (checkThemeSysSupported(getActivity())) {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_half_supported));
        } else {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_not_supported));
        }

        if (checkThemeMainSupported(getActivity()) && checkThemeSysSupported(getActivity())) {
            Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                    getResources().
                            getString(R.string.
                                    homepage_dashboard_app_development_status_default_username))
                            + getResources().
                            getString(R.string.homepage_dashboard_app_development_status)
                            + " (" + BuildConfig.VERSION_NAME + ")",
                    Snackbar.LENGTH_SHORT);
            snack.show();
        } else if (checkThemeMainSupported(getActivity())) {
            Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                    getResources().
                            getString(R.string.
                                    homepage_dashboard_app_development_status_default_username))
                            + getResources().
                            getString(R.string.homepage_dashboard_app_development_status)
                            + " (" + BuildConfig.VERSION_NAME + ")",
                    Snackbar.LENGTH_SHORT);
            snack.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(inflation, getResources().getString(R.string.theme_half_snack), Snackbar.LENGTH_INDEFINITE).setAction("Install", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager","com.lovejoy777.rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        } else if (checkThemeSysSupported(getActivity())) {
            Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                    getResources().
                            getString(R.string.
                                    homepage_dashboard_app_development_status_default_username))
                            + getResources().
                            getString(R.string.homepage_dashboard_app_development_status)
                            + " (" + BuildConfig.VERSION_NAME + ")",
                    Snackbar.LENGTH_SHORT);
            snack.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(inflation, getResources().getString(R.string.theme_half_snack), Snackbar.LENGTH_INDEFINITE).setAction("Install", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager","com.lovejoy777.rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        } else {
            Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                    getResources().
                            getString(R.string.
                                    homepage_dashboard_app_development_status_default_username))
                            + getResources().
                            getString(R.string.homepage_dashboard_app_development_status)
                            + " (" + BuildConfig.VERSION_NAME + ")",
                    Snackbar.LENGTH_SHORT);
            snack.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(inflation, getResources().getString(R.string.theme_not_snack), Snackbar.LENGTH_INDEFINITE).setAction("Install", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager","com.lovejoy777.rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        }

        Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                getResources().
                        getString(R.string.
                                homepage_dashboard_app_development_status_default_username))
                        + getResources().
                        getString(R.string.homepage_dashboard_app_development_status)
                        + " (" + BuildConfig.VERSION_NAME + ")",
                Snackbar.LENGTH_SHORT);
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
