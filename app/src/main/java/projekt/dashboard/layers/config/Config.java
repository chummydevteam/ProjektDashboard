package projekt.dashboard.layers.config;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import projekt.dashboard.layers.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Config implements IConfig {

    private static Config mConfig;
    private Context mContext;
    private Resources mR;

    private Config(@Nullable Context context) {
        mR = null;
        mContext = context;
        if (context != null)
            mR = context.getResources();
    }

    public static void init(@NonNull Context context) {
        mConfig = new Config(context);
    }

    public static void setContext(Context context) {
        if (mConfig != null) {
            mConfig.mContext = context;
            mConfig.mR = context.getResources();
        }
    }

    public static void deinit() {
        mConfig.destroy();
        mConfig = null;
    }

    @NonNull
    public static IConfig get() {
        if (mConfig == null)
            return new Config(null);
        return mConfig;
    }

    private void destroy() {
        mContext = null;
        mR = null;
    }

    @Override
    public boolean persistSelectedPage() {
        return mR == null || mR.getBoolean(R.bool.persist_selected_page);
    }

    @Override
    public int gridWidthWallpaper() {
        if (mR == null) return 2;
        return mR.getInteger(R.integer.wallpaper_grid_width);
    }
}