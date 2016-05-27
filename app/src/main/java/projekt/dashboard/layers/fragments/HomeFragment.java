package projekt.dashboard.layers.fragments;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projekt.dashboard.layers.BuildConfig;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.ui.SettingsActivity;
import projekt.dashboard.layers.ui.SplashScreenActivity;
import projekt.dashboard.layers.util.LayersFunc;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    public SharedPreferences prefs;
    public String current_rom;
    @Bind(R.id.tapBarMenu)
    TapBarMenu tapBarMenu;

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

    @OnClick({R.id.item1, R.id.item2, R.id.item3, R.id.item4})
    public void onMenuItemClick(View view) {
        switch (view.getId()) {
            case R.id.item1:
                Intent gplus = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://plus.google.com/communities/104086528025432169285"));
                startActivity(gplus);
                break;
            case R.id.item2:
                Intent xda = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "http://forum.xda-developers" +
                                ".com/android/themes/cdt-projektdashboard-t3348297"));
                startActivity(xda);
                break;
            case R.id.item3:
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                        getActivity());
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.name_picker_dialog, null);
                alert.setView(dialogView);
                final AnimatedEditText textBox = (AnimatedEditText) dialogView.findViewById(R.id
                        .editText);

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
            case R.id.item4:
                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                tapBarMenu.close();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_homepage, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Animation anim2;

        final ImageView iv2 = (ImageView) inflation.findViewById(R.id.spinnerWheel);
        iv2.setImageDrawable(getResources().getDrawable(R.drawable.rainbow_logo));
        anim2 = AnimationUtils.loadAnimation(getActivity(), R.anim.spin_faster);

        anim2.reset();

        iv2.clearAnimation();
        iv2.startAnimation(anim2);

        final String[] myStrings = {
                "dashboard.layers" + " (" + BuildConfig.VERSION_NAME + ")",
                current_rom + " " + getProp("ro.build.version.release") +
                        " (" + getProp("ro.build.id") + ")",
                getProp("ro.product.manufacturer") + " " + getProp("ro.product.model") +
                        " (" + getProp("ro.product.name") + ")",
                getProp("ro.sf.lcd_density") + "DPI"};

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        String parsedDate = "";
        try {
            convertedDate = dateFormat.parse(getProp("ro.build.version.security_patch"));
            parsedDate = convertedDate.toString().substring(4, 10) + ", " +
                    convertedDate.toString().substring(
                            convertedDate.toString().length() - 4,
                            convertedDate.toString().length());
        } catch (ParseException e) {
        }
        // Introduce Xposed Framework Checker

        String xposed_version = "";

        File f = new File("/system/framework/XposedBridge.jar");
        if (f.exists() && !f.isDirectory()) {
            File file = new File("/system/", "xposed.prop");
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String unparsed_br = br.readLine();
                xposed_version = unparsed_br.substring(8, 10);
            } catch (FileNotFoundException e) {
                Log.e("XposedChecker", "'xposed.prop' could not be found!");
            } catch (IOException e) {
                Log.e("XposedChecker", "Unable to parse BufferedReader from 'xposed.prop'");
            }
            xposed_version = ", " + "Xposed Framework" + " (" + xposed_version + ")";
        }

        final String appendedStrings = Arrays.toString(myStrings).replaceAll("\\[|\\]", "") +
                ", " +
                getResources().getString(R.string.vendor_fingerprint) + " " +
                parsedDate + xposed_version;

        TextView marqueeText = (TextView) inflation.findViewById(R.id.MarqueeText);

        marqueeText.setText(appendedStrings);

        marqueeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                            getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(appendedStrings);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(
                            getResources().getString(
                                    R.string.copied_system_info), appendedStrings);
                    clipboard.setPrimaryClip(clip);
                }
                Toast toast = Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.copied_system_info_to_clipboard),
                        Toast.LENGTH_LONG);
                toast.show();

            }
        });

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (new LayersFunc(getActivity()).checkLayersInstalled(getActivity())) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(getResources().getString(R.string.homepage_rom_supported));
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_supported));
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Layers ROM missing");
            ad.setMessage("you are missing one of the below prerequisites:-\n1. Layers Rom\n2. " +
                    "Layers Manager");
            ad.setPositiveButton("Download Layers Manager", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent_rrolayers = new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("https://play.google.com/store/apps/details?id=com.lovejoy777" +
                                    ".rroandlayersmanager"));
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
        if (LayersFunc.themesystemui != "Nill" && LayersFunc.themeframework != "Nill" &&
                LayersFunc.downloaded) {
            if (new LayersFunc(getActivity()).checkThemeMainSupported(getActivity()) && new
                    LayersFunc(getActivity()).checkThemeSysSupported(getActivity())) {
                theme_message.setTextColor(getResources().getColor(R.color.attention_color_green));
                theme_message.setText(getResources().getString(R.string
                        .homepage_theme_full_supported));
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
                ad.setMessage("Changing colors over the fly ,without reboot.Seems fictional " +
                        "right?But let me tell you something,its possible using akzent.");
                ad.setPositiveButton("Download Akzent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com" +
                                ".chummy.aditya.materialdark.layers.donate")) {
                            startActivity(new Intent().setComponent(new ComponentName("com" +
                                    ".lovejoy777.rroandlayersmanager", "com.lovejoy777" +
                                    ".rroandlayersmanager.MainActivity")));
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play" +
                                    ".google.com/store/apps/details?id=com.chummy.aditya" +
                                    ".materialdark.layers.donate")));
                        }
                    }
                });
                ad.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
        } else if (new LayersFunc(getActivity()).checkThemeMainSupported(getActivity()) &&
                LayersFunc.downloaded) {
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
                    Snackbar.make(inflation, getResources().getString(R.string.theme_half_snack),
                            Snackbar.LENGTH_INDEFINITE).setAction("Install", new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com" +
                                    ".chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent(getActivity().getPackageManager()
                                        .getLaunchIntentForPackage("com.lovejoy777" +
                                                ".rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                        ("https://play.google.com/store/apps/details?id=com" +
                                                ".chummy.aditya.materialdark.layers.donate")));
                            }
                        }
                    }).show();
                }
            }, Snackbar.LENGTH_SHORT + 1000);
        } else if (new LayersFunc(getActivity()).checkThemeSysSupported(getActivity()) &&
                LayersFunc.downloaded) {
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
                    Snackbar.make(inflation, getResources().getString(R.string.theme_half_snack),
                            Snackbar.LENGTH_INDEFINITE).setAction("Install", new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com" +
                                    ".chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent(getActivity().getPackageManager()
                                        .getLaunchIntentForPackage("com.lovejoy777" +
                                                ".rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                        ("https://play.google.com/store/apps/details?id=com" +
                                                ".chummy.aditya.materialdark.layers.donate")));
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
                    Snackbar.make(inflation, getResources().getString(R.string.theme_not_snack),
                            Snackbar.LENGTH_INDEFINITE).setAction("Install", new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (new LayersFunc(getActivity()).isAppInstalled(getActivity(), "com" +
                                    ".chummy.aditya.materialdark.layers.donate")) {
                                startActivity(new Intent(getActivity().getPackageManager()
                                        .getLaunchIntentForPackage("com.lovejoy777" +
                                                ".rroandlayersmanager")));
                            } else {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                        ("https://play.google.com/store/apps/details?id=com" +
                                                ".chummy.aditya.materialdark.layers.donate")));
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
