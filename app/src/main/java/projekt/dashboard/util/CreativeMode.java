package projekt.dashboard.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.widgets.AnimatedEditText;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import com.mutualmobile.cardstack.CardStackAdapter;
import com.tramsun.libs.prefcompat.Pref;

import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import kellinwood.security.zipsigner.ZipSigner;
import projekt.dashboard.BuildConfig;
import projekt.dashboard.R;
import projekt.dashboard.colorpicker.ColorPickerDialog;

public class CreativeMode extends CardStackAdapter implements
        CompoundButton.OnCheckedChangeListener {
    private static int[] bgColorIds;
    private final LayoutInflater mInflater;
    private final Context mContext;
    public Runnable updateSettingsView;
    public SharedPreferences prefs;
    public boolean colorful_icon = true;
    public int folder_directory = 1;
    public String current_cdt_theme;
    public Boolean did_it_compile = true;

    public CircularFillableLoaders loader;
    public TextView loader_string;

    public android.support.v7.widget.Toolbar framework_toolbar;
    public android.support.v7.widget.Toolbar settings_toolbar;
    public String header_pack_location = "";
    public View main_color_dark_view;

    public Boolean header_creative_mode_activated = false;
    public String header_pack_name, header_pack_author;

    public ImageView accent_universal, accent_secondary, accent_light, appbg_dark, appbg_light, dialog_dark, dialog_light, main_color, main_color_dark, notifications_primary, notifications_secondary, ripples;
    public Switch colorful_icon_switch, categories_title_caps, categories_title_bold, categories_title_italics, dashboard_divider, dutweaks_icons;
    public ImageView settings_dashboard_background_color, settings_dashboard_category_background_color, settings_icon_colors, settings_title_colors, settings_switchbar_background_color;
    public ImageView qs_accents, qs_header, qs_notification, qs_panel_bg, qs_tile, qs_text, qs_recents;
    public AnimatedEditText aet1, aet2;
    public Switch themable_gapps;

    public CardView framework_card, settings_card, systemui_card, final_card;

    // ==================================== Framework Tweaks ================================ //
    public int current_selected_system_accent_color = Color.argb(255, 255, 255, 255); // White
    public int current_selected_system_accent_dual_color = Color.argb(255, 119, 119, 119); // Medium Grey
    public int current_selected_system_accent_light_color = Color.argb(255, 119, 119, 119); // Medium grey
    public int current_selected_system_appbg_color = Color.argb(255, 0, 0, 0); // Black
    public int current_selected_system_appbg_light_color = Color.argb(255, 215, 215, 215); // Light grey
    public int current_selected_system_dialog_color = Color.argb(191, 0, 0, 0); // Black with Transparency
    public int current_selected_system_dialog_light_color = Color.argb(191, 238, 238, 238); // Light Grey with Transparency
    public int current_selected_system_notifications_primary_color = Color.argb(255, 255, 255, 255); // White
    public int current_selected_system_notifications_secondary_color = Color.argb(255, 174, 174, 174); // Lighter grey
    public int current_selected_system_ripple_color = Color.argb(74, 119, 119, 119); // Medium Grey with Transparency
    public int current_selected_system_main_color = Color.argb(255, 33, 32, 33); // Main theme color
    public int current_selected_system_main_dark_color = Color.argb(255, 33, 32, 33); // Main theme color dark
    // ==================================== Settings Tweaks ================================== //
    public boolean category_title_caps = true;
    public boolean category_title_bold = true;
    public boolean category_title_italics = true;
    public boolean dashboard_dividers = true;
    public boolean dirtytweaks_icon_presence = false;
    public int current_selected_dashboard_background_color = Color.argb(255, 33, 32, 33);
    public int current_selected_dashboard_category_background_color = Color.argb(255, 0, 0, 0);
    public int current_selected_settings_icon_color = Color.argb(255, 255, 255, 255);
    public int current_selected_settings_title_color = Color.argb(255, 255, 255, 255);
    public int current_selected_settings_switchbar_color = Color.argb(255, 55, 55, 55);
    // ==================================== SystemUI Tweaks ================================== //
    public int current_selected_header_background_color = Color.argb(255, 31, 31, 31);
    public int current_selected_notification_background_color = Color.argb(229, 33, 32, 33);
    public int current_selected_qs_panel_background_color = Color.argb(204, 0, 0, 0);
    public int current_selected_qs_accent_color = Color.argb(255, 255, 255, 255);
    public int current_selected_qs_tile_color = Color.argb(255, 255, 255, 255);
    public int current_selected_qs_text_color = Color.argb(255, 255, 255, 255);
    public int current_selected_recents_clear_all_icon_color = Color.argb(255, 255, 255, 255);
    public String spinnerItem, themeName, themeAuthor;
    // ========================= On decision based color injection =========================== //
    public Boolean is_framework_accent_changed = false;
    public Boolean is_framework_accent_secondary_changed = false;
    public Boolean is_framework_accent_light_changed = false;
    public Boolean is_framework_app_background_changed = false;
    public Boolean is_framework_app_background_light_changed = false;
    public Boolean is_framework_dialog_background_dark_changed = false;
    public Boolean is_framework_dialog_background_light_changed = false;
    public Boolean is_framework_main_theme_color_changed = false;
    public Boolean is_framework_main_theme_color_dark_changed = false;
    public Boolean is_framework_notifications_primary_changed = false;
    public Boolean is_framework_notifications_secondary_changed = false;
    public Boolean is_framework_system_ripple_changed = false;
    public Boolean is_settings_colorful_icon_color_changed = false;
    public Boolean is_settings_dashboard_background_color_changed = false;
    public Boolean is_settings_dashboard_category_background_color_changed = false;
    public Boolean is_settings_dashboard_category_title_caps_changed = false;
    public Boolean is_settings_dashboard_category_title_bold_changed = false;
    public Boolean is_settings_dashboard_category_title_italics_changed = false;
    public Boolean is_settings_dashboard_dividers_changed = false;
    public Boolean is_settings_dashboard_dirty_tweaks_icon_presence_changed = false;
    public Boolean is_settings_icon_color_changed = false;
    public Boolean is_settings_title_color_changed = false;
    public Boolean is_settings_switchbar_background_color_changed = false;
    public Boolean is_header_pack_chosen = false;
    public Boolean is_systemui_header_color_changed = false;
    public Boolean is_systemui_notification_background_color_changed = false;
    public Boolean is_systemui_qs_panel_background_color_changed = false;
    public Boolean is_systemui_accent_color_changed = false;
    public Boolean is_systemui_qs_tile_color_changed = false;
    public Boolean is_systemui_qs_text_color_changed = false;
    public Boolean is_systemui_recents_clear_all_icon_color_changed = false;
    public Boolean use_themable_gapps = false;
    public Boolean use_themable_gapps_changed = false;

    ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    private Logger log = new Logger(CreativeMode.class.getSimpleName());

    public CreativeMode(Activity activity) {
        super(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
        bgColorIds = new int[]{
                R.color.card1_bg, // Framework
                R.color.card2_bg, // Settings
                R.color.card3_bg, // SystemUI
                R.color.card4_bg, // Final Card
        };
    }

    public void cleanTempFolder() {
        File dir = mContext.getCacheDir();
        deleteRecursive(dir);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public int getCount() {
        return bgColorIds.length;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        log.d("onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], " +
                "isChecked = [" + isChecked + "]");
        Pref.putBoolean(CardStackPrefs.PARALLAX_ENABLED, isChecked);
        Pref.putBoolean(CardStackPrefs.SHOW_INIT_ANIMATION, isChecked);
        updateSettingsView.run();
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View createView(int position, ViewGroup container) {
        if (position == 0) return getFrameworksView(container);
        if (position == 1) return getSettingsView(container);
        if (position == 2) return getSystemUIView(container);
        if (position == 3) return getFinalizedView(container);

        CardView root = (CardView) mInflater.inflate(R.layout.card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[position]));
        TextView cardTitle = (TextView) root.findViewById(R.id.card_title);
        cardTitle.setText(mContext.getString(R.string.card_title, position));

        return root;
    }

    public boolean checkCurrentThemeSelection(String packageName) {
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, 0);
            File directory1 = new File("/data/app/" + packageName + "-1/base.apk");
            if (directory1.exists()) {
                folder_directory = 1;
                return true;
            } else {
                File directory2 = new File("/data/app/" + packageName + "-2/base.apk");
                if (directory2.exists()) {
                    folder_directory = 2;
                    return true;
                } else {
                    File directory3 = new File("/data/app/" + packageName + "-3/base.apk");
                    if (directory3.exists()) {
                        folder_directory = 3;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public int checkCurrentThemeSelectionLocation(String packageName) {
        try {
            mContext.getPackageManager().getApplicationInfo(packageName, 0);
            File directory1 = new File("/data/app/" + packageName + "-1/base.apk");
            if (directory1.exists()) {
                return 1;
            } else {
                File directory2 = new File("/data/app/" + packageName + "-2/base.apk");
                if (directory2.exists()) {
                    return 2;
                } else {
                    File directory3 = new File("/data/app/" + packageName + "-3/base.apk");
                    if (directory3.exists()) {
                        return 3;
                    } else {
                        return 0;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private View getFrameworksView(ViewGroup container) {
        framework_card = (CardView) mInflater.inflate(R.layout.framework_card, container, false);
        framework_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[0]));

        framework_toolbar = (android.support.v7.widget.Toolbar)
                framework_card.findViewById(R.id.framework_toolbar);
        framework_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);

        main_color_dark_view = framework_card.findViewById(R.id.main_color_dark_colorpicker);

        final Switch switch1 = (Switch) framework_card.findViewById(R.id.switch_example);
        final Switch switch2 = (Switch) framework_card.findViewById(R.id.switch_example2);

        final RelativeLayout rl = (RelativeLayout) framework_card.findViewById(R.id.main_relativeLayout);

        // Framework Accent (universal)

        accent_universal = (ImageView) framework_card.findViewById(
                R.id.system_accent_colorpicker);
        final TextView accent_universal_text = (TextView) framework_card.findViewById(
                R.id.system_accent_colorpicker_text);
        accent_universal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_accent_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_accent_color = color;
                        accent_universal.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        ColorStateList csl = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_checked},
                                        new int[]{}
                                },
                                new int[]{
                                        color, color
                                }
                        );
                        switch1.setTrackTintList(csl);
                        switch1.setThumbTintList(csl);
                        is_framework_accent_changed = true;
                        accent_universal_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Accent (dual)

        accent_secondary = (ImageView) framework_card.findViewById(
                R.id.system_accent_dual_colorpicker);
        final TextView accent_secondary_text = (TextView) framework_card.findViewById(
                R.id.system_accent_dual_colorpicker_text);
        accent_secondary.setColorFilter(
                current_selected_system_accent_dual_color, PorterDuff.Mode.SRC_ATOP);
        accent_secondary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_accent_dual_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_accent_dual_color = color;
                        accent_secondary.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_accent_secondary_changed = true;
                        accent_secondary_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Accent (light)

        accent_light = (ImageView) framework_card.findViewById(
                R.id.system_accent_light_colorpicker);
        final TextView accent_light_text = (TextView) framework_card.findViewById(
                R.id.system_accent_light_colorpicker_text);
        accent_light.setColorFilter(
                current_selected_system_accent_light_color, PorterDuff.Mode.SRC_ATOP);
        accent_light.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_accent_light_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_accent_light_color = color;
                        accent_light.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        ColorStateList csl = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_checked},
                                        new int[]{}
                                },
                                new int[]{
                                        color, color
                                }
                        );
                        switch2.setTrackTintList(csl);
                        switch2.setThumbTintList(csl);
                        is_framework_accent_light_changed = true;
                        accent_light_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Appbg (dark)

        appbg_dark = (ImageView) framework_card.findViewById(
                R.id.system_appbg_colorpicker);
        final TextView appbg_dark_text = (TextView) framework_card.findViewById(
                R.id.system_appbg_colorpicker_text);
        rl.setBackgroundColor(current_selected_system_appbg_color);
        appbg_dark.setColorFilter(current_selected_system_appbg_color, PorterDuff.Mode.SRC_ATOP);
        appbg_dark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_appbg_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_appbg_color = color;
                        appbg_dark.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        rl.setBackgroundColor(color);
                        is_framework_app_background_changed = true;
                        appbg_dark_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Appbg (light)

        appbg_light = (ImageView) framework_card.findViewById(
                R.id.system_appbg_light_colorpicker);
        final TextView appbg_light_text = (TextView) framework_card.findViewById(
                R.id.system_appbg_light_colorpicker_text);
        appbg_light.setColorFilter(
                current_selected_system_appbg_light_color, PorterDuff.Mode.SRC_ATOP);
        switch2.setBackgroundColor(current_selected_system_appbg_light_color);
        appbg_light.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_appbg_light_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_appbg_light_color = color;
                        appbg_light.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        switch2.setBackgroundColor(color);
                        is_framework_app_background_light_changed = true;
                        appbg_light_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework System Dialog Color (dark)

        dialog_dark = (ImageView) framework_card.findViewById(
                R.id.system_dialog_colorpicker);
        final TextView dialog_dark_text = (TextView) framework_card.findViewById(
                R.id.system_dialog_colorpicker_text);
        dialog_dark.setColorFilter(current_selected_system_dialog_color, PorterDuff.Mode.SRC_ATOP);
        dialog_dark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_dialog_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_dialog_color = color;
                        dialog_dark.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_dialog_background_dark_changed = true;
                        dialog_dark_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework System Dialog Color (light)

        dialog_light = (ImageView) framework_card.findViewById(
                R.id.system_dialog_light_colorpicker);
        final TextView dialog_light_text = (TextView) framework_card.findViewById(
                R.id.system_dialog_light_colorpicker_text);
        dialog_light.setColorFilter(
                current_selected_system_dialog_light_color, PorterDuff.Mode.SRC_ATOP);
        dialog_light.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_dialog_light_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_dialog_light_color = color;
                        dialog_light.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_dialog_background_light_changed = true;
                        dialog_light_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework System Main Color

        main_color = (ImageView) framework_card.findViewById(
                R.id.system_main_colorpicker);
        final TextView main_color_text = (TextView) framework_card.findViewById(
                R.id.system_main_colorpicker_text);
        main_color.setColorFilter(current_selected_system_main_color, PorterDuff.Mode.SRC_ATOP);
        main_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_main_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_main_color = color;
                        main_color.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        framework_toolbar.setBackgroundColor(color);
                        settings_toolbar.setBackgroundColor(color);
                        is_framework_main_theme_color_changed = true;
                        main_color_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework System Main Color Dark

        main_color_dark = (ImageView) framework_card.findViewById(
                R.id.system_main_dark_colorpicker);
        final TextView main_color_dark_text = (TextView) framework_card.findViewById(
                R.id.system_main_dark_colorpicker_text);
        main_color_dark.setColorFilter(current_selected_system_main_dark_color, PorterDuff.Mode.SRC_ATOP);
        main_color_dark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_main_dark_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_main_dark_color = color;
                        is_framework_main_theme_color_dark_changed = true;
                        main_color_dark_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Notifications Primary Color

        notifications_primary = (ImageView) framework_card.findViewById(
                R.id.system_notification_text_1_colorpicker);
        final TextView notifications_primary_text = (TextView) framework_card.findViewById(
                R.id.system_notification_text_1_colorpicker_text);
        notifications_primary.setColorFilter(
                current_selected_system_notifications_primary_color, PorterDuff.Mode.SRC_ATOP);
        notifications_primary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_notifications_primary_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_notifications_primary_color = color;
                        notifications_primary.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_notifications_primary_changed = true;
                        notifications_primary_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Notifications Secondary Color

        notifications_secondary = (ImageView) framework_card.findViewById(
                R.id.system_notification_text_2_colorpicker);
        final TextView notifications_secondary_text = (TextView) framework_card.findViewById(
                R.id.system_notification_text_2_colorpicker_text);
        notifications_secondary.setColorFilter(
                current_selected_system_notifications_secondary_color, PorterDuff.Mode.SRC_ATOP);
        notifications_secondary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_notifications_secondary_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_notifications_secondary_color = color;
                        notifications_secondary.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_notifications_secondary_changed = true;
                        notifications_secondary_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Framework Ripple Color

        ripples = (ImageView) framework_card.findViewById(
                R.id.system_ripple_colorpicker);
        final TextView ripples_text = (TextView) framework_card.findViewById(
                R.id.system_ripple_colorpicker_text);
        ripples.setColorFilter(current_selected_system_ripple_color, PorterDuff.Mode.SRC_ATOP);
        ripples.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_system_ripple_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_system_ripple_color = color;
                        ripples.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_framework_system_ripple_changed = true;
                        ripples_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });


        return framework_card;
    }

    private View getSettingsView(ViewGroup container) {
        settings_card = (CardView) mInflater.inflate(R.layout.settings_card, container, false);
        settings_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[1]));

        final ImageView wifiIcon = (ImageView) settings_card.findViewById(R.id.wifiIcon);
        final TextView categoryHeader = (TextView) settings_card.findViewById(R.id.categoryHeaderTitle);
        settings_toolbar = (android.support.v7.widget.Toolbar)
                settings_card.findViewById(R.id.settings_toolbar);

        // Colorful DU/PN Tweaks Icon

        colorful_icon_switch = (Switch) settings_card.findViewById(R.id.colorful_icon);
        colorful_icon_switch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            colorful_icon = true;
                            Log.d("Switch Colorful Icon", colorful_icon + "");
                        } else {
                            colorful_icon = false;
                            Log.d("Switch Colorful Icon", colorful_icon + "");
                        }
                        is_settings_colorful_icon_color_changed = true;
                        colorful_icon_switch.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
        colorful_icon_switch.setVisibility(View.GONE);

        // Dashboard Categories Title (All Caps)

        categories_title_caps = (Switch) settings_card.findViewById(
                R.id.dashboard_title_allcaps);
        categories_title_caps.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            category_title_caps = true;
                            Log.d("Categories Title (Caps)", category_title_caps + "");
                        } else {
                            category_title_caps = false;
                            Log.d("Categories Title (Caps)", category_title_caps + "");
                        }
                        is_settings_dashboard_category_title_caps_changed = true;
                        categories_title_caps.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });

        // Dashboard Categories Title (Bold)

        categories_title_bold = (Switch) settings_card.findViewById(
                R.id.dashboard_title_bold);
        categories_title_bold.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            category_title_bold = true;
                            Log.d("Categories Title (Bold)", category_title_bold + "");
                        } else {
                            category_title_bold = false;
                            Log.d("Categories Title (Bold)", category_title_bold + "");
                        }
                        is_settings_dashboard_category_title_bold_changed = true;
                        categories_title_bold.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });

        // Dashboard Categories Title (Italics)

        categories_title_italics = (Switch) settings_card.findViewById(
                R.id.dashboard_title_italics);
        categories_title_italics.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            category_title_italics = true;
                            Log.d("Categories Title (Ita)", category_title_italics + "");
                        } else {
                            category_title_italics = false;
                            Log.d("Categories Title (Ita)", category_title_italics + "");
                        }
                        is_settings_dashboard_category_title_italics_changed = true;
                        categories_title_italics.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });

        // Dashboard Dividers

        dashboard_divider = (Switch) settings_card.findViewById(R.id.dashboard_dividers);
        dashboard_divider.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dashboard_dividers = true;
                            Log.d("Dashboard Dividers", dashboard_dividers + "");
                        } else {
                            dashboard_dividers = false;
                            Log.d("Dashboard Dividers", dashboard_dividers + "");
                        }
                        is_settings_dashboard_dividers_changed = true;
                        dashboard_divider.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });

        // Dirty Tweaks Icon Presence

        dutweaks_icons = (Switch) settings_card.findViewById(R.id.dirty_tweaks_icons);
        dutweaks_icons.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dirtytweaks_icon_presence = true;
                            Log.d("DU Tweaks Icon", dirtytweaks_icon_presence + "");
                        } else {
                            dirtytweaks_icon_presence = false;
                            Log.d("DU Tweaks Icon", dirtytweaks_icon_presence + "");
                        }
                        is_settings_dashboard_dirty_tweaks_icon_presence_changed = true;
                        dutweaks_icons.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });

        // Settings Dashboard Background Color

        settings_dashboard_background_color = (ImageView) settings_card.findViewById(
                R.id.settings_dashboard_background_colorpicker);
        final TextView settings_dashboard_background_color_text = (TextView) settings_card.findViewById(
                R.id.settings_dashboard_background_colorpicker_text);
        settings_dashboard_background_color.setColorFilter(current_selected_dashboard_background_color, PorterDuff.Mode.SRC_ATOP);
        settings_dashboard_background_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_dashboard_background_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_dashboard_background_color = color;
                        settings_dashboard_background_color.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_settings_dashboard_background_color_changed = true;
                        settings_dashboard_background_color_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Settings Dashboard Category Background Color

        settings_dashboard_category_background_color = (ImageView) settings_card.findViewById(
                R.id.settings_dashboard_category_colorpicker);
        final TextView settings_dashboard_category_background_color_text = (TextView) settings_card.findViewById(
                R.id.settings_dashboard_category_colorpicker_text);
        final RelativeLayout settings_preview = (RelativeLayout) settings_card.findViewById(R.id.settings_container);
        settings_dashboard_category_background_color.setColorFilter(current_selected_dashboard_category_background_color,
                PorterDuff.Mode.SRC_ATOP);
        settings_dashboard_category_background_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_dashboard_category_background_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_dashboard_category_background_color = color;
                        settings_dashboard_category_background_color.setColorFilter(color,
                                PorterDuff.Mode.SRC_ATOP);
                        settings_preview.setBackgroundColor(color);
                        is_settings_dashboard_category_background_color_changed = true;
                        settings_dashboard_category_background_color_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Settings Icons Colors

        settings_icon_colors = (ImageView) settings_card.findViewById(
                R.id.settings_icon_colorpicker);
        final TextView settings_icon_colors_text = (TextView) settings_card.findViewById(
                R.id.settings_icon_colorpicker_text);
        settings_icon_colors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_settings_icon_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_settings_icon_color = color;
                        settings_icon_colors.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        wifiIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_settings_icon_color_changed = true;
                        settings_icon_colors_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Settings Title Colors

        settings_title_colors = (ImageView) settings_card.findViewById(
                R.id.settings_title_colorpicker);
        final TextView settings_title_colors_text = (TextView) settings_card.findViewById(
                R.id.settings_title_colorpicker_text);
        settings_title_colors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_settings_title_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_settings_title_color = color;
                        settings_title_colors.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        categoryHeader.setTextColor(color);
                        is_settings_title_color_changed = true;
                        settings_title_colors_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // Settings Switchbar Background Color

        settings_switchbar_background_color = (ImageView) settings_card.findViewById(
                R.id.settings_switchbar_background_colorpicker);
        final TextView settings_switchbar_background_color_text = (TextView) settings_card.findViewById(
                R.id.settings_switchbar_background_colorpicker_text);
        settings_switchbar_background_color.setColorFilter(current_selected_settings_switchbar_color, PorterDuff.Mode.SRC_ATOP);
        settings_switchbar_background_color.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_settings_switchbar_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_settings_switchbar_color = color;
                        settings_switchbar_background_color.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_settings_switchbar_background_color_changed = true;
                        settings_switchbar_background_color_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        return settings_card;
    }

    public void checkWhetherZIPisValid(CardView root, String source, String destination) {

        try {
            net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(source);
            Log.d("Unzip", "The ZIP has been located and will now be unzipped...");
            zipFile.extractAll(destination);
            Log.d("Unzip",
                    "Successfully unzipped the file to the corresponding directory!");

            String[] checkerCommands = {destination + "/headers.xml"};
            String[] newArray = ReadXMLFile.main(checkerCommands);

            header_pack_location = source;
            is_header_pack_chosen = true;

            TextView headerPackName = (TextView) root.findViewById(R.id.themeName2);
            headerPackName.setText(newArray[0]);
            header_pack_name = newArray[0];

            TextView headerPackAuthor = (TextView) root.findViewById(R.id.themeAuthor2);
            headerPackAuthor.setText(newArray[1]);
            header_pack_author = newArray[1];

            TextView headerPackDevTeam = (TextView) root.findViewById(R.id.themeDevTeam2);
            headerPackDevTeam.setText(newArray[2]);

            TextView headerPackVersion = (TextView) root.findViewById(R.id.themeVersion2);
            headerPackVersion.setText(newArray[3]);

            TextView headerPackCount = (TextView) root.findViewById(R.id.themeCount2);
            int how_many_themed = countPNGs();
            if (how_many_themed == 10) {
                headerPackCount.setText(mContext.getResources().getString(
                        R.string.contextualheaderimporter_all_themed));
            } else {
                if (how_many_themed == 1) {
                    headerPackCount.setText(
                            how_many_themed + " " + mContext.getResources().getString(
                                    R.string.contextualheaderimporter_only_one_themed));
                } else {
                    headerPackCount.setText(
                            how_many_themed + " " + mContext.getResources().getString(
                                    R.string.contextualheaderimporter_not_all_themed));
                }
            }

            cleanTempFolder();

        } catch (ZipException e) {
            Log.d("Unzip",
                    "Failed to unzip the file the corresponding directory. (EXCEPTION)");
            e.printStackTrace();
        }
    }

    public int countPNGs() {
        int count = 0;

        List<String> filenamePNGs = Arrays.asList(
                "notifhead_afternoon.png", "notifhead_christmas.png", "notifhead_morning.png",
                "notifhead_newyearseve.png", "notifhead_night.png", "notifhead_noon.png",
                "notifhead_sunrise.png", "notifhead_sunset_hdpi.png",
                "notifhead_sunset_xhdpi.png", "notifhead_sunset.png");

        File f2 = new File(
                mContext.getCacheDir().getAbsolutePath() + "/headers/");
        File[] files2 = f2.listFiles();
        if (files2 != null) {
            for (File inFile2 : files2) {
                if (inFile2.isFile()) {
                    // Filter out filenames of which were unzipped earlier
                    String filenameParse[] = inFile2.getAbsolutePath().split("/");
                    String filename = filenameParse[filenameParse.length - 1];

                    if (filenamePNGs.contains(filename)) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    private View getSystemUIView(ViewGroup container) {
        systemui_card = (CardView) mInflater.inflate(R.layout.systemui_card, container, false);
        systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final TextView wifiLabel = (TextView) systemui_card.findViewById(R.id.wifiLabel);
        final TextView bluetoothLabel = (TextView) systemui_card.findViewById(R.id.bluetoothLabel);
        wifiLabel.setText(prefs.getString("dashboard_username",
                systemui_card.getResources().getString(R.string.systemui_preview_default_no_username)) +
                systemui_card.getResources().getString(R.string.systemui_preview_label));
        final SeekBar brightness = (SeekBar) systemui_card.findViewById(R.id.seekBar);

        final Spinner spinner3 = (Spinner) systemui_card.findViewById(R.id.spinner3);

        List<String> zipsFound = new ArrayList<String>();
        zipsFound.add(mContext.getResources().getString(R.string.contextual_header_pack));

        // Function that filters out all zip files within /storage/0/dashboard., but not only that,
        // it checks the zip file and sees if there is headers.xml found inside so that it's a
        // filter.

        File f2 = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/dashboard./");
        File[] files2 = f2.listFiles();
        if (files2 != null) {
            for (File inFile2 : files2) {
                if (inFile2.isFile()) {
                    String filenameArray[] = inFile2.toString().split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    if (extension.equals("zip")) {
                        try {
                            String filenameParse[] = inFile2.getAbsolutePath().split("/");
                            String filename = filenameParse[filenameParse.length - 1];

                            ZipFile zipFile = new ZipFile(
                                    Environment.getExternalStorageDirectory().
                                            getAbsolutePath() + "/dashboard./" + filename);
                            ZipEntry entry = zipFile.getEntry("headers.xml");
                            if (entry != null) {
                                // headers.xml was found in the file, so add it into the spinner
                                zipsFound.add(filename);
                            }
                        } catch (IOException e) {
                            System.out.println(
                                    "There was an IOException within the filter function");
                        }
                    }
                }
            }
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, zipsFound);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos != 0) {
                    checkWhetherZIPisValid(systemui_card, Environment.getExternalStorageDirectory().
                                    getAbsolutePath() +
                                    "/dashboard./" + spinner3.getSelectedItem(),
                            mContext.getCacheDir().getAbsolutePath() + "/headers");
                } else {
                    header_pack_location = "";
                    is_header_pack_chosen = false;
                    TextView headerPackName = (TextView)
                            systemui_card.findViewById(R.id.themeName2);
                    headerPackName.setText(mContext.getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackAuthor = (TextView)
                            systemui_card.findViewById(R.id.themeAuthor2);
                    headerPackAuthor.setText(mContext.getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackDevTeam = (TextView)
                            systemui_card.findViewById(R.id.themeDevTeam2);
                    headerPackDevTeam.setText(mContext.getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackVersion = (TextView)
                            systemui_card.findViewById(R.id.themeVersion2);
                    headerPackVersion.setText(mContext.getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));

                    TextView headerPackCount = (TextView)
                            systemui_card.findViewById(R.id.themeCount2);
                    headerPackCount.setText(mContext.getResources().getString(
                            R.string.contextualheaderimporter_header_pack_na));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // Apply the adapter to the spinner
        spinner3.setAdapter(adapter3);

        // QS Accent Colors

        qs_accents = (ImageView) systemui_card.findViewById(R.id.qs_accent_colorpicker);
        final TextView qs_accents_text = (TextView) systemui_card.findViewById(
                R.id.qs_accent_colorpicker_text);
        qs_accents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_qs_accent_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_qs_accent_color = color;
                        qs_accents.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        ColorStateList csl = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_pressed},
                                        new int[]{android.R.attr.state_focused},
                                        new int[]{}
                                },
                                new int[]{
                                        color, color, color
                                }
                        );
                        brightness.setProgressTintList(csl);
                        brightness.setThumbTintList(csl);
                        is_systemui_accent_color_changed = true;
                        qs_accents_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // QS Header Colors

        qs_header = (ImageView) systemui_card.findViewById(R.id.qs_header_colorpicker);
        qs_header.setColorFilter(current_selected_header_background_color,
                PorterDuff.Mode.SRC_ATOP);
        final TextView qs_header_color = (TextView) systemui_card.findViewById(
                R.id.qs_header_colorpicker_text);
        qs_header.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_header_background_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_header_background_color = color;
                        qs_header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_systemui_header_color_changed = true;
                        qs_header_color.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // QS Notification Background Colors

        qs_notification = (ImageView) systemui_card.findViewById(
                R.id.system_notification_background_colorpicker);
        qs_notification.setColorFilter(current_selected_notification_background_color,
                PorterDuff.Mode.SRC_ATOP);
        final TextView qs_notification_color = (TextView) systemui_card.findViewById(
                R.id.system_notification_background_colorpicker_text);
        qs_notification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_notification_background_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_notification_background_color = color;
                        qs_notification.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_systemui_notification_background_color_changed = true;
                        qs_notification_color.setTextColor(
                                mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // QS Panel Background Colors

        qs_panel_bg = (ImageView) systemui_card.findViewById(R.id.qs_background_colorpicker);
        qs_panel_bg.setColorFilter(current_selected_qs_panel_background_color,
                PorterDuff.Mode.SRC_ATOP);
        final RelativeLayout qs_panel_bg_preview = (RelativeLayout) systemui_card.findViewById(R.id.systemui_preview);
        qs_panel_bg_preview.setBackgroundColor(current_selected_qs_panel_background_color);
        final TextView qs_panel_bg_color = (TextView) systemui_card.findViewById(
                R.id.qs_background_colorpicker_text);
        qs_panel_bg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_qs_panel_background_color);
                cpd.setAlphaSliderVisible(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_qs_panel_background_color = color;
                        qs_panel_bg.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_systemui_qs_panel_background_color_changed = true;
                        qs_panel_bg_color.setTextColor(mContext.getColor(android.R.color.white));
                        qs_panel_bg_preview.setBackgroundColor(current_selected_qs_panel_background_color);
                    }
                });
                cpd.show();
            }
        });

        // QS Icon Colors

        qs_tile = (ImageView) systemui_card.findViewById(R.id.qs_tile_icon_colorpicker);
        final TextView qs_tile_text = (TextView) systemui_card.findViewById(
                R.id.qs_tile_icon_colorpicker_text);
        qs_tile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_qs_tile_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_qs_tile_color = color;
                        qs_tile.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        ColorStateList csl = new ColorStateList(
                                new int[][]{
                                        new int[]{android.R.attr.state_pressed},
                                        new int[]{android.R.attr.state_focused},
                                        new int[]{}
                                },
                                new int[]{
                                        color, color, color
                                }
                        );
                        bluetoothLabel.setCompoundDrawableTintList(csl);
                        wifiLabel.setCompoundDrawableTintList(csl);
                        is_systemui_qs_tile_color_changed = true;
                        qs_tile_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        // QS Title Colors

        qs_text = (ImageView) systemui_card.findViewById(R.id.qs_tile_text_colorpicker);
        final TextView qs_text_text = (TextView) systemui_card.findViewById(
                R.id.qs_tile_text_colorpicker_text);
        qs_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_qs_text_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_qs_text_color = color;
                        qs_text.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        wifiLabel.setTextColor(color);
                        bluetoothLabel.setTextColor(color);
                        is_systemui_qs_text_color_changed = true;
                        qs_text_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });


        // QS Recents Clear All Icon Color

        qs_recents = (ImageView) systemui_card.findViewById(R.id.qs_recents_clear_all_icon_colorpicker);
        final TextView qs_recents_text = (TextView) systemui_card.findViewById(
                R.id.qs_recents_clear_all_icon_colorpicker_text);
        qs_recents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ColorPickerDialog cpd = new ColorPickerDialog(
                        mContext, current_selected_recents_clear_all_icon_color);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_selected_recents_clear_all_icon_color = color;
                        qs_recents.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        is_systemui_recents_clear_all_icon_color_changed = true;
                        qs_recents_text.setTextColor(mContext.getColor(android.R.color.white));
                    }
                });
                cpd.show();
            }
        });

        return systemui_card;
    }

    public void activateAllOptions(Boolean bool) {
        // All Framework Modifications
        accent_universal.setClickable(bool);
        accent_secondary.setClickable(bool);
        accent_light.setClickable(bool);
        appbg_dark.setClickable(bool);
        appbg_light.setClickable(bool);
        dialog_dark.setClickable(bool);
        dialog_light.setClickable(bool);
        main_color.setClickable(bool);
        main_color_dark.setClickable(bool);
        notifications_primary.setClickable(bool);
        notifications_secondary.setClickable(bool);
        ripples.setClickable(bool);

        // All Settings Modifications
        colorful_icon_switch.setClickable(bool);
        colorful_icon_switch.setVisibility(View.GONE);
        categories_title_caps.setClickable(bool);
        categories_title_bold.setClickable(bool);
        categories_title_italics.setClickable(bool);
        dashboard_divider.setClickable(bool);
        dutweaks_icons.setClickable(bool);
        settings_dashboard_background_color.setClickable(bool);
        settings_dashboard_category_background_color.setClickable(bool);
        settings_icon_colors.setClickable(bool);
        settings_title_colors.setClickable(bool);
        settings_switchbar_background_color.setClickable(bool);

        // All SystemUI Modifications
        qs_accents.setClickable(bool);
        qs_header.setClickable(bool);
        qs_notification.setClickable(bool);
        qs_panel_bg.setClickable(bool);
        qs_tile.setClickable(bool);
        qs_text.setClickable(bool);
        qs_recents.setClickable(bool);

        // All Finalized Card Modifications
        if (!bool) {
            aet1.setHint(mContext.getString(R.string.creative_mode_card_header_selection_hint));
            aet2.setHint(mContext.getString(R.string.creative_mode_card_header_selection_hint));
        } else {
            aet1.setHint(mContext.getString(R.string.creation_name_empty));
            aet2.setHint(mContext.getString(R.string.creator_name_empty));
        }
        aet1.setEnabled(bool);
        aet1.setFocusable(bool);
        aet1.setFocusableInTouchMode(bool);

        aet2.setEnabled(bool);
        aet2.setFocusable(bool);
        aet2.setFocusableInTouchMode(bool);
        themable_gapps.setClickable(bool);
    }

    private View getFinalizedView(ViewGroup container) {
        final_card = (CardView) mInflater.inflate(R.layout.final_card, container, false);
        final_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[3]));

        int counter = 0;

        final Spinner spinner1 = (Spinner) final_card.findViewById(R.id.spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        List<String> list = new ArrayList<String>();

        aet1 = (AnimatedEditText) final_card.findViewById(R.id.edittext1);
        aet2 = (AnimatedEditText) final_card.findViewById(R.id.edittext2);

        themable_gapps = (Switch) final_card.findViewById(R.id.themable_gapps);
        themable_gapps.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            use_themable_gapps = true;
                            Log.d("Use Themable Gapps", "true");
                            use_themable_gapps_changed = true;
                            themable_gapps.setTextColor(mContext.getColor(android.R.color.white));
                        } else {
                            use_themable_gapps = false;
                            Log.d("Use Themable Gapps", "false");
                            use_themable_gapps_changed = false;
                            themable_gapps.setTextColor(mContext.getColor(R.color.creative_mode_text_disabled));
                        }

                    }
                });


        list.add(mContext.getResources().getString(R.string.contextualheaderswapper_select_theme));
        list.add(mContext.getString(R.string.creative_mode_card_header_selection));
        list.add("dark material // akZent");
        list.add("blacked out // blakZent");


        // Now lets add all the located themes found that aren't cdt themes
        File f = new File("/data/resource-cache/");
        File[] files = f.listFiles();
        if (files != null) {
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    if (!inFile.getAbsolutePath().substring(21).equals(
                            "com.chummy.jezebel.blackedout.donate")) {
                        if (!inFile.getAbsolutePath().substring(21).equals(
                                "com.chummy.jezebel.materialdark.donate")) {
                            if (!inFile.getAbsolutePath().substring(21).equals("projekt.klar")) {
                                if (checkIfPackageInstalled(inFile.getAbsolutePath().substring(21), mContext)) {
                                    list.add(inFile.getAbsolutePath().substring(21));
                                    counter += 1;
                                }
                            }
                        } else {
                            counter += 1;
                        }
                    } else {
                        counter += 1;
                    }
                }
            }
        }
        if (counter == 0) {
            Toast toast = Toast.makeText(mContext.getApplicationContext(),
                    mContext.getResources().getString(
                            R.string.contextualheaderswapper_toast_cache_empty_reboot_first),
                    Toast.LENGTH_LONG);
            toast.show();
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set On Item Selected Listener
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                if (pos == 0) {
                    Log.d("getFinalizedViewSpinner", "header pack creation activated, all options have been disabled!");
                    header_creative_mode_activated = false;
                    activateAllOptions(true);
                }
                if (pos == 1) {
                    Log.d("getFinalizedViewSpinner", "header pack creation activated, all options have been disabled!");
                    header_creative_mode_activated = true;

                    activateAllOptions(false);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[1]));
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[1]));
                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));
                                                            final Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[1]));
                                                                    final Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            systemui_card.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));
                                                                        }
                                                                    }, 100);
                                                                }
                                                            }, 100);
                                                        }
                                                    }, 100);
                                                }
                                            }, 100);
                                        }
                                    }, 100);
                                }
                            }, 100);
                        }
                    }, 100);
                }
                if (pos == 2) {
                    if (!checkCurrentThemeSelection("com.chummy.jezebel.materialdark.donate")) {
                        Toast toast = Toast.makeText(mContext.getApplicationContext(),
                                mContext.getResources().getString(
                                        R.string.akzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Log.d("getFinalizedViewSpinner", "akZent has been selected, all options have been swapped!");
                        colorful_icon_switch.setVisibility(View.VISIBLE);
                        main_color_dark_view.setVisibility(View.GONE);
                        is_framework_main_theme_color_dark_changed = false;
                        if (current_selected_system_main_color == Color.argb(255, 0, 0, 0)) {
                            current_selected_system_main_color = Color.argb(255, 33, 32, 33);
                            main_color.setColorFilter(current_selected_system_main_color);
                            framework_toolbar.setBackgroundColor(current_selected_system_main_color);
                            settings_toolbar.setBackgroundColor(current_selected_system_main_color);
                        }
                        if (current_selected_system_main_dark_color == Color.argb(255, 0, 0, 0)) {
                            current_selected_system_main_dark_color = Color.argb(255, 33, 32, 33);
                        }
                        if (current_selected_header_background_color == Color.argb(255, 0, 0, 0)) {
                            current_selected_header_background_color = Color.argb(255, 31, 31, 31);
                            qs_header.setColorFilter(current_selected_header_background_color);
                        }
                        if (current_selected_notification_background_color == Color.argb(255, 0, 0, 0)) {
                            current_selected_notification_background_color = Color.argb(229, 33, 32, 33);
                            qs_notification.setColorFilter(current_selected_notification_background_color);
                        }
                        if (current_selected_qs_panel_background_color == Color.argb(255, 0, 0, 0)) {
                            current_selected_qs_panel_background_color = Color.argb(204, 0, 0, 0);
                            qs_panel_bg.setColorFilter(current_selected_qs_panel_background_color);
                        }
                        current_cdt_theme = "com.chummy.jezebel.materialdark.donate";
                    }
                    header_creative_mode_activated = false;
                    activateAllOptions(true);
                }
                if (pos >= 3) {
                    if (!checkCurrentThemeSelection("com.chummy.jezebel.blackedout.donate")) {
                        Toast toast = Toast.makeText(mContext.getApplicationContext(),
                                mContext.getResources().getString(
                                        R.string.blakzent_toast_install_before_using),
                                Toast.LENGTH_LONG);
                        toast.show();
                        spinner1.setSelection(0);
                    } else {
                        Log.d("getFinalizedViewSpinner", "blakZent has been selected, all options have been swapped!");
                        colorful_icon_switch.setVisibility(View.VISIBLE);
                        main_color_dark_view.setVisibility(View.GONE);
                        is_framework_main_theme_color_dark_changed = false;
                        if (current_selected_system_main_color == Color.argb(255, 33, 32, 33)) {
                            current_selected_system_main_color = Color.argb(255, 0, 0, 0);
                            main_color.setColorFilter(current_selected_system_main_color);
                            framework_toolbar.setBackgroundColor(current_selected_system_main_color);
                            settings_toolbar.setBackgroundColor(current_selected_system_main_color);
                        }
                        if (current_selected_system_main_dark_color == Color.argb(255, 33, 32, 33)) {
                            current_selected_system_main_dark_color = Color.argb(255, 0, 0, 0);
                        }
                        if (current_selected_header_background_color == Color.argb(255, 31, 31, 31)) {
                            current_selected_header_background_color = Color.argb(255, 0, 0, 0);
                            qs_header.setColorFilter(current_selected_header_background_color);
                        }
                        if (current_selected_notification_background_color == Color.argb(229, 33, 32, 33)) {
                            current_selected_notification_background_color = Color.argb(255, 0, 0, 0);
                            qs_notification.setColorFilter(current_selected_notification_background_color);
                        }
                        if (current_selected_qs_panel_background_color == Color.argb(204, 0, 0, 0)) {
                            current_selected_qs_panel_background_color = Color.argb(255, 0, 0, 0);
                            qs_panel_bg.setColorFilter(current_selected_qs_panel_background_color);
                        }
                        current_cdt_theme = "com.chummy.jezebel.blackedout.donate";
                    }
                    header_creative_mode_activated = false;
                    activateAllOptions(true);

                }

                if (pos >= 3) {
                    header_creative_mode_activated = false;
                    activateAllOptions(true);
                    if (spinner1.getSelectedItem().toString().substring(0, 10).equals("com.chummy")
                            || spinner1.getSelectedItem().toString().substring(0, 13).equals("projekt.klar")) {
                        Log.d("getFinalizedViewSpinner", "a cdt theme derivative has been selected, several options have been changed!");
                        main_color_dark_view.setVisibility(View.GONE);
                        is_framework_main_theme_color_dark_changed = false;
                    } else {
                        Log.d("getFinalizedViewSpinner", "header pack creation deactivated, all options have been re-enabled!");
                        if (!main_color_dark_view.isShown()) {
                            main_color_dark_view.setVisibility(View.VISIBLE);
                        }
                        if (colorful_icon_switch.isShown()) {
                            colorful_icon_switch.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //
            }
        });
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);


        // Begin Creative Mode Functions

        mProgressDialog = new ProgressDialog(mContext, R.style.CreativeMode_ActivityTheme);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        Button creative_mode_start = (Button) final_card.findViewById(R.id.begin_action);
        creative_mode_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (header_creative_mode_activated) {
                    if (is_header_pack_chosen) {
                        // We have to unzip the destination APK first

                        did_it_compile = true;  // Reset the checker

                        // Check that there is SOMETHING changed, let's decide on the theme at least

                        Phase1_UnzipAssets unzipTask = new Phase1_UnzipAssets();
                        unzipTask.execute(header_pack_location);

                    }

                } else {
                    // We have to unzip the destination APK first

                    did_it_compile = true;  // Reset the checker

                    // Check that there is SOMETHING changed, let's decide on the theme at least

                    if (spinner1.getSelectedItemPosition() != 0 && !aet1.getText().toString()
                            .equals("")) {
                        spinnerItem = spinner1.getSelectedItem().toString();
                        if (spinnerItem.equals("dark material // akZent") ||
                                spinnerItem.equals("blacked out // blakZent")) {
                            spinnerItem = current_cdt_theme;
                        } else {
                            spinnerItem = spinner1.getSelectedItem().toString();
                        }
                        themeName = aet1.getText().toString();
                        themeAuthor = aet2.getText().toString();
                        if (themeAuthor.equals("")) {
                            themeAuthor = prefs.getString("dashboard_username",
                                    mContext.getResources().getString(R.string.default_username));
                        }
                        Phase1_UnzipAssets unzipTask = new Phase1_UnzipAssets();
                        unzipTask.execute(spinnerItem);
                    } else {
                        Toast toast = Toast.makeText(mContext.getApplicationContext(),
                                mContext.getResources().getString(
                                        R.string.no_theme_selected),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });
        return final_card;
    }

    public Boolean checkIfPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private class Phase1_UnzipAssets extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Phase 1", "This phase has started it's asynchronous task.");
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager)
                    mContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.custom_dialog_loader);
            loader_string = (TextView) mProgressDialog.findViewById(R.id.loadingTextCreativeMode);
            loader_string.setText(mContext.getResources().getString(
                    R.string.unzipping_assets_small));
            loader = (CircularFillableLoaders) mProgressDialog.findViewById(
                    R.id.circularFillableLoader);
            loader.setProgress(90);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loader.setProgress(80);
            loader_string.setText(mContext.getResources().getString(R.string.phase2_dialog_title));
            startPhase2();
        }

        public void startPhase2() {

            if (!header_creative_mode_activated) {

                // Begin going through all AsyncTasks for Framework (v10)

                if (is_framework_accent_changed) {
                    Phase2_InjectAndMove accent = new Phase2_InjectAndMove();
                    String accent_color = "#" + Integer.toHexString(current_selected_system_accent_color);
                    accent.execute("creative_mode_accent", accent_color, "creative_mode_accent",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_accent_secondary_changed) {
                    Phase2_InjectAndMove accent_secondary = new Phase2_InjectAndMove();
                    String accent_secondary_color = "#" + Integer.toHexString(
                            current_selected_system_accent_dual_color);
                    accent_secondary.execute("creative_mode_accent_secondary", accent_secondary_color,
                            "creative_mode_accent_secondary", mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_accent_light_changed) {
                    Phase2_InjectAndMove accent_light = new Phase2_InjectAndMove();
                    String accent_light_color = "#" + Integer.toHexString(
                            current_selected_system_accent_light_color);
                    accent_light.execute("creative_mode_accent_light", accent_light_color,
                            "creative_mode_accent_light",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_app_background_changed) {
                    Phase2_InjectAndMove app_bg = new Phase2_InjectAndMove();
                    String app_bg_color = "#" + Integer.toHexString(current_selected_system_appbg_color);
                    app_bg.execute("creative_mode_app_background", app_bg_color, "creative_mode_app_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_app_background_light_changed) {
                    Phase2_InjectAndMove app_bg_light = new Phase2_InjectAndMove();
                    String app_bg_light_color = "#" + Integer.toHexString(
                            current_selected_system_appbg_light_color);
                    app_bg_light.execute("creative_mode_light_background", app_bg_light_color,
                            "creative_mode_light_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_dialog_background_dark_changed) {
                    Phase2_InjectAndMove dialog_dark = new Phase2_InjectAndMove();
                    String dialog_dark_color = "#" + Integer.toHexString(
                            current_selected_system_dialog_color);
                    dialog_dark.execute("creative_mode_dialog_color_dark", dialog_dark_color, "creative_mode_dialog_color_dark",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_dialog_background_light_changed) {
                    Phase2_InjectAndMove dialog_light = new Phase2_InjectAndMove();
                    String dialog_light_color = "#" + Integer.toHexString(
                            current_selected_system_dialog_light_color);
                    dialog_light.execute("creative_mode_dialog_color_light", dialog_light_color,
                            "creative_mode_dialog_color_light", mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_main_theme_color_changed) {
                    Phase2_InjectAndMove theme_color = new Phase2_InjectAndMove();
                    String theme_color_ = "#" + Integer.toHexString(current_selected_system_main_color);
                    theme_color.execute("creative_mode_main_color", theme_color_, "creative_mode_main_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_main_theme_color_dark_changed) {
                    Phase2_InjectAndMove theme_color_dark = new Phase2_InjectAndMove();
                    String theme_color_dark_ = "#" + Integer.toHexString(current_selected_system_main_dark_color);
                    theme_color_dark.execute("creative_mode_main_color_dark", theme_color_dark_, "creative_mode_main_color_dark",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_notifications_primary_changed) {
                    Phase2_InjectAndMove notification_primary = new Phase2_InjectAndMove();
                    String notification_primary_color = "#" + Integer.toHexString(
                            current_selected_system_notifications_primary_color);
                    notification_primary.execute("creative_mode_notification_primary", notification_primary_color,
                            "creative_mode_notification_primary", mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_notifications_secondary_changed) {
                    Phase2_InjectAndMove notification_secondary = new Phase2_InjectAndMove();
                    String notification_secondary_color = "#" + Integer.toHexString(
                            current_selected_system_notifications_secondary_color);
                    notification_secondary.execute("creative_mode_notification_secondary", notification_secondary_color,
                            "creative_mode_notification_secondary", mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                if (is_framework_system_ripple_changed) {
                    Phase2_InjectAndMove ripple = new Phase2_InjectAndMove();
                    String ripple_color = "#" + Integer.toHexString(
                            current_selected_system_ripple_color);
                    ripple.execute("creative_mode_ripple", ripple_color, "creative_mode_ripple",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v10/");
                }

                // Begin going through all AsyncTasks for Settings (v11)

                if (is_settings_dashboard_background_color_changed) {
                    Phase2_InjectAndMove settings_dashboard = new Phase2_InjectAndMove();
                    String settings_dashboard_color = "#" + Integer.toHexString(
                            current_selected_dashboard_background_color);
                    settings_dashboard.execute("dashboard_background_color", settings_dashboard_color,
                            "creative_mode_settings_dashboard_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.settings/" +
                                    "res/values-v11/");
                }

                if (is_settings_dashboard_category_background_color_changed) {
                    Phase2_InjectAndMove settings_dashboard_category = new Phase2_InjectAndMove();
                    String settings_dashboard_category_color = "#" + Integer.toHexString(
                            current_selected_dashboard_category_background_color);
                    settings_dashboard_category.execute("dashboard_category_background_color",
                            settings_dashboard_category_color,
                            "creative_mode_settings_dashboard_category_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.settings/" +
                                    "res/values-v11/");
                }

                if (is_settings_icon_color_changed) {
                    Phase2_InjectAndMove settings_icon = new Phase2_InjectAndMove();
                    String settings_icon_color = "#" + Integer.toHexString(
                            current_selected_settings_icon_color);
                    settings_icon.execute("creative_mode_settings_icon_tint", settings_icon_color,
                            "creative_mode_settings_icon_tint",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v11/");
                }

                if (is_settings_title_color_changed) {
                    Phase2_InjectAndMove settings_title = new Phase2_InjectAndMove();
                    String settings_title_color = "#" + Integer.toHexString(
                            current_selected_settings_title_color);
                    settings_title.execute("theme_accent", settings_title_color,
                            "creative_mode_settings_title_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.settings/" +
                                    "res/values-v11/");
                }

                if (is_settings_switchbar_background_color_changed) {
                    Phase2_InjectAndMove switchbar_background = new Phase2_InjectAndMove();
                    String switchbar_background_color = "#" + Integer.toHexString(
                            current_selected_settings_switchbar_color);
                    switchbar_background.execute("switchbar_background_color",
                            switchbar_background_color,
                            "creative_mode_settings_switchbar_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.settings/" +
                                    "res/values-v11/");
                }

                // Begin going through all AsyncTasks for SystemUI (v12)

                if (is_systemui_accent_color_changed) {
                    Phase2_InjectAndMove sysui_accent = new Phase2_InjectAndMove();
                    String sysui_accent_color = "#" + Integer.toHexString(
                            current_selected_qs_accent_color);
                    sysui_accent.execute("system_accent_color", sysui_accent_color,
                            "creative_mode_systemui_accent_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.systemui/" +
                                    "res/values-v12/");
                }

                if (is_systemui_header_color_changed) {
                    Phase2_InjectAndMove sysui_header = new Phase2_InjectAndMove();
                    String sysui_header_color = "#" + Integer.toHexString(
                            current_selected_header_background_color);
                    sysui_header.execute("creative_mode_qs_header_color", sysui_header_color,
                            "creative_mode_qs_header_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_notification_background_color_changed) {
                    Phase2_InjectAndMove sysui_notif_bg = new Phase2_InjectAndMove();
                    String sysui_header_color = "#" + Integer.toHexString(
                            current_selected_notification_background_color);
                    sysui_notif_bg.execute("creative_mode_notification_background", sysui_header_color,
                            "creative_mode_notification_background",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_qs_panel_background_color_changed) {
                    Phase2_InjectAndMove sysui_panelbg = new Phase2_InjectAndMove();
                    String sysui_panelbg_color = "#" + Integer.toHexString(
                            current_selected_qs_panel_background_color);
                    sysui_panelbg.execute("creative_mode_qs_background_color", sysui_panelbg_color,
                            "creative_mode_qs_background_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_qs_tile_color_changed) {
                    Phase2_InjectAndMove sysui_qs_tile = new Phase2_InjectAndMove();
                    String sysui_qs_tile_color = "#" + Integer.toHexString(
                            current_selected_qs_tile_color);
                    sysui_qs_tile.execute("creative_mode_qs_icon_color", sysui_qs_tile_color,
                            "creative_mode_qs_icon_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_qs_tile_color_changed) {
                    Phase2_InjectAndMove sysui_qs_tile_disabled = new Phase2_InjectAndMove();
                    String sysui_qs_tile_disabled_color = "#4d" +
                            Integer.toHexString(current_selected_qs_tile_color).substring(2);
                    sysui_qs_tile_disabled.execute("creative_mode_qs_icon_color_disabled",
                            sysui_qs_tile_disabled_color,
                            "creative_mode_qs_icon_color_disabled",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_qs_text_color_changed) {
                    Phase2_InjectAndMove sysui_qs_tile_text = new Phase2_InjectAndMove();
                    String sysui_qs_tile_text_color = "#" + Integer.toHexString(
                            current_selected_qs_text_color);
                    sysui_qs_tile_text.execute("creative_mode_qs_tile_text", sysui_qs_tile_text_color,
                            "creative_mode_qs_tile_text",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/common/res/values-v12/");
                }

                if (is_systemui_recents_clear_all_icon_color_changed) {
                    Phase2_InjectAndMove sysui_recents = new Phase2_InjectAndMove();
                    String sysui_recents_color = "#" + Integer.toHexString(
                            current_selected_recents_clear_all_icon_color);
                    sysui_recents.execute("floating_action_button_icon_color", sysui_recents_color,
                            "creative_mode_recents_clear_all_icon_color",
                            mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.systemui/" +
                                    "res/values-v12/");
                }
            }

            Phase3_MovePremadeFiles phase3 = new Phase3_MovePremadeFiles();
            phase3.execute();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String package_identifier = sUrl[0];
            try {
                unzip(package_identifier);
            } catch (IOException e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.unzip_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
            return null;
        }

        public void unzip(String package_identifier) throws IOException {

            if (!header_creative_mode_activated) {
                // Let's check where it is first

                Boolean is_valid = checkCurrentThemeSelection(package_identifier);

                // After checking package identifier validity, check for exact folder number
                if (is_valid) {
                    int folder_abbreviation = checkCurrentThemeSelectionLocation(package_identifier);
                    if (folder_abbreviation != 0) {
                        String source = "/data/app/" + package_identifier + "-" +
                                folder_abbreviation + "/base.apk";
                        String destination = mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/";

                        File checkFile = new File(source);
                        long fileSize = checkFile.length();
                        if (fileSize > 50000000) { // Picking 50mb to be the threshold of large themes
                            loader_string.setText(mContext.getResources().getString(R.string.unzipping_assets_big));
                        }
                        File myDir = new File(mContext.getCacheDir(), "creative_mode");
                        if (!myDir.exists()) {
                            myDir.mkdir();
                        }

                        ZipInputStream inputStream = new ZipInputStream(
                                new BufferedInputStream(new FileInputStream(source)));
                        try {
                            ZipEntry zipEntry;
                            int count;
                            byte[] buffer = new byte[8192];
                            while ((zipEntry = inputStream.getNextEntry()) != null) {
                                File file = new File(destination, zipEntry.getName());
                                File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                                if (!dir.isDirectory() && !dir.mkdirs())
                                    throw new FileNotFoundException("Failed to ensure directory: " +
                                            dir.getAbsolutePath());
                                if (zipEntry.isDirectory())
                                    continue;
                                FileOutputStream outputStream = new FileOutputStream(file);
                                try {
                                    while ((count = inputStream.read(buffer)) != -1)
                                        outputStream.write(buffer, 0, count);
                                } finally {
                                    outputStream.close();
                                }
                            }
                        } finally {
                            inputStream.close();
                        }
                    } else {
                        Log.d("Unzip",
                                "There is no valid package name under this abbreviated folder count.");
                    }
                } else {
                    Log.d("Unzip", "Package name chosen is invalid.");
                }
            } else {
                String source = header_pack_location;  // This is already the absolute path
                String destination = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/assets/overlays/com.android.systemui/res/drawable-xxhdpi";

                File checkFile = new File(source);
                long fileSize = checkFile.length();
                if (fileSize > 50000000) { // Picking 50mb to be the threshold of large themes
                    loader_string.setText(mContext.getResources().getString(R.string.unzipping_assets_big));
                }
                File myDir = new File(mContext.getCacheDir(), "creative_mode");
                if (!myDir.exists()) {
                    myDir.mkdir();
                }

                ZipInputStream inputStream = new ZipInputStream(
                        new BufferedInputStream(new FileInputStream(source)));
                try {
                    ZipEntry zipEntry;
                    int count;
                    byte[] buffer = new byte[8192];
                    while ((zipEntry = inputStream.getNextEntry()) != null) {
                        File file = new File(destination, zipEntry.getName());
                        File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                        if (!dir.isDirectory() && !dir.mkdirs())
                            throw new FileNotFoundException("Failed to ensure directory: " +
                                    dir.getAbsolutePath());
                        if (zipEntry.isDirectory())
                            continue;
                        FileOutputStream outputStream = new FileOutputStream(file);
                        try {
                            while ((count = inputStream.read(buffer)) != -1)
                                outputStream.write(buffer, 0, count);
                        } finally {
                            outputStream.close();
                        }
                    }
                } finally {
                    File headers_xml = new File(mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.systemui/res/" +
                            "drawable-xxhdpi/headers.xml");
                    boolean deleted = headers_xml.delete();
                    inputStream.close();
                }
            }
        }
    }

    private class Phase2_InjectAndMove extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Phase 2", "This phase has started it's asynchronous task.");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String color_name = sUrl[0];
            String colorHex = sUrl[1];
            String filename = sUrl[2];
            String theme_destination = sUrl[3];
            createXMLfile(color_name, colorHex, filename, theme_destination);
            return null;
        }

        private void createXMLfile(String color_name, String colorHex, String filename,
                                   String theme_destination) {

            File root = new File(
                    mContext.getCacheDir().getAbsolutePath() + "/" + filename + ".xml");
            try {
                root.createNewFile();
                FileWriter fw = new FileWriter(root);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
                String xmlRes1 = ("<resources>" + "\n");
                String xmlRes2 = ("    <color name=\"" + color_name + "\">" + colorHex + "</color>"
                        + "\n");
                String xmlRes3 = ("</resources>");
                pw.write(xmlTags);
                pw.write(xmlRes1);
                pw.write(xmlRes2);
                pw.write(xmlRes3);
                pw.close();
                bw.close();
                fw.close();
                moveXMLfile(
                        mContext.getCacheDir().getAbsolutePath() + "/",
                        filename + ".xml", theme_destination);
            } catch (IOException e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.createXMLFile_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private void moveXMLfile(String current_source, String inputFile,
                                 String theme_destination) {
            InputStream in;
            OutputStream out;
            try {
                //create output directory if it doesn't exist
                File dir = new File(theme_destination);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                in = new FileInputStream(current_source + inputFile);
                out = new FileOutputStream(theme_destination + inputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();

                // write the output file
                out.flush();
                out.close();

                // delete the original file
                new File(current_source + inputFile).delete();

            } catch (FileNotFoundException f) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.moveXMLfile_FNF_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            } catch (Exception e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.moveXMLfile_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class Phase3_MovePremadeFiles extends AsyncTask<String, Integer, String> {

        final String TARGET_BASE_PATH = mContext.getCacheDir().getAbsolutePath() + "/";

        @Override
        protected void onPreExecute() {
            Log.d("Phase 3", "This phase has started it's asynchronous task.");
            loader.setProgress(70);
            loader_string.setText(mContext.getResources().getString(R.string.phase3_dialog_title));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            MoveWhateverIsActivated();
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            copyFileOrDir("");
            return null;
        }

        private void copyFileOrDir(String path) {
            AssetManager assetManager = mContext.getAssets();
            String assets[];
            try {
                Log.d("movePremadeFiles", path);
                assets = assetManager.list(path);
                if (assets.length == 0) {
                    copyFile(path);
                } else {
                    String fullPath = TARGET_BASE_PATH + path;
                    File dir = new File(fullPath);
                    if (!dir.exists() && !path.startsWith("images") &&
                            !path.startsWith("sounds") && !path.startsWith("webkit")) {
                        if (!dir.mkdirs()) {
                            Log.e("movePremadeFiles", "Could not create directory " + fullPath);
                        }
                    }
                    for (int i = 0; i < assets.length; ++i) {
                        String p;
                        if (path.equals("")) {
                            p = "";
                        } else {
                            p = path + "/";
                        }
                        if (!path.startsWith("images") &&
                                !path.startsWith("sounds") && !path.startsWith("webkit")) {
                            copyFileOrDir(p + assets[i]);
                        }
                    }
                }
            } catch (IOException e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.copyFileOrDir_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private void copyFile(String filename) {
            AssetManager assetManager = mContext.getAssets();

            InputStream in;
            OutputStream out;
            String newFileName = null;
            try {
                in = assetManager.open(filename);
                if (filename.endsWith(".jpg")) // .jpg used to avoid compression on APK file
                    newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length() - 4);
                else
                    newFileName = TARGET_BASE_PATH + filename;
                out = new FileOutputStream(newFileName);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.flush();
                out.close();
            } catch (Exception e) {
                did_it_compile = false;
                Log.e("movePremadeFiles", "Exception in copyFile() for " + newFileName);
                Log.e("movePremadeFiles", "Exception in copyFile() " + e.toString());
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.copyFile_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private void MoveWhateverIsActivated() {
            if (!header_creative_mode_activated) {
                if (!colorful_icon && is_settings_colorful_icon_color_changed) {
                    String source_colorful_du = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    String destination_colorful_du = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/drawable-v11/";
                    String source_colorful_pn = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    String destination_colorful_pn = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/drawable-v11/";

                    moveFile(source_colorful_du, "ic_dirtytweaks.xml", destination_colorful_du);
                    moveFile(source_colorful_pn, "ic_settings_purenexus.xml", destination_colorful_pn);
                }
                if (is_settings_dashboard_category_title_caps_changed ||
                        is_settings_dashboard_category_title_bold_changed ||
                        is_settings_dashboard_category_title_italics_changed) {
                    if (category_title_bold || category_title_italics || category_title_caps) {
                        createSettingsTitleXML("settings_title_style",
                                mContext.getCacheDir().getAbsolutePath() +
                                        "/creative_mode/assets/overlays/com.android.settings/" +
                                        "res/values-v11/");
                    }
                }
                if (dashboard_dividers && is_settings_dashboard_dividers_changed) {
                    String source = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    // Use v12 here just in case
                    String destination = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/values-v12/";
                    moveFile(source, "dashboard_dividers_activated.xml", destination);
                }
                if (!dashboard_dividers && is_settings_dashboard_dividers_changed) {
                    String source = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    // Use v12 here just in case
                    String destination = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/values-v12/";
                    moveFile(source, "dashboard_dividers_deactivated.xml", destination);
                }
                if (dirtytweaks_icon_presence && is_settings_dashboard_dirty_tweaks_icon_presence_changed) {
                    String source = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    String destination = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/values-v11/";
                    moveFile(source, "dirty_tweaks_icon_presence_activated.xml", destination);
                }
                if (!dirtytweaks_icon_presence && is_settings_dashboard_dirty_tweaks_icon_presence_changed) {
                    String source = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_files/";
                    String destination = mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/assets/overlays/com.android.settings/res/values-v11/";
                    moveFile(source, "dirty_tweaks_icon_presence_deactivated.xml", destination);
                }

                if (use_themable_gapps_changed) {
                    if (use_themable_gapps) {
                        Boolean themable_gmail = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.gmail").exists();

                        Boolean themable_google_app = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.google.app").exists();

                        Boolean themable_contacts = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.google.contacts").exists();

                        Boolean themable_dialer = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.google.dialer").exists();

                        Boolean themable_plusOne = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.google.plus").exists();

                        Boolean themable_hangouts = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.hangouts").exists();

                        Boolean themable_vending = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.vending").exists();

                        Boolean themable_youtube = new File(mContext.getCacheDir().getAbsolutePath() +
                                "/creative_mode/assets/overlays/themable.youtube").exists();

                        if (themable_gmail) {
                            Log.d("MoveWhateverIsActivated", "Themable Gmail Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.gm");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.gmail");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.gm");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_google_app) {
                            Log.d("MoveWhateverIsActivated", "Themable Google App Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.googlequicksearchbox");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.google.app");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.googlequicksearchbox");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_contacts) {
                            Log.d("MoveWhateverIsActivated", "Themable Google Contacts Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.contacts");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.google.contacts");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.contacts");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_dialer) {
                            Log.d("MoveWhateverIsActivated", "Themable Google Dialer Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.dialer");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.google.dialer");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.dialer");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_plusOne) {
                            Log.d("MoveWhateverIsActivated", "Themable Google+ Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.apps.plus");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.google.plus");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.apps.plus");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_hangouts) {
                            Log.d("MoveWhateverIsActivated", "Themable Hangouts Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.talk");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.hangouts");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.talk");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_vending) {
                            Log.d("MoveWhateverIsActivated", "Themable Play Store Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.android.vending");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.vending");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.android.vending");
                            boolean success = oldFolder.renameTo(newFolder);
                        }

                        if (themable_youtube) {
                            Log.d("MoveWhateverIsActivated", "Themable Youtube Overlay found! Hotswapping...");

                            eu.chainfire.libsuperuser.Shell.SU.run(
                                    "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                            "/creative_mode/assets/overlays/com.google.android.youtube");

                            File oldFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/themable.vending");
                            File newFolder = new File(mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/assets/overlays/com.google.android.youtube");
                            boolean success = oldFolder.renameTo(newFolder);
                        }
                    }
                }

                // Add default theme icon if no traditional theme icon found

                String source = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_files/";
                String destination = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xxhdpi/";
                moveFile(source, "dashboard_default.png", destination);
            } else {
                // Add default theme icon if no traditional theme icon found

                String source = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_files/";
                String destination = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xxhdpi/";
                moveFile(source, "dashboard_default.png", destination);

                // Now add Theme Engine Header Whitelist

                String source2 = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_files/";
                String destination2 = mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/values/";
                moveFile(source2, "headers_whitelist.xml", destination2);

            }

            Phase4_ManifestCreation createManifest = new Phase4_ManifestCreation();
            createManifest.execute();

        }

        private void createSettingsTitleXML(String filename, String theme_destination) {

            File root = new File(
                    mContext.getCacheDir().getAbsolutePath() + "/" + filename + ".xml");

            String parseMe = "";
            String allCaps = "";

            if (category_title_bold && is_settings_dashboard_category_title_bold_changed) {
                if (parseMe.length() == 0) {
                    parseMe = "bold";
                } else {
                    parseMe += "|bold";
                }
            }
            if (category_title_italics || is_settings_dashboard_category_title_italics_changed) {
                if (parseMe.length() == 0) {
                    parseMe = "italic";
                } else {
                    parseMe += "|italic";
                }
            }

            String boldItalics = ("        <item name=\"android:textStyle\">" +
                    parseMe + "</item>" + "\n");

            if (!category_title_bold && !category_title_italics) {
                boldItalics = "";
            }
            if (category_title_caps && is_settings_dashboard_category_title_caps_changed) {
                allCaps = ("        <item name=\"android:textAllCaps\">true</item>" + "\n");
            }
            if (!category_title_caps && is_settings_dashboard_category_title_caps_changed) {
                allCaps = ("        <item name=\"android:textAllCaps\">false</item>" + "\n");
            }

            try {
                root.createNewFile();
                FileWriter fw = new FileWriter(root);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
                String xmlRes1 = ("<resources>" + "\n");
                String xmlRes2 = ("    <style name=\"TextAppearance.CategoryTitle\" " +
                        "parent=\"@android:style/TextAppearance.Material.Body2\">" + "\n");
                String xmlRes3 = ("        <item name=\"android:textColor\">" +
                        "?android:attr/colorAccent</item>" + "\n");
                String xmlRes5 = ("    </style>" + "\n");
                String xmlRes6 = ("</resources>");
                pw.write(xmlTags);
                pw.write(xmlRes1);
                pw.write(xmlRes2);
                pw.write(allCaps);
                pw.write(xmlRes3);
                pw.write(boldItalics);
                pw.write(xmlRes5);
                pw.write(xmlRes6);
                pw.close();
                bw.close();
                fw.close();
                moveFile(
                        mContext.getCacheDir().getAbsolutePath() + "/",
                        filename + ".xml", theme_destination);
            } catch (IOException e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.createSettingsTitleXML_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        private void moveFile(String current_source, String inputFile,
                              String theme_destination) {
            InputStream in;
            OutputStream out;
            try {
                //create output directory if it doesn't exist
                File dir = new File(theme_destination);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                in = new FileInputStream(current_source + inputFile);
                out = new FileOutputStream(theme_destination + inputFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();

                // write the output file
                out.flush();
                out.close();

                // delete the original file
                new File(current_source + inputFile).delete();

            } catch (FileNotFoundException f) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.moveFile_FNF_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            } catch (Exception e) {
                did_it_compile = false;
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.moveFile_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class Phase4_ManifestCreation extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Phase 4", "This phase has started it's asynchronous task.");
            loader.setProgress(60);
            loader_string.setText(mContext.getResources().getString(R.string.phase4_dialog_title));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Phase5_Compile createAPK = new Phase5_Compile();
            createAPK.execute();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            if (!header_creative_mode_activated) {
                String packageName = spinnerItem;

                // Parse Theme Name of all spaces and symbols
                String parse1_themeName = themeName.replaceAll("\\s+", "");
                String parse2_themeName = parse1_themeName.replaceAll("[^a-zA-Z0-9]+", "");

                if (parse2_themeName.equals("")) {
                    int inputNumber = 1;
                    while (checkIfPackageInstalled(packageName + "." + "dashboard" + inputNumber, mContext)) {
                        inputNumber += 1;
                    }
                    packageName = packageName + "." + "dashboard" + inputNumber;
                } else {
                    packageName = packageName + "." + parse2_themeName;
                }

                // Theme Name is now parsed
                String theme_name = themeName;

                // Check themeName if it has a whitespace at the end of the name
                if (themeName.endsWith(" ")) {
                    theme_name = theme_name.substring(0, theme_name.length() - 1);
                }

                // No need to parse Theme Author, it should display all characters naturally
                String theme_author = themeAuthor;

                String filename = "AndroidManifest";

                createXMLfile(packageName, theme_name, theme_author, filename);
            } else {
                String packageName = "chummy.dashboard";

                String author = "";

                // Parse Header Author of all spaces and symbols
                String parse1_authorName = header_pack_author.replaceAll("\\s+", "");
                String parse2_authorName = parse1_authorName.replaceAll("[^a-zA-Z0-9]+", "");

                if (parse2_authorName.equals("")) {
                    author = "header_creator";
                } else {
                    author = parse2_authorName;
                }

                // Parse Header Name of all spaces and symbols
                String parse1_themeName = header_pack_name.replaceAll("\\s+", "");
                String parse2_themeName = parse1_themeName.replaceAll("[^a-zA-Z0-9]+", "");

                if (parse2_themeName.equals("")) {
                    int inputNumber = 1;
                    while (checkIfPackageInstalled(packageName + "." + author + "." + "headerpack" + inputNumber, mContext)) {
                        inputNumber += 1;
                    }
                    packageName = packageName + "." + author + "." + "headerpack" + inputNumber;
                } else {
                    packageName = packageName + "." + author + "." + parse2_themeName;
                }

                String filename = "AndroidManifest";

                createXMLfile(packageName, parse2_themeName, author, filename);
            }

            return null;
        }

        private void createXMLfile(String packageName, String theme_name, String theme_author,
                                   String filename) {

            if (!header_creative_mode_activated) {
                File root = new File(
                        mContext.getCacheDir().getAbsolutePath() + "/creative_mode/" +
                                filename + ".xml");

                String icon_location;

                File iconChecker1 = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xhdpi/ic_launcher.png");
                File iconChecker1_ = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xhdpi-v4/ic_launcher.png");
                File iconChecker2 = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/mipmap-xhdpi/ic_launcher.png");
                File iconChecker2_ = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/mipmap-xhdpi-v4/ic_launcher.png");
                File iconChecker3 = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xhdpi/cdt.png");
                File iconChecker3_ = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/res/drawable-xhdpi-v4/cdt.png");

                // Now check for the icon on non -v4 folders

                if (iconChecker1.exists()) {
                    icon_location = "@drawable/ic_launcher";
                } else {
                    if (iconChecker2.exists()) {
                        icon_location = "@mipmap/ic_launcher";
                    } else {
                        if (iconChecker3.exists()) {
                            icon_location = "@drawable/cdt";
                        } else {
                            icon_location = "@drawable/dashboard_default";

                        }
                    }
                }

                // Now check for the icon on -v4 folders

                if (iconChecker1_.exists()) {
                    icon_location = "@drawable/ic_launcher";
                } else {
                    if (iconChecker2_.exists()) {
                        icon_location = "@mipmap/ic_launcher";
                    } else {
                        if (iconChecker3_.exists()) {
                            icon_location = "@drawable/cdt";
                        } else {
                            icon_location = "@drawable/dashboard_default";

                        }
                    }
                }

                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date = new Date();

                    root.createNewFile();
                    FileWriter fw = new FileWriter(root);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);
                    String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" " +
                            "standalone=\"no\"?>" + "\n");
                    String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/" +
                            "apk/res/android\"" + "\n");
                    String xmlRes2 = ("    package=\"" + packageName + "\"" + "\n");
                    String xmlRes3 = ("    android:versionCode=\"" + BuildConfig.VERSION_CODE + "\"");
                    String xmlRes4 = ("    android:versionName=\"" + "dashboard. - " +
                            BuildConfig.VERSION_NAME + " (" + dateFormat.format(date) + ")" + "\">");
                    String xmlRes5 = ("    <uses-sdk android:minSdkVersion=\"23\"/>" + "\n");
                    String xmlRes6 = ("    <uses-feature" + "\n");
                    String xmlRes7 = ("        android:name=\"org.cyanogenmod.theme\"" + "\n");
                    String xmlRes8 = ("        android:required=\"true\" />" + "\n");
                    String xmlRes9 = ("    <meta-data" + "\n");
                    String xmlRes10 = ("        android:name=\"org.cyanogenmod.theme.name\"" + "\n");
                    String xmlRes11 = ("        android:value=\"" + theme_name + "\" />" + "\n");
                    String xmlRes12 = ("    <meta-data" + "\n");
                    String xmlRes13 = ("        android:name=\"org.cyanogenmod.theme.author\"" + "\n");
                    String xmlRes14 = ("        android:value=\"" + theme_author + "\" />" + "\n");
                    String xmlRes15 = ("    <application android:hasCode=\"false\"" + "\n");
                    String xmlRes16 = ("        android:icon=\"" + icon_location + "\"" + "\n");
                    String xmlRes17 = ("        android:label=\"" + theme_name + "\"/>" + "\n");
                    String xmlRes18 = ("</manifest>");
                    pw.write(xmlTags);
                    pw.write(xmlRes1);
                    pw.write(xmlRes2);
                    pw.write(xmlRes3);
                    pw.write(xmlRes4);
                    pw.write(xmlRes5);
                    pw.write(xmlRes6);
                    pw.write(xmlRes7);
                    pw.write(xmlRes8);
                    pw.write(xmlRes9);
                    pw.write(xmlRes10);
                    pw.write(xmlRes11);
                    pw.write(xmlRes12);
                    pw.write(xmlRes13);
                    pw.write(xmlRes14);
                    pw.write(xmlRes15);
                    pw.write(xmlRes16);
                    pw.write(xmlRes17);
                    pw.write(xmlRes18);
                    pw.close();
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    did_it_compile = false;
                    Toast toast = Toast.makeText(mContext.getApplicationContext(),
                            mContext.getResources().getString(
                                    R.string.createXMLFile_exception_toast),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                File root = new File(
                        mContext.getCacheDir().getAbsolutePath() + "/creative_mode/" +
                                filename + ".xml");

                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date = new Date();

                    root.createNewFile();
                    FileWriter fw = new FileWriter(root);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);
                    String xmlTags = ("<?xml version=\"1.0\" encoding=\"utf-8\" " +
                            "standalone=\"no\"?>" + "\n");
                    String xmlRes1 = ("<manifest xmlns:android=\"http://schemas.android.com/" +
                            "apk/res/android\"" + "\n");
                    String xmlRes2 = ("    package=\"" + packageName + "\"" + "\n");
                    String xmlRes3 = ("    android:versionCode=\"" + BuildConfig.VERSION_CODE + "\"");
                    String xmlRes4 = ("    android:versionName=\"" + "dashboard. - " +
                            BuildConfig.VERSION_NAME + " (" + dateFormat.format(date) + ")" + "\">");
                    String xmlRes5 = ("    <uses-sdk android:minSdkVersion=\"23\"/>" + "\n");
                    String xmlRes6 = ("    <uses-feature" + "\n");
                    String xmlRes7 = ("        android:name=\"org.cyanogenmod.theme.extensions\"" + "\n");
                    String xmlRes8 = ("        android:required=\"true\" />" + "\n");
                    String xmlRes9 = ("    <meta-data" + "\n");
                    String xmlRes10 = ("        android:name=\"org.cyanogenmod.theme.name\"" + "\n");
                    String xmlRes11 = ("        android:value=\"" + theme_name + "\" />" + "\n");
                    String xmlRes12 = ("    <meta-data" + "\n");
                    String xmlRes13 = ("        android:name=\"org.cyanogenmod.theme.author\"" + "\n");
                    String xmlRes14 = ("        android:value=\"" + theme_author + "\" />" + "\n");
                    String xmlRes15 = ("    <application android:hasCode=\"false\"" + "\n");
                    String xmlRes16 = ("        android:icon=\"" + "@drawable/dashboard_default" + "\"" + "\n");
                    String xmlRes17 = ("        android:label=\"" + theme_name + "\"/>" + "\n");
                    String xmlRes18 = ("</manifest>");
                    pw.write(xmlTags);
                    pw.write(xmlRes1);
                    pw.write(xmlRes2);
                    pw.write(xmlRes3);
                    pw.write(xmlRes4);
                    pw.write(xmlRes5);
                    pw.write(xmlRes6);
                    pw.write(xmlRes7);
                    pw.write(xmlRes8);
                    pw.write(xmlRes9);
                    pw.write(xmlRes10);
                    pw.write(xmlRes11);
                    pw.write(xmlRes12);
                    pw.write(xmlRes13);
                    pw.write(xmlRes14);
                    pw.write(xmlRes15);
                    pw.write(xmlRes16);
                    pw.write(xmlRes17);
                    pw.write(xmlRes18);
                    pw.close();
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    did_it_compile = false;
                    Toast toast = Toast.makeText(mContext.getApplicationContext(),
                            mContext.getResources().getString(
                                    R.string.createXMLFile_exception_toast),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    private class Phase5_Compile extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Phase 5", "This phase has started it's asynchronous task.");
            loader.setProgress(50);
            if (!header_creative_mode_activated) {
                loader_string.setText(mContext.getResources().getString(R.string.phase5_dialog_title));
            } else {
                loader_string.setText(mContext.getResources().getString(R.string.phase5_hp_dialog_title));
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Phase6_SignAndInstall phase6 = new Phase6_SignAndInstall();
            phase6.execute();
        }

        @Override
        protected String doInBackground(String... sUrl) {

            // Let's clean up the dashboard cache for creative mode extraction zone

            File[] fileList = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/creative_mode/").listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (!fileList[i].getName().equals("AndroidManifest.xml") &&
                        !fileList[i].getName().equals("assets") &&
                        !fileList[i].getName().equals("res")) {
                    File file = new File(mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/" + fileList[i].getName());
                    boolean deleted = file.delete();
                    if (fileList[i].getName().equals("META-INF")) {
                        eu.chainfire.libsuperuser.Shell.SU.run(
                                "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                        "/creative_mode/META-INF");
                    }
                    Log.d("FileDeletion", "Deleted file/folder: " + fileList[i].getName());
                }
            }

            File[] fileList2 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/creative_mode/res/").listFiles();
            for (int i = 0; i < fileList2.length; i++) {
                if (!fileList2[i].getName().equals("drawable-xhdpi") &&
                        !fileList2[i].getName().equals("drawable-xhdpi-v4") &&
                        !fileList2[i].getName().equals("drawable-xxhdpi") &&
                        !fileList2[i].getName().equals("drawable-xxhdpi-v4") &&
                        !fileList2[i].getName().equals("drawable-xxxhdpi") &&
                        !fileList2[i].getName().equals("drawable-xxxhdpi-v4") &&
                        !fileList2[i].getName().equals("mipmap-xhdpi") &&
                        !fileList2[i].getName().equals("mipmap-xhdpi-v4") &&
                        !fileList2[i].getName().equals("mipmap-xxhdpi") &&
                        !fileList2[i].getName().equals("mipmap-xxhdpi-v4") &&
                        !fileList2[i].getName().equals("mipmap-xxxhdpi") &&
                        !fileList2[i].getName().equals("mipmap-xxxhdpi-v4")) {
                    File file2 = new File(mContext.getCacheDir().getAbsolutePath() +
                            "/creative_mode/" + fileList2[i].getName());
                    boolean deleted2 = file2.delete();
                    eu.chainfire.libsuperuser.Shell.SU.run(
                            "rm -r " + mContext.getCacheDir().getAbsolutePath() +
                                    "/creative_mode/res/" + fileList2[i].getName());
                    Log.d("FileDeletion", "Deleted file/folder: " + fileList2[i].getName());
                }
            }

            // Now let's build an APK based on the cleaned out directory
            try {
                Process nativeApp = Runtime.getRuntime().exec(
                        "aapt p -M " +
                                mContext.getCacheDir().getAbsolutePath() + "/creative_mode" +
                                "/AndroidManifest.xml -S " +
                                mContext.getCacheDir().getAbsolutePath() + "/creative_mode" +
                                "/res/ -I " +
                                "system/framework/framework-res.apk -F " +
                                mContext.getCacheDir().getAbsolutePath() +
                                "/dashboard_creation.apk -f\n");

                // We need this Process to be waited for before moving on to the next function.
                Log.d("ProcessWaitFor", "Dummy APK creation is running now...");
                nativeApp.waitFor();
                Log.d("ProcessWaitFor", "Dummy APK creation has completed!");

                // APK should now be built, good for us, now let's break it apart
                try {
                    if (is_header_pack_chosen && !header_creative_mode_activated) {
                        unzipHeaderPacks();
                    }
                    unzipNewAPK();
                } catch (IOException e) {
                    did_it_compile = false;
                    Log.e("unzipNewAPK", "There has been an exception while trying to unzip the " +
                            "new dummy APK.");
                    Toast toast = Toast.makeText(mContext.getApplicationContext(),
                            mContext.getResources().getString(
                                    R.string.unzipNewAPK_exception_toast),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (IOException e) {
                did_it_compile = false;
                Log.e("ProcessBuilder", "There has been an exception while trying to create the " +
                        "new dummy APK.");
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.process_IO_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            } catch (InterruptedException f) {
                did_it_compile = false;
                Log.e("ProcessBuilder", "There has been an exception while trying to create the " +
                        "new dummy APK.");
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.process_Interrupted_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
            return null;
        }

        public void unzipNewAPK() throws IOException {
            String source = mContext.getCacheDir().getAbsolutePath() +
                    "/dashboard_creation.apk";
            String destination = mContext.getCacheDir().getAbsolutePath() +
                    "/creative_mode/";

            ZipInputStream inputStream = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(source)));
            try {
                ZipEntry zipEntry;
                int count;
                byte[] buffer = new byte[8192];
                while ((zipEntry = inputStream.getNextEntry()) != null) {
                    File file = new File(destination, zipEntry.getName());
                    File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (zipEntry.isDirectory())
                        continue;
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        while ((count = inputStream.read(buffer)) != -1)
                            outputStream.write(buffer, 0, count);
                    } finally {
                        outputStream.close();
                    }
                }
                inputStream.close();
                Log.d("unzipNewAPK", "Finished decompressing new APK file!");

                // The APK has now overwritten the resources located in creative_mode folder -
                // zip it!

                String sourced = mContext.getCacheDir().getAbsolutePath() +
                        "/dashboard_creation_unsigned.apk";
                String destinations = mContext.getCacheDir().getAbsolutePath() + "/creative_mode/";
                try {
                    UnsignedAPKCreator.main(sourced, destinations);
                    Log.d("UnsignedAPKCreator", "Finished compiling unsigned APK file!");
                } catch (Exception e) {
                    did_it_compile = false;
                    Log.e("UnsignedAPKCreator", "There has been an exception while trying to " +
                            "create the unsigned APK.");
                    Toast toast = Toast.makeText(mContext.getApplicationContext(),
                            mContext.getResources().getString(
                                    R.string.UnsignedAPKCreator_exception_toast),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (Exception e) {
                did_it_compile = false;
                Log.e("unzipNewAPK", "There has been an exception while trying to decompress " +
                        "the APK.");
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.unzipNewAPK_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        public void unzipHeaderPacks() throws IOException {
            String source = header_pack_location;
            String destination = mContext.getCacheDir().getAbsolutePath() +
                    "/creative_mode/assets/overlays/com.android.systemui/res/drawable-xxhdpi-v20/";

            ZipInputStream inputStream = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(source)));
            try {
                ZipEntry zipEntry;
                int count;
                byte[] buffer = new byte[8192];
                while ((zipEntry = inputStream.getNextEntry()) != null) {
                    File file = new File(destination, zipEntry.getName());
                    File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (zipEntry.isDirectory())
                        continue;
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        while ((count = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    } finally {
                        outputStream.close();
                    }
                }
                File headers_xml = new File(mContext.getCacheDir().getAbsolutePath() +
                        "/creative_mode/assets/overlays/com.android.systemui/res/" +
                        "drawable-xxhdpi-v20/headers.xml");
                boolean deleted = headers_xml.delete();
                inputStream.close();
                Log.d("unzipHeaderPacks", "Finished decompressing header pack archive!");
            } catch (Exception e) {
                did_it_compile = false;
                Log.e("unzipHeaderPacks", "There has been an exception while trying to " +
                        "decompress the ZIP.");
                Toast toast = Toast.makeText(mContext.getApplicationContext(),
                        mContext.getResources().getString(
                                R.string.unzipHeaderPacks_exception_toast),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class Phase6_SignAndInstall extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Log.d("Phase 6", "This phase has started it's asynchronous task.");
            loader.setProgress(10);
            if (!header_creative_mode_activated) {
                loader_string.setText(mContext.getResources().getString(R.string.phase6_dialog_title));
            } else {
                loader_string.setText(mContext.getResources().getString(R.string.phase6_hp_dialog_title));
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            mWakeLock.release();
        }

        @Override
        protected String doInBackground(String... sUrl) {

            try {
                // Sign with the built-in auto-test key/certificate.
                String source = mContext.getCacheDir().getAbsolutePath() +
                        "/dashboard_creation_unsigned.apk";
                String destination = mContext.getCacheDir().getAbsolutePath() +
                        "/dashboard_creation_signed.apk";

                ZipSigner zipSigner = new ZipSigner();
                zipSigner.setKeymode("testkey");
                zipSigner.signZip(source, destination);

                Log.d("ZipSigner", "APK successfully signed!");

                // Delete the previous APK if it exists in the dashboard folder
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "rm -r " + Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/dashboard./dashboard_creation_signed.apk");

                // We need root to copy this file because as of this moment, there are extra needs
                // to care for when transferring this file due to permissions.
                eu.chainfire.libsuperuser.Shell.SU.run(
                        "cp " + mContext.getCacheDir().getAbsolutePath() +
                                "/dashboard_creation_signed.apk " +
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/dashboard./dashboard_creation_signed.apk");
            } catch (Throwable t) {
                did_it_compile = false;
                Log.e("ZipSigner", "APK could not be signed. " + t.toString());
            }

            // Once the transfer is complete, launch it like a normal APK
            if (did_it_compile) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/dashboard./dashboard_creation_signed.apk")),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

            // Temporary cache folder MUST be cleared for next run.
            cleanTempFolder();

            return null;
        }

        public void cleanTempFolder() {
            File dir = mContext.getCacheDir();
            deleteRecursive(dir);
        }

        private void deleteRecursive(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        }
    }
}