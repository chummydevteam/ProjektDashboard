package colorswitcher.chummy.aditya.colorswitcher;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;

import colorswitcher.chummy.aditya.colorswitcher.slide.SampleSlide;

/**
 * Created by adity on 4/13/2016.
 */
public class IntroActivity extends AppIntro2 {
    final String PREFS_NAME = "MyPrefsIntroFile";

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(SampleSlide.newInstance(R.layout.intro1));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro4));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro5));

        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        askForPermissions(new String[]{Manifest.permission.INTERNET}, 3);
        askForPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 4);

    }
    // Add your slide's fragments here.
    // AppIntro will automatically generate the dots indicator and buttons.
    // Instead of fragments, you can also use our default slide


    @Override
    public void onDonePressed() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    public void getStarted(View v) {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
    }
}