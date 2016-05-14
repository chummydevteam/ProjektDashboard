package projekt.dashboard.layers.fragments;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.StringTokenizer;

import butterknife.ButterKnife;
import projekt.dashboard.layers.BuildConfig;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.util.LayersFunc;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {
    public static String vendor = "/system/vendor/overlay";
    public static String mount = "/system";
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_homepage, container, false);


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Animation anim2;
        if (new LayersFunc(getActivity()).checkLayersInstalled(getActivity())) {
            anim2 = AnimationUtils.loadAnimation(getActivity(), R.anim.spin);
        } else {
            anim2 = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        }
        anim2.reset();
        final ImageView iv2 = (ImageView) inflation.findViewById(R.id.spinnerWheel);
        iv2.clearAnimation();
        iv2.startAnimation(anim2);

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (new LayersFunc(getActivity()).checkLayersInstalled(getActivity())) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(getResources().getString(R.string.homepage_rom_supported));
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_supported));
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Layers ROM missing");
            ad.setMessage("you are missing one of the below prerequisites:-\n1. Layers Rom\n2. Layers Manager");
            ad.setPositiveButton("Download Layers Manager", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent_rrolayers = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lovejoy777.rroandlayersmanager"));
                    startActivity(intent_rrolayers);
                }
            });
            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }

        TextView theme_message = (TextView) inflation.findViewById(R.id.theme_message);
        if (LayersFunc.themesystemui != "Nill" && LayersFunc.themeframework != "Nill" && LayersFunc.downloaded) {
            if (new LayersFunc(getActivity()).checkThemeMainSupported(getActivity()) && new LayersFunc(getActivity()).checkThemeSysSupported(getActivity())) {
                theme_message.setTextColor(getResources().getColor(R.color.attention_color_green));
                theme_message.setText(getResources().getString(R.string.homepage_theme_full_supported));
                Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                        getResources().
                                getString(R.string.
                                        homepage_dashboard_app_development_status_default_username))
                                + getResources().
                                getString(R.string.homepage_dashboard_app_development_status)
                                + " (" + BuildConfig.VERSION_NAME + ")",
                        Snackbar.LENGTH_SHORT);
                snack.show();
            } else {
                theme_message.setTextColor(getResources().getColor(R.color.attention_color_green));
                String themename = LayersFunc.themeframework;
                StringTokenizer stringTokenizer = new StringTokenizer(themename, "_");
                theme_message.setText(stringTokenizer.nextToken() + " Detected !!");
                Snackbar snack = Snackbar.make(inflation, prefs.getString("dashboard_username",
                        getResources().
                                getString(R.string.
                                        homepage_dashboard_app_development_status_default_username))
                                + getResources().
                                getString(R.string.homepage_dashboard_app_development_status)
                                + " (" + BuildConfig.VERSION_NAME + ")",
                        Snackbar.LENGTH_SHORT);
                snack.show();
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setTitle("Theme other than Akzent is found");
                ad.setMessage("Changing colors over the fly ,without reboot.Seems fictional right?But let me tell you something,its possible using akzent.");
                ad.setPositiveButton("Download Akzent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                            startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.MainActivity")));
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                        }
                    }
                });
                ad.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
        } else if (new LayersFunc(getActivity()).checkThemeMainSupported(getActivity()) && LayersFunc.downloaded) {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_half_supported));
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
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.MainActivity")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        } else if (new LayersFunc(getActivity()).checkThemeSysSupported(getActivity()) && LayersFunc.downloaded) {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_half_supported));
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
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.MainActivity")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        } else {
            theme_message.setTextColor(getResources().getColor(R.color.attention_color));
            theme_message.setText(getResources().getString(R.string.homepage_theme_not_supported));
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
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com.chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent().setComponent(new ComponentName("com.lovejoy777.rroandlayersmanager", "com.lovejoy777.rroandlayersmanager.MainActivity")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
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
