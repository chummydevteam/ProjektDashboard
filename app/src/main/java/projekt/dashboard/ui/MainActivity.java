package projekt.dashboard.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.bridge.Bridge;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.tramsun.libs.prefcompat.Pref;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.chainfire.libsuperuser.Shell;
import projekt.dashboard.R;
import projekt.dashboard.adapters.MainPagerAdapter;
import projekt.dashboard.config.Config;
import projekt.dashboard.fragments.ColorChangerFragment;
import projekt.dashboard.fragments.CreatorFragment;
import projekt.dashboard.fragments.HeaderImportFragment;
import projekt.dashboard.fragments.HeaderSwapperFragment;
import projekt.dashboard.fragments.HomeFragment;
import projekt.dashboard.fragments.ThemeUtilitiesFragment;
import projekt.dashboard.fragments.WallpapersFragment;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.ui.base.BaseDonateActivity;
import projekt.dashboard.util.DrawableXmlParser;
import projekt.dashboard.util.PagesBuilder;
import projekt.dashboard.util.WallpaperUtils;
import projekt.dashboard.views.DisableableViewPager;

import static projekt.dashboard.fragments.WallpapersFragment.RQ_CROPANDSETWALLPAPER;
import static projekt.dashboard.fragments.WallpapersFragment.RQ_VIEWWALLPAPER;
import static projekt.dashboard.viewer.ViewerActivity.STATE_CURRENT_POSITION;

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
    boolean doubleBackToExitPressedOnce = false;
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

        if (!prefs.getBoolean("extended_actionbar_enabled", false)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mToolbar
                    .getLayoutParams();
            layoutParams.height = 0;
            mToolbar.setLayoutParams(layoutParams);
        }

        setSupportActionBar(mToolbar);

        setupPages();
        setupPager();
        setupTabs();

        Pref.init(this);

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
            if (prefs.getBoolean("color_switcher_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.color_changer_fragment, R.drawable
                        .tab_palette,
                        R.string.home_tab_two, new ColorChangerFragment()));
            }
            if (prefs.getBoolean("advanced_mode_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.theme_utilities_fragment, R.drawable
                        .tab_creator,
                        R.string.home_tab_seven, new CreatorFragment()));
            }
            if (prefs.getBoolean("header_swapper_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable
                        .tab_swapper,
                        R.string.home_tab_three, new HeaderSwapperFragment()));
            }
            if (prefs.getBoolean("header_importer_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.header_swapper_fragment, R.drawable
                        .tab_header_import,
                        R.string.home_tab_four, new HeaderImportFragment()));
            }
            if (prefs.getBoolean("theme_debugging_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.theme_utilities_fragment, R.drawable
                        .tab_rebuild,
                        R.string.home_tab_five, new ThemeUtilitiesFragment()));
            }
        }
        if (isNetworkAvailable()) {
            if (prefs.getBoolean("wallpapers_enabled", true)) {
                mPages.add(new PagesBuilder.Page(R.id.theme_utilities_fragment, R.drawable
                        .tab_wallpapers,
                        R.string.home_tab_six, new WallpapersFragment()));
            }
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
                    .edit().putInt("last_selected_page", mPager.getCurrentItem()).apply();
        }
        if (isFinishing()) {
            Config.deinit();
            Bridge.destroy();
            DrawableXmlParser.cleanup();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.double_click_back), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

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
