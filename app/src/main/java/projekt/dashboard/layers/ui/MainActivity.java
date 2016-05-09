package projekt.dashboard.layers.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.afollestad.bridge.Bridge;
import com.afollestad.materialdialogs.util.DialogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.chainfire.libsuperuser.Shell;
import projekt.dashboard.layers.R;
import projekt.dashboard.layers.adapters.MainPagerAdapter;
import projekt.dashboard.layers.config.Config;
import projekt.dashboard.layers.fragments.ColorChangerFragment;
import projekt.dashboard.layers.fragments.HeaderImportFragment;
import projekt.dashboard.layers.fragments.HeaderSwapperFragment;
import projekt.dashboard.layers.fragments.HomeFragment;
import projekt.dashboard.layers.fragments.ThemeUtilitiesFragment;
import projekt.dashboard.layers.fragments.WallpapersFragment;
import projekt.dashboard.layers.fragments.base.BasePageFragment;
import projekt.dashboard.layers.ui.base.BaseDonateActivity;
import projekt.dashboard.layers.util.DrawableXmlParser;
import projekt.dashboard.layers.util.PagesBuilder;
import projekt.dashboard.layers.util.WallpaperUtils;
import projekt.dashboard.layers.views.DisableableViewPager;

import static projekt.dashboard.layers.fragments.WallpapersFragment.RQ_CROPANDSETWALLPAPER;
import static projekt.dashboard.layers.fragments.WallpapersFragment.RQ_VIEWWALLPAPER;
import static projekt.dashboard.layers.viewer.ViewerActivity.STATE_CURRENT_POSITION;

/**
 * @author Adityata
 */
