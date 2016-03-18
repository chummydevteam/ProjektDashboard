package projekt.dashboard.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.assent.AssentActivity;
import com.afollestad.materialdialogs.util.DialogUtils;

import projekt.dashboard.R;
import projekt.dashboard.config.Config;
import projekt.dashboard.util.TintUtils;
import projekt.dashboard.util.Utils;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class BaseThemedActivity extends AssentActivity {

    public static void themeMenu(Context context, Menu menu) {
        final int tintColor = DialogUtils.resolveColor(context, R.attr.toolbar_icons_color);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null)
                item.setIcon(TintUtils.createTintedDrawable(item.getIcon(), tintColor));
        }
    }

    public abstract Toolbar getToolbar();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Config.init(this);
        setTheme(getCurrentTheme());
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !DialogUtils.resolveBoolean(this, R.attr.disable_auto_light_status_bar)) {
            final View decorView = getWindow().getDecorView();
            final boolean lightStatusEnabled = DialogUtils.resolveBoolean(this, R.attr.force_light_status_bar) ||
                    TintUtils.isColorLight(DialogUtils.resolveColor(this, R.attr.colorPrimaryDark));
            final int systemUiVisibility = decorView.getSystemUiVisibility();
            if (lightStatusEnabled) {
                decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    @SuppressLint("PrivateResource")
    @Override
    protected void onStart() {
        super.onStart();
        final Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            final int titleColor = DialogUtils.resolveColor(this, R.attr.toolbar_title_color);
            final int iconColor = DialogUtils.resolveColor(this, R.attr.toolbar_icons_color);
            toolbar.setTitleTextColor(titleColor);
            Utils.setOverflowButtonColor(this, iconColor);

            if (TintUtils.isColorLight(titleColor)) {
                toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
            } else {
                toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Config.setContext(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        themeMenu(this, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected boolean isTranslucent() {
        return false;
    }

    @StyleRes
    private int getCurrentTheme() {
        return R.style.AppTheme_Dark;
    }
}