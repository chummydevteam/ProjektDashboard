package projekt.dashboard.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import projekt.dashboard.R;

/**
 * Created by Nicholas on 2016-03-20.
 */
public class AppIntroduction extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance(
                "welcome to dashboard",
                "dashboard is a cm theme engine addon by chummy development team that allows" +
                        " you to expand the possibilities of all cdt themes, as well as extras " +
                        "for other themes as well.",
                R.drawable.homepage_icon,
                Color.parseColor("#212021")
        ));
        addSlide(AppIntroFragment.newInstance(
                "color picker",
                "dashboard comes with a built in color picker for hotswapping of colors!\n\n" +
                        "please allow it to automatically download a small patch file for " +
                        "itself before you leave your internet connection.",
                R.drawable.painbrush_palette,
                Color.parseColor("#212021")
        ));
        addSlide(AppIntroFragment.newInstance(
                "contextual header swapper",
                "dashboard also comes with a built in contextual header swapper to customize " +
                        "your phone further without the use of useless xposed plugins or making " +
                        "a theme yourself!",
                R.drawable.phone_heart,
                Color.parseColor("#212021")
        ));
        addSlide(AppIntroFragment.newInstance(
                "theme engine in need of diagnosing?",
                "dashboard also comes with built in theme utilities to offer you a" +
                        "plethora of options to help you fix your theme engine directly on your " +
                        "phone!\n\n",
                R.drawable.theme_utilities,
                Color.parseColor("#212021")
        ));
        addSlide(AppIntroFragment.newInstance(
                "this app needs root!",
                "to use all the functions of the app, your phone must definitely have root " +
                        "enabled, as well as a custom rom flashed if you would like to have a " +
                        "contextual header!",
                R.drawable.needs_root,
                Color.parseColor("#212021")
        ));
        addSlide(AppIntroFragment.newInstance(
                "are you ready?",
                "to use all the functions of the app, your phone must definitely have root" +
                        "enabled, as well as a custom rom flashed if you would like to have a" +
                        "contextual header!" +
                        "phone!\n\n",
                R.drawable.are_you_ready,
                Color.parseColor("#212021")
        ));

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());
        prefs.edit().putBoolean("first_run", false).commit();
        Intent intent = new Intent(AppIntroduction.this, MainActivity.class);
        startActivity(intent);
        finish();
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