public class MainActivity extends BaseDonateActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public RecyclerView mRecyclerView;
    public SharedPreferences prefs;
    final String PREFS_NAME = "MyPrefsFile";
    String link64 = "https://dl.dropboxusercontent.com/u/" +
            "2429389/dashboard.%20files/aapt-64";
    String link = "https://dl.dropboxusercontent.com/u/" +
            "2429389/dashboard.%20files/aapt";
    public String vendor = "/system/vendor/overlay";
    public String mount = "/system";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Nullable
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Nullable
    @Bind(R.id.navigation_view)
    NavigationView mNavView;
    @Nullable
    @Bind(R.id.drawer)
    DrawerLayout mDrawer;
    @Bind(R.id.pager)
    DisableableViewPager mPager;
    @Nullable
    @Bind(R.id.app_bar)
    LinearLayout mAppBarLinear;
    private PagesBuilder mPages;

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        setupPages();
        setupPager();
        setupTabs();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            if (isNetworkAvailable()) {
                if (HomeFragment.checkThemeMainSupported(this) && HomeFragment.checkThemeSysSupported(this)) {
                    Log.e("Switcher", "First time");
                    Log.e("DownloadAAPT", "Calling Function");
                    downloadAAPT();
                    // record the fact that the app has been started at least once
                    settings.edit().putBoolean("my_first_time", false).commit();
                }
            }
        }

        // Restore last selected page, tab/nav-drawer-item
        if (Config.get().persistSelectedPage()) {
            int lastPage = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                    .getInt("last_selected_page", 0);
            if (lastPage > mPager.getAdapter().getCount() - 1) lastPage = 0;
            mPager.setCurrentItem(lastPage);
            if (mNavView != null) invalidateNavViewSelection(lastPage);
        }
        processIntent(getIntent());
    }

    public void downloadAAPT() {
        Log.e("DownloadAAPT", "Function Called");
        Log.e("DownloadAAPT", "Function Started");
        Log.e("Checkbitphone", "Calling Function");
        boolean flag = ColorChangerFragment.checkbitphone();
        if (flag) {
            Log.e("DownloadAAPT", "64 Bit Active");
            Log.e("64 bit Device ", Build.DEVICE + " Found,now changing the vendor and mount");
            vendor = "/vendor/overlay";
            mount = "/vendor";
            Log.e("64 bit Device ", Build.DEVICE + " changed the vendor and mount");
            String[] downloadCommands = {link64,
                    "aapt"};
            Log.e("DownloadindResources", "Calling Function");
            new downloadResources().execute(downloadCommands);
            Log.e("DownloadAAPT", "Function Stopped");
        } else

        {
            Log.e("DownloadAAPT", "32 Bit Active");
            Log.e("32 bit Device ", Build.DEVICE + " Found,now changing the vendor and mount");
            vendor = "/system/vendor/overlay";
            mount = "/system";
            Log.e("32 bit Device ", Build.DEVICE + " changed the vendor and mount");
            String[] downloadCommands = {link,
                    "aapt"};
            new downloadResources().execute(downloadCommands);
            Log.e("DownloadAAPT", "Function Stopped");
        }
    }

    private class downloadResources extends AsyncTask<String, Integer, String> {

        private ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            Log.e("Downloadind Resources", "Function Called");
            Log.e("Downloadind Resources", "Function Started");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Downloading Resources");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            pd.setMessage("Download Complete,Getting Things Finalised");
            Log.e("File Downloaded Found", "Copying File");
            Log.e("copyAAPT", "Calling Function");
            copyAAPT();
            pd.dismiss();
            Log.e("Downloadind Resources", "Function Stoppped");
        }

        public void copyAAPT() {
            Log.e("copyAAPT", "Function Called");
            Log.e("copyAAPT", "Function Started");
            Log.e("copyAAPT", "Start");
            String mount = new String("mount -o remount,rw /");
            String mountsys = new String("mount -o remount,rw /system");
            String remount = new String("mount -o remount,ro /");
            String remountsys = new String("mount -o remount,ro /system");
            eu.chainfire.libsuperuser.Shell.SU.run(mount);
            Log.e("copyAAPT", "Mounted /");
            eu.chainfire.libsuperuser.Shell.SU.run(mountsys);
            Log.e("copyAAPT", "Mounted " + mount);

            eu.chainfire.libsuperuser.Shell.SU.run(
                    "cp " +
                            getFilesDir().getAbsolutePath() +
                            "/aapt" + " /system/bin/aapt");
            eu.chainfire.libsuperuser.Shell.SU.run("chmod 777 /system/bin/aapt");
            Log.e("copyAAPT", "Copied AAPT");
            eu.chainfire.libsuperuser.Shell.SU.run(remount);
            Log.e("copyAAPT", "ReMounted /");
            eu.chainfire.libsuperuser.Shell.SU.run(remountsys);
            Log.e("copyAAPT", "ReMounted " + mount);
            Log.e("copyAAPT", "End");
            Log.e("copyAAPT", "Function Stopped");
        }


        @Override
        protected String doInBackground(String... sUrl) {
            try {
                Log.e("File download", "Started from :" + sUrl[0]);
                URL url = new URL(sUrl[0]);
                //URLConnection connection = url.openConnection();
                File myDir = new File(getFilesDir().getAbsolutePath());
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(sUrl[0]);
                request.setHeader("User-Agent", sUrl[0]);

                HttpResponse response = client.execute(request);
                // create the directory if it doesnt exist
                if (!myDir.exists()) myDir.mkdirs();

                File outputFile = new File(myDir, sUrl[1]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int fileLength = connection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                Log.e("File download", "complete");
            } catch (Exception e) {
                Log.e("File download", "error: " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (Intent.ACTION_SET_WALLPAPER.equals(intent.getAction())) {
            for (int i = 0; i < mPages.size(); i++) {
                PagesBuilder.Page page = mPages.get(i);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupPages() {
        mPages = new PagesBuilder(6);
        mPages.add(new PagesBuilder.Page(R.id.home_fragment, R.drawable.tab_home,
                R.string.home_tab_one, new HomeFragment()));
        if (Shell.SU.available()) {
            if (HomeFragment.checkThemeMainSupported(this)) {
                mPages.add(new PagesBuilder.Page(R.id.color_changer_fragment, R.drawable.tab_palette,
                        R.string.home_tab_two, new ColorChangerFragment()));
            }
        }
        if (Shell.SU.available()) {
            if (HomeFragment.checkThemeMainSupported(this)) {
                if (HomeFragment.checkThemeSysSupported(this)) {
                    mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable.tab_swapper,
                            R.string.home_tab_three, new HeaderSwapperFragment()));
                    mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable.tab_header_import,
                            R.string.home_tab_four, new HeaderImportFragment()));
                }
            }
        }
        if (Shell.SU.available()) {
            mPages.add(new PagesBuilder.Page(R.id.theme_utilities_fragment, R.drawable.tab_creator,
                    R.string.home_tab_five, new ThemeUtilitiesFragment()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    void invalidateNavViewSelection(int position) {
        assert mNavView != null;
        final int selectedId = mPages.get(position).drawerId;
        mNavView.post(new Runnable() {
            @Override
            public void run() {
                mNavView.setCheckedItem(selectedId);
            }
        });
    }

    private void setupPager() {
        mPager.setAdapter(new MainPagerAdapter(getFragmentManager(), mPages));
        mPager.setOffscreenPageLimit(mPages.size() - 1);
        // Paging is only enabled in tab mode
        mPager.setPagingEnabled(true);
    }

    private void setupTabs() {
        assert mTabs != null;
        mTabs.setTabMode(mPages.size() > 6 ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        mTabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                dispatchFragmentUpdateTitle(false);
            }
        });

        for (PagesBuilder.Page page : mPages)
            addTab(page.iconRes);
        mTabs.setSelectedTabIndicatorColor(DialogUtils.resolveColor(
                this, R.attr.tab_indicator_color));
    }

    void dispatchFragmentUpdateTitle(final boolean checkTabsLocation) {
        //First set the presumed title, then let fragment do anything specific.
        setTitle(mPages.get(mPager.getCurrentItem()).titleRes);

        mPager.post(new Runnable() {
            @Override
            public void run() {
                final BasePageFragment frag = (BasePageFragment) getFragmentManager()
                        .findFragmentByTag("page:" + mPager.getCurrentItem());
                if (frag != null) frag.updateTitle();

                if (checkTabsLocation) {
                    moveTabsIfNeeded();
                }
            }
        });
    }

    void moveTabsIfNeeded() {
        final CharSequence currentTitle = getTitle();

        String longestTitle = null;
        for (PagesBuilder.Page page : mPages) {
            String title = getString(page.titleRes);
            if (longestTitle == null || title.length() > longestTitle.length()) {
                longestTitle = title;
            }
        }
        setTitle(longestTitle);

        if (mTabs != null) {
            ViewTreeObserver vto = mToolbar.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (mToolbar.isTitleTruncated() && mTabs.getParent() == mToolbar) {
                        mToolbar.removeView(mTabs);
                        //noinspection ConstantConditions
                        mAppBarLinear.addView(mTabs);
                    }

                    setTitle(currentTitle);

                    mToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer)).closeDrawers();
        final int index = mPages.findPositionForItem(item);
        if (index > -1)
            mPager.setCurrentItem(index, false);
        return false;
    }

    private void addTab(@DrawableRes int icon) {
        assert mTabs != null;
        TabLayout.Tab tab = mTabs.newTab().setIcon(icon);
        if (tab.getIcon() != null) {
            Drawable tintedIcon = DrawableCompat.wrap(tab.getIcon());
            DrawableCompat.setTint(tintedIcon, DialogUtils.resolveColor(
                    this, R.attr.tab_icon_color));
            tab.setIcon(tintedIcon);
        }
        mTabs.addTab(tab);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Config.get().persistSelectedPage()) {
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                    .edit().putInt("last_selected_page", mPager.getCurrentItem()).commit();
        }
        if (isFinishing()) {
            Config.deinit();
            Bridge.destroy();
            DrawableXmlParser.cleanup();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_CROPANDSETWALLPAPER) {
            WallpapersFragment.showToast(this, R.string.wallpaper_set);
            WallpaperUtils.resetOptionCache(true);
        } else if (requestCode == RQ_VIEWWALLPAPER) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mDrawer != null) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                mDrawer.setStatusBarBackgroundColor(DialogUtils.resolveColor(
                        this, R.attr.colorPrimaryDark));
            }
            if (mRecyclerView != null) {
                mRecyclerView.requestFocus();
                final int currentPos = data.getIntExtra(STATE_CURRENT_POSITION, 0);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.smoothScrollToPosition(currentPos);
                    }
                });
            }
        }
    }
}