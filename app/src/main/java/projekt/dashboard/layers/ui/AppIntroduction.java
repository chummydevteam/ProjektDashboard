package projekt.dashboard.layers.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import com.alimuzaffar.lib.widgets.AnimatedEditText;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import projekt.dashboard.layers.R;

/**
 * Created by Nicholas on 2016-03-20.
 */
public class AppIntroduction extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance(getString(R.string.first_slide_title), getString(R
                .string.first_slide_description), R.drawable.homepage_icon, Color.parseColor
                ("#212021")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.second_slide_title), getString(R
                .string.second_slide_description), R.drawable.painbrush_palette, Color.parseColor
                ("#212021")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.fourth_slide_title), getString(R
                .string.fourth_slide_description), R.drawable.phone_heart, Color.parseColor
                ("#212021")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.fifth_slide_title), getString(R
                .string.fifth_slide_description), R.drawable.theme_utilities, Color.parseColor
                ("#212021")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.sixth_slide_title), getString(R
                .string.sixth_slide_description), R.drawable.needs_root, Color.parseColor
                ("#212021")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.last_slide_title), getString(R
                .string.last_slide_description), R.drawable.are_you_ready, Color.parseColor
                ("#212021")));

        showDoneButton(true);
        showSkipButton(false);
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        prefs.edit().putBoolean("first_run", false).commit();
        Intent intent = new Intent(AppIntroduction.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        prefs.edit().putBoolean("first_run", false).commit();
        prefs.edit().putBoolean("blacked_out_enabled", false).commit();
        prefs.edit().putBoolean("extended_actionbar_enabled", false).commit();
        prefs.edit().putBoolean("advanced_mode_enabled", true).commit();
        prefs.edit().putBoolean("header_downloader_low_power_mode", false).commit();

        prefs.edit().putBoolean("color_switcher_enabled", true).commit();
        prefs.edit().putBoolean("header_swapper_enabled", true).commit();
        prefs.edit().putBoolean("header_importer_enabled", true).commit();
        prefs.edit().putBoolean("theme_debugging_enabled", true).commit();
        prefs.edit().putBoolean("wallpapers_enabled", true).commit();

        AlertDialog.Builder alert = new AlertDialog.Builder(AppIntroduction.this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.name_picker_dialog, null);
        alert.setView(dialogView);
        final AnimatedEditText textBox = (AnimatedEditText) dialogView.findViewById(R.id.editText);

        alert.setMessage(getResources().getString(R.string.welcome_back_dialog_message));
        alert.setTitle(getResources().getString(R.string.welcome_back_dialog_title));
        alert.setCancelable(false);
        alert.setPositiveButton(getResources().getString(R.string.welcome_back_dialog_confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String string_processor = textBox.getText().toString();
                        if (string_processor.endsWith(" ")) {
                            string_processor = string_processor.substring(0,
                                    string_processor.length() - 1);
                        }
                        prefs.edit().putString("dashboard_username", string_processor).commit();
                        startActivity(new Intent(AppIntroduction.this, MainActivity.class));
                        finish();
                    }
                });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}
