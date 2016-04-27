package projekt.dashboard.layers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import butterknife.ButterKnife;
import projekt.dashboard.layers.BuildConfig;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.fragments.base.BasePageFragment;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class HomeFragment extends BasePageFragment {

    SharedPreferences prefs;

    final public static boolean checkRomSupported(Context context) {

        if (isAppInstalled(context, "com.lovejoy777.rroandlayersmanager")) {
            return true;
        } else {
            return false;
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
