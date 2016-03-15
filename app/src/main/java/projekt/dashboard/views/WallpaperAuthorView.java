package projekt.dashboard.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import projekt.dashboard.util.WallpaperUtils;

/**
 * @author Aidan Follestad (afollestad)
 */
public class WallpaperAuthorView extends TextView {

    private WallpaperUtils.Wallpaper mWallpaper;

    public WallpaperAuthorView(Context context) {
        super(context);
    }

    public WallpaperAuthorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WallpaperAuthorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWallpaper(WallpaperUtils.Wallpaper viewHolder) {
        mWallpaper = viewHolder;
    }

    @Override
    public void setTextColor(int color) {
        setTextColor(color, true);
    }

    public void setTextColor(int color, boolean cache) {
        super.setTextColor(color);
        if (cache && mWallpaper != null)
            mWallpaper.setPaletteAuthorColor(color);
    }
}
