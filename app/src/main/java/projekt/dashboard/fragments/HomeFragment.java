package projekt.dashboard.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import projekt.dashboard.BuildConfig;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.ui.SettingsActivity;
import projekt.dashboard.ui.SplashScreenActivity;

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

    final public String checkRomSupported(Context context) {
        if (getProp("ro.aicp.device") != "") {
            current_rom = "AICP";
            return "AICP ✓";
        }
        if (getProp("ro.aosip.version") != "") {
            current_rom = "AOSiP";
            return "AOSiP ✓";
        }
        if (getProp("ro.bliss.device") != "") {
            current_rom = "Bliss";
            return "Bliss ✓";
        }
        if (getProp("ro.cm.device") != "") {
            current_rom = "CyanogenMod";
            return "CyanogenMod ✓";
        }
        if (getProp("ro.du.device") != "") {
            current_rom = "Dirty Unicorns";
            return "Dirty Unicorns ✓";
        }
        if (getProp("ro.to.version") != "") {
            current_rom = "OctOS";
            return "OctOS ✓";
        }
        if (getProp("ro.pac.version") != "") {
            current_rom = "PAC-ROM";
            return "PAC-ROM ✓";
        }
        if (getProp("ro.purenexus.version") != "") {
            current_rom = "PureNexus";
            return "Pure Nexus ✓";
        }
        if (getProp("ro.rr.device") != "") {
            current_rom = "Resurrection Remix";
            return "Resurrection Remix ✓";
        }
        if (getProp("ro.screwd.device") != "") {
            current_rom = "Screw'd";
            return "Screw'd Android ✓";
        }
        if (getProp("ro.validus.version") != "") {
            current_rom = "Validus";
            return "Validus ✓";
        } else {
            if (isAppInstalled(context, "org.cyanogenmod.theme.chooser")) {
                current_rom = "Generic CMTE Based ROM";
                return "cm_based_rom";
            } else {
                current_rom = null;
                return null;
            }
        }
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
                        "http://forum.xda-developers.com/android/themes/cdt-projektdashboard-t3348297"));
                startActivity(xda);
                break;
            case R.id.item3:
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

        TextView status_message = (TextView) inflation.findViewById(R.id.status_message);
        if (checkRomSupported(getActivity()) == null) {
            status_message.setTextColor(getResources().getColor(R.color.attention_color));
            status_message.setText(getResources().getString(R.string.homepage_rom_not_supported));
        }
        if (checkRomSupported(getActivity()) == "cm_based_rom") {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_orange));
            status_message.setText(getResources().getString(
                    R.string.homepage_rom_not_officially_supported));
        } else {
            status_message.setTextColor(getResources().getColor(R.color.attention_color_green));
            status_message.setText(checkRomSupported(getActivity()));
        }

        TextView usernameMessage = (TextView) inflation.findViewById(R.id.usernameMessage);
        usernameMessage.setText(getResources().getString(R.string.username_welcome) + ", " +
                prefs.getString("dashboard_username", getResources().getString(R.string.
                        homepage_dashboard_app_development_status_default_username)) + "!");

        final String[] myStrings = {
                "dashboard." + " (" + BuildConfig.VERSION_NAME + ")",
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
                            getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(appendedStrings);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(
                            getResources().getString(
                                    R.string.copied_system_info), appendedStrings);
                    clipboard.setPrimaryClip(clip);
                }
                Toast toast = Toast.makeText(
                        getContext(),
                        getResources().getString(R.string.copied_system_info_to_clipboard),
                        Toast.LENGTH_LONG);
                toast.show();

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
        return R.string.home;
    }
}
