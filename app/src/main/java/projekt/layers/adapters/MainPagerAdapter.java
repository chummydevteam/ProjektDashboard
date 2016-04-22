package projekt.layers.adapters;


import android.app.Fragment;
import android.app.FragmentManager;

import projekt.layers.util.PagesBuilder;
import projekt.layers.viewer.FragmentStatePagerAdapter;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final PagesBuilder mPages;

    public MainPagerAdapter(FragmentManager fm, PagesBuilder pages) {
        super(fm);
        mPages = pages;
    }

    @Override
    protected Fragment getItem(int position) {
        return mPages.get(position).fragment;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }
}