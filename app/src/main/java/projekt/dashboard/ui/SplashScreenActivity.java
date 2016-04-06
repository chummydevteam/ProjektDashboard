package projekt.dashboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;
import projekt.dashboard.R;

/**
 * Created by Nicholas on 2016-03-31.
 */
public class SplashScreenActivity extends Activity {

    public TextView tv;
    /**
     * Called when the activity is first created.
     */
    Thread splashThread;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        StartAnimations();
    }

    private void StartAnimations() {
        tv = (TextView) findViewById(R.id.loadingText);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.spin);
        anim.reset();
        anim2.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        ImageView iv2 = (ImageView) findViewById(R.id.spinnerWheel);
        iv.clearAnimation();
        iv2.clearAnimation();
        //iv.startAnimation(anim);
        iv2.startAnimation(anim2);


        splashThread = new Thread() {
            @Override
            public void run() {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext());
                try {
                    sleep(300);
                    if (Shell.SU.available()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.root_verified));
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.root_unverified));
                            }
                        });
                    }
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2500) {
                        sleep(100);
                        waited += 100;
                    }
                    if (prefs.getBoolean("first_run", true)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.initial_run));
                            }
                        });
                        sleep(3000);
                        startActivity(new Intent(SplashScreenActivity.this, AppIntroduction.class));
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(getResources().getString(R.string.welcome_back) +
                                        " " + prefs.getString("dashboard_username", getResources().
                                        getString(R.string.welcome_back_default_username)) + "!");
                            }
                        });
                        sleep(300);
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreenActivity.this.finish();
                }
            }
        };
        splashThread.start();

    }

}