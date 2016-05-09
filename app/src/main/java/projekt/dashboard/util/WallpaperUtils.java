package projekt.dashboard.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Callback;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.afollestad.bridge.annotations.Body;
import com.afollestad.bridge.annotations.ContentType;
import com.afollestad.inquiry.Inquiry;
import com.afollestad.inquiry.annotations.Column;
import com.afollestad.inquiry.callbacks.RunCallback;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

import projekt.dashboard.R;
import projekt.dashboard.fragments.WallpapersFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class WallpaperUtils {

    public static final String TABLE_NAME = "polar_wallpapers";
    public static final String DATABASE_NAME = "data_cache";
    public static final int DATABASE_VERSION = 1;
    private static Activity mContextCache;
    private static Wallpaper mWallpaperCache;
    private static boolean mApplyCache;
    private static File mFileCache;
    private static Toast mToast;

    private WallpaperUtils() {
    }

    public static WallpapersHolder getAll(final Context context, boolean allowCached) throws Exception {
        Inquiry.init(context, DATABASE_NAME, DATABASE_VERSION);
        try {
            if (allowCached) {
                Wallpaper[] cache = Inquiry.get().selectFrom(TABLE_NAME, Wallpaper.class).all();
                if (cache != null && cache.length > 0) {
                    Log.d("WallpaperUtils", String.format("Loaded %d wallpapers from cache.", cache.length));
                    return new WallpapersHolder(cache);
                }
            } else {
                Inquiry.get().deleteFrom(TABLE_NAME, Wallpaper.class).run();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

        try {
            String defaultSource = "";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String mapTypeString = prefs.getString("selected_wallpaper_source", "default");
            if (!mapTypeString.equals("default")) {
                if (mapTypeString.equals("customworx_du")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_customworx_du);
                }
                if (mapTypeString.equals("customworx_octos")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_customworx_octOS);
                }
                if (mapTypeString.equals("customworx_screwd")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_customworx_screwd);
                }
                if (mapTypeString.equals("customworx")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_customworx);
                }
                if (mapTypeString.equals("gagan_du")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_gagan_du);
                }
                if (mapTypeString.equals("gagan")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_gagan);
                }
                if (mapTypeString.equals("vignesh_du")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_vignesh_du);
                }
                if (mapTypeString.equals("vignesh")) {
                    defaultSource = context.getString(R.string.wallpapers_json_url_vignesh);
                }
            } else {
                defaultSource = context.getString(R.string.wallpapers_json_url);
            }

            WallpapersHolder holder = Bridge.get(defaultSource)
                    .tag(WallpapersFragment.class.getName())
                    .asClass(WallpapersHolder.class);
            if (holder == null)
                throw new Exception("No wallpapers returned.");
            Log.d("WallpaperUtils", String.format("Loaded %d wallpapers from web.", holder.length()));
            if (holder.length() > 0) {
                try {
                    Inquiry.init(context, DATABASE_NAME, DATABASE_VERSION);
                    Inquiry.get().insertInto(TABLE_NAME, Wallpaper.class)
                            .values(holder.wallpapers)
                            .run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return holder;
        } catch (Exception e1) {
            Log.d("WallpaperUtils", String.format("Failed to load wallpapers... %s", e1.getMessage()));
            throw e1;
        } finally {
            Inquiry.deinit();
        }
    }

    public static void saveDb(@Nullable final Context context, @Nullable final WallpapersHolder holder) {
        if (context == null || holder == null || holder.length() == 0) return;
        Inquiry.init(context, DATABASE_NAME, DATABASE_VERSION);
        try {
            Inquiry.get().deleteFrom(TABLE_NAME, Wallpaper.class).run();
            Inquiry.get().insertInto(TABLE_NAME, Wallpaper.class)
                    .values(holder.wallpapers)
                    .run(new RunCallback<Long[]>() {
                        @Override
                        public void result(Long[] changed) {
                            Inquiry.deinit();
                        }
                    });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void getAll(final Context context, boolean allowCached, final WallpapersCallback callback) {
        Inquiry.init(context, DATABASE_NAME, DATABASE_VERSION);
        try {
            if (allowCached) {
                Wallpaper[] cache = Inquiry.get().selectFrom(TABLE_NAME, Wallpaper.class).all();
                if (cache != null && cache.length > 0) {
                    Log.d("WallpaperUtils", String.format("Loaded %d wallpapers from cache.", cache.length));
                    callback.onRetrievedWallpapers(new WallpapersHolder(cache), null, false);
                    return;
                }
            } else {
                Inquiry.get().deleteFrom(TABLE_NAME, Wallpaper.class).run();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        String defaultSource = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mapTypeString = prefs.getString("selected_wallpaper_source", "default");
        if (!mapTypeString.equals("default")) {
            if (mapTypeString.equals("customworx_du")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_customworx_du);
            }
            if (mapTypeString.equals("customworx_octos")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_customworx_octOS);
            }
            if (mapTypeString.equals("customworx_screwd")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_customworx_screwd);
            }
            if (mapTypeString.equals("customworx")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_customworx);
            }
            if (mapTypeString.equals("gagan_du")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_gagan_du);
            }
            if (mapTypeString.equals("gagan")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_gagan);
            }
            if (mapTypeString.equals("vignesh_du")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_vignesh_du);
            }
            if (mapTypeString.equals("vignesh")) {
                defaultSource = context.getString(R.string.wallpapers_json_url_vignesh);
            }
        } else {
            defaultSource = context.getString(R.string.wallpapers_json_url);
        }

        Bridge.get(defaultSource)
                .tag(WallpapersFragment.class.getName())
                .asClass(WallpapersHolder.class, new ResponseConvertCallback<WallpapersHolder>() {
                    @Override
                    public void onResponse(@NonNull Response response, @Nullable WallpapersHolder holder, @Nullable BridgeException e) {
                        if (e != null) {
                            callback.onRetrievedWallpapers(null, e, e.reason() == BridgeException.REASON_REQUEST_CANCELLED);
                        } else {
                            if (holder == null) {
                                callback.onRetrievedWallpapers(null, new Exception("No wallpapers returned."), false);
                                return;
                            }
                            try {
                                Log.d("WallpaperUtils", String.format("Loaded %d wallpapers from web.", holder.length()));
                                if (holder.length() > 0) {
                                    try {
                                        Inquiry.init(context, DATABASE_NAME, DATABASE_VERSION);
                                        Inquiry.get().insertInto(TABLE_NAME, Wallpaper.class)
                                                .values(holder.wallpapers)
                                                .run();
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                                callback.onRetrievedWallpapers(holder, null, false);
                            } catch (Throwable e1) {
                                Log.d("WallpaperUtils", String.format("Failed to load wallpapers... %s", e1.getMessage()));
                                if (e1 instanceof Exception)
                                    callback.onRetrievedWallpapers(null, (Exception) e1, false);
                            } finally {
                                Inquiry.deinit();
                            }
                        }
                    }
                });
    }

    private static void showToast(Context context, @StringRes int msg) {
        showToast(context, context.getString(msg));
    }

    private static void showToast(Context context, String msg) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void download(final Activity context, final Wallpaper wallpaper, final boolean apply) {
        mContextCache = context;
        mWallpaperCache = wallpaper;
        mApplyCache = apply;

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet permissionResultSet) {
                    if (permissionResultSet.isGranted(Assent.WRITE_EXTERNAL_STORAGE))
                        download(mContextCache, mWallpaperCache, mApplyCache);
                    else
                        Toast.makeText(context, R.string.write_storage_permission_denied, Toast.LENGTH_LONG).show();
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
            return;
        }

        final File saveFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
        //noinspection ResultOfMethodCallIgnored
        saveFolder.mkdirs();

        final String name;
        final String extension = wallpaper.url.toLowerCase(Locale.getDefault()).endsWith(".png") ? "png" : "jpg";
        if (apply) {
            // Crop/Apply
            name = String.format("%s_%s_wallpaper.%s",
                    wallpaper.name.replace(" ", "_"),
                    wallpaper.author.replace(" ", "_"),
                    extension);
        } else {
            // Save
            name = String.format("%s_%s.%s",
                    wallpaper.name.replace(" ", "_"),
                    wallpaper.author.replace(" ", "_"),
                    extension);
        }

        mFileCache = new File(saveFolder, name);

        if (!mFileCache.exists()) {
            final MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .content(R.string.downloading_wallpaper)
                    .progress(true, -1)
                    .cancelable(true)
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if (mContextCache != null && !mContextCache.isFinishing())
                                showToast(mContextCache, R.string.download_cancelled);
                            Bridge.cancelAll()
                                    .tag(WallpaperUtils.class.getName())
                                    .commit();
                        }
                    }).show();
            Bridge.get(wallpaper.url)
                    .tag(WallpaperUtils.class.getName())
                    .request(new Callback() {
                        @Override
                        public void response(Request request, Response response, BridgeException e) {
                            if (e != null) {
                                dialog.dismiss();
                                if (e.reason() == BridgeException.REASON_REQUEST_CANCELLED) return;
                                Utils.showError(context, e);
                            } else {
                                try {
                                    response.asFile(mFileCache);
                                    finishOption(mContextCache, apply, dialog);
                                } catch (BridgeException e1) {
                                    dialog.dismiss();
                                    Utils.showError(context, e1);
                                }
                            }
                        }
                    });
        } else {
            finishOption(context, apply, null);
        }
    }

    private static void finishOption(final Activity context, boolean apply, @Nullable final MaterialDialog dialog) {
        MediaScannerConnection.scanFile(context,
                new String[]{mFileCache.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("WallpaperScan", "Scanned " + path + ":");
                        Log.i("WallpaperScan", "-> uri = " + uri);
                    }
                });

        if (apply) {
            // Apply
            if (dialog != null)
                dialog.dismiss();
            final Intent intent = new Intent(Intent.ACTION_ATTACH_DATA)
                    .setDataAndType(Uri.fromFile(mFileCache), "image/*")
                    .putExtra("mimeType", "image/*");
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.set_wallpaper_using)));
        } else {
            // Save
            if (dialog != null)
                dialog.dismiss();
            showToast(context, context.getString(R.string.saved_to_x, mFileCache.getAbsolutePath()));
            resetOptionCache(false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void resetOptionCache(boolean delete) {
        mContextCache = null;
        mWallpaperCache = null;
        mApplyCache = false;
        if (delete && mFileCache != null) {
            mFileCache.delete();
            final File[] contents = mFileCache.getParentFile().listFiles();
            if (contents != null && contents.length > 0)
                mFileCache.getParentFile().delete();
        }
    }

    public interface WallpapersCallback {
        void onRetrievedWallpapers(WallpapersHolder wallpapers, Exception error, boolean cancelled);
    }

    @ContentType("application/json")
    public static class WallpapersHolder implements Serializable {

        @Body
        public Wallpaper[] wallpapers;

        public WallpapersHolder() {
        }

        public WallpapersHolder(Wallpaper[] wallpapers) {
            this.wallpapers = wallpapers;
        }

        public Wallpaper get(int index) {
            return wallpapers[index];
        }

        public int length() {
            return wallpapers != null ? wallpapers.length : 0;
        }
    }

    @ContentType("application/json")
    public static class Wallpaper implements Serializable {

        @Column(primaryKey = true, notNull = true, autoIncrement = true)
        public long _id;
        @Body
        @Column
        public String author;
        @Body
        @Column
        public String url;
        @Body
        @Column
        public String name;
        @Body
        @Column
        public String thumbnail;
        @Column
        private int paletteNameColor;
        @Column
        private int paletteAuthorColor;
        @Column
        private int paletteBgColor;

        public Wallpaper() {
        }

        public String getListingImageUrl() {
            return thumbnail != null ? thumbnail : url;
        }

        @ColorInt
        public int getPaletteNameColor() {
            return paletteNameColor;
        }

        public void setPaletteNameColor(@ColorInt int color) {
            this.paletteNameColor = color;
        }

        @ColorInt
        public int getPaletteAuthorColor() {
            return paletteAuthorColor;
        }

        public void setPaletteAuthorColor(@ColorInt int color) {
            this.paletteAuthorColor = color;
        }

        @ColorInt
        public int getPaletteBgColor() {
            return paletteBgColor;
        }

        public void setPaletteBgColor(@ColorInt int color) {
            this.paletteBgColor = color;
        }

        public boolean isPaletteComplete() {
            return paletteNameColor != 0 && paletteAuthorColor != 0 && paletteBgColor != 0;
        }
    }
}