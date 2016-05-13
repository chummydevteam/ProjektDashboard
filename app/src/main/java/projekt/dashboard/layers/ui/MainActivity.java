package projekt.dashboard.layers.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import projekt.dashboard.layers.util.LayersFunc;
import projekt.dashboard.layers.util.PagesBuilder;
import projekt.dashboard.layers.util.WallpaperUtils;
import projekt.dashboard.layers.views.DisableableViewPager;

import static projekt.dashboard.layers.fragments.WallpapersFragment.RQ_CROPANDSETWALLPAPER;
import static projekt.dashboard.layers.fragments.WallpapersFragment.RQ_VIEWWALLPAPER;
import static projekt.dashboard.layers.viewer.ViewerActivity.STATE_CURRENT_POSITION;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends BaseDonateActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public RecyclerView mRecyclerView;
    public SharedPreferences prefs;

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

        new LayersFunc(this).DownloadFirstResources(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ButterKnife.bind(this);

        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);

        setupPages();
        setupPager();
        setupTabs();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


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

    private void setupPages() {
        mPages = new PagesBuilder(6);
        mPages.add(new PagesBuilder.Page(R.id.home_fragment, R.drawable.tab_home,
                R.string.home_tab_one, new HomeFragment()));
        if (Shell.SU.available()) {
            if (new LayersFunc(this).checkThemeMainSupported(this)) {
                mPages.add(new PagesBuilder.Page(R.id.color_changer_fragment, R.drawable.tab_palette,
                        R.string.home_tab_two, new ColorChangerFragment()));
            }
        }
        if (Shell.SU.available()) {
            if (LayersFunc.themesystemui!="Nill") {
                Log.e("Adding Fragment","Framework found");
                if (LayersFunc.themeframework!="Nill") {
                    Log.e("Adding Fragment","SystemUI found");
                    mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable.tab_swapper,
                            R.string.home_tab_three, new HeaderSwapperFragment()));
                    Log.e("Adding Fragment","added");
                }
                mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable.tab_header_import,
                        R.string.home_tab_four, new HeaderImportFragment()));
            }
        }
        if (Shell.SU.available()) {
            mPages.add(new PagesBuilder.Page(R.id.theme_utilities_fragment, R.drawable.tab_creator,
                    R.string.home_tab_five, new ThemeUtilitiesFragment()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.share){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Check out Dashboard by Chummy Development Team !\n\nDownload it here!: " + "https://play.google.com/store/apps/details?id=com.chummy.jezebel.material.dark";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share :-"));
        }else if(id==R.id.contact){
            StringBuilder emailBuilder = new StringBuilder();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "ebizeraditya@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dashboard.Layers Talk");
            emailBuilder.append("\n \n \nOS Version: " + System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")");
            emailBuilder.append("\nOS API Level: " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ") " + "[" + Build.ID + "]");
            emailBuilder.append("\nDevice: " + Build.DEVICE);
            emailBuilder.append("\nManufacturer: " + Build.MANUFACTURER);
            emailBuilder.append("\nModel (and Product): " + Build.MODEL + " (" + Build.PRODUCT + ")");
            PackageInfo appInfo = null;
            try {
                appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            emailBuilder.append("\nApp Version Name: " + appInfo.versionName);
            emailBuilder.append("\nApp Version Code: " + appInfo.versionCode);

            intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
            startActivity(Intent.createChooser(intent, "Lets Talk Using :-"));
        }
        return true;
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