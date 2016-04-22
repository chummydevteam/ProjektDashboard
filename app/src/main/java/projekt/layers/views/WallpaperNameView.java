package projekt.layers.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import projekt.layers.util.WallpaperUtils;

/**
 * @author Aidan Follestad (afollestad)
 */
public class WallpaperNameView extends TextView {

    private WallpaperUtils.Wallpaper mWallpaper;

    public WallpaperNameView(Context context) {
        super(context);
    }

    public WallpaperNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WallpaperNameView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            mWallpaper.setPaletteNameColor(color);
    }
}
