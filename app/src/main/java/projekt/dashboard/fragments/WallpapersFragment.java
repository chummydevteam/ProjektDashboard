package projekt.dashboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;

import java.net.SocketTimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.adapters.WallpaperAdapter;
import projekt.dashboard.config.Config;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.util.WallpaperUtils;
import projekt.dashboard.viewer.ViewerActivity;

import static projekt.dashboard.viewer.ViewerActivity.STATE_CURRENT_POSITION;

/**
 * @author Aidan Follestad (afollestad)
 */
public class WallpapersFragment extends BasePageFragment implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final int RQ_CROPANDSETWALLPAPER = 8585;
    public static final int RQ_VIEWWALLPAPER = 2001;
    private static Toast mToast;
    @Bind(android.R.id.list)
    RecyclerView mRecyclerView;
    @Bind(android.R.id.empty)
    TextView mEmpty;
    @Bind(android.R.id.progress)
    View mProgress;
    WallpaperUtils.WallpapersHolder mWallpapers;
    private WallpaperAdapter mAdapter;
    private String mQueryText;
    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            mAdapter.filter(mQueryText);
            setListShown(true);
        }
    };

    public WallpapersFragment() {
    }

    public static void showToast(Context context, @StringRes int message) {
        showToast(context, context.getString(message));
    }

    public static void showToast(Context context, String message) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public int getTitle() {
        return R.string.home_tab_six;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("wallpapers", mWallpapers);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflation = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getActivity());

        final Spinner wallpaperSourcePicker = (Spinner) inflation.findViewById(R.id.sourcePicker);
        final String[] wallpaperSourcePickerURLs = getResources().getStringArray(R.array.wallpaper_sources_urls);
        ArrayAdapter<String> spinnerCountShoesArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.wallpaper_sources));
        wallpaperSourcePicker.setAdapter(spinnerCountShoesArrayAdapter);
        wallpaperSourcePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                prefs.edit().putString("selected_wallpaper_source",
                        wallpaperSourcePickerURLs[wallpaperSourcePicker.
                                getSelectedItemPosition()]).apply();
                prefs.edit().putInt("selected_wallpaper_source_position",
                        wallpaperSourcePicker.getSelectedItemPosition()).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        int mapTypeString = prefs.getInt("selected_wallpaper_source_position", 0);
        wallpaperSourcePicker.setSelection(mapTypeString);

        ImageButton restartActivity = (ImageButton) inflation.findViewById(R.id.restart);
        restartActivity.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                mWallpapers = null;
                load(false);
            }
        }));

        return inflation;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    void openViewer(View view, int index) {
        ImageView iv = (ImageView) view.findViewById(R.id.image);

        final Intent intent = new Intent(getActivity(), ViewerActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("wallpapers", mWallpapers);
        extras.putInt(STATE_CURRENT_POSITION, index);
        intent.putExtras(extras);

        final String transName = "view_" + index;
        ViewCompat.setTransitionName(iv, transName);
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), iv, transName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Somehow this works (setting status bar color in both MainActivity and here)
            //to avoid image glitching through on when ViewActivity is first created.
            getActivity().getWindow().setStatusBarColor(
                    DialogUtils.resolveColor(getActivity(), R.attr.colorPrimaryDark));
            View statusBar = getActivity().getWindow().getDecorView().findViewById(android.R.id.statusBarBackground);
            if (statusBar != null) {
                statusBar.post(new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.startActivityForResult(getActivity(), intent, RQ_VIEWWALLPAPER, options.toBundle());
                    }
                });
                return;
            }
        }

        ActivityCompat.startActivityForResult(getActivity(), intent, RQ_VIEWWALLPAPER, options.toBundle());
    }

    private void showOptions(final int imageIndex) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.wallpaper)
                .items(R.array.wallpaper_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                        final WallpaperUtils.Wallpaper wallpaper = mWallpapers.get(imageIndex);
                        WallpaperUtils.download(getActivity(), wallpaper, i == 0);
                    }
                }).show();
    }

    private void setListShown(boolean shown) {
        final View v = getView();
        if (v != null) {
            mRecyclerView.setVisibility(shown ?
                    View.VISIBLE : View.GONE);
            mProgress.setVisibility(shown ?
                    View.GONE : View.VISIBLE);
            mEmpty.setVisibility(shown && mAdapter.getItemCount() == 0 ?
                    View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new WallpaperAdapter(new WallpaperAdapter.ClickListener() {
            @Override
            public boolean onClick(View view, int index, boolean longPress) {
                if (longPress) {
                    showOptions(index);
                    return true;
                } else {
                    openViewer(view, index);
                    return false;
                }
            }
        });
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                Config.get().gridWidthWallpaper(), StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null)
            mWallpapers = (WallpaperUtils.WallpapersHolder) savedInstanceState.getSerializable("wallpapers");
        if (getActivity() != null) load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        WallpaperUtils.resetOptionCache(true);
    }

    public void load() {
        load(false);
    }

    private void load(boolean allowCached) {
        if (allowCached && mWallpapers != null) {
            mAdapter.set(mWallpapers);
            setListShown(true);
            return;
        }
        setListShown(false);
        mAdapter.clear();
        Bridge.config().logging(true);
        WallpaperUtils.getAll(getActivity(), allowCached, new WallpaperUtils.WallpapersCallback() {
            @Override
            public void onRetrievedWallpapers(WallpaperUtils.WallpapersHolder wallpapers, Exception error, boolean cancelled) {
                if (error != null) {
                    if (error instanceof BridgeException) {
                        BridgeException e = (BridgeException) error;
                        if (e.reason() == BridgeException.REASON_REQUEST_FAILED)
                            mEmpty.setText(R.string.unable_to_contact_server);
                        else if (e.reason() == BridgeException.REASON_REQUEST_TIMEOUT ||
                                (e.underlyingException() != null && e.underlyingException() instanceof SocketTimeoutException))
                            mEmpty.setText(R.string.unable_to_contact_server);
                        else mEmpty.setText(e.getMessage());
                    } else {
                        mEmpty.setText(error.getMessage());
                    }
                } else {
                    mEmpty.setText(cancelled ? R.string.request_cancelled : R.string.intro_wallpapers);
                    mWallpapers = wallpapers;
                    mAdapter.set(mWallpapers);
                }
                setListShown(true);
            }
        });
    }

    // Search

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            if (mAdapter != null)
                WallpaperUtils.saveDb(getActivity(), mAdapter.getWallpapers());
            if (getActivity().isFinishing()) {
                Bridge.cancelAll()
                        .tag(WallpapersFragment.class.getName())
                        .commit();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQueryText = newText;
        mRecyclerView.postDelayed(searchRunnable, 400);
        return false;
    }

    @Override
    public boolean onClose() {
        mRecyclerView.removeCallbacks(searchRunnable);
        mQueryText = null;
        mAdapter.filter(null);
        setListShown(true);
        return false;
    }
}