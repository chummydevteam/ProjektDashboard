package projekt.dashboard.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mutualmobile.cardstack.CardStackLayout;
import com.mutualmobile.cardstack.utils.Units;

import butterknife.ButterKnife;
import projekt.dashboard.R;
import projekt.dashboard.fragments.base.BasePageFragment;
import projekt.dashboard.util.CardStackPrefs;
import projekt.dashboard.util.CreativeMode;

/**
 * @author Nicholas Chum (nicholaschum)
 */
public class CreatorFragment extends BasePageFragment {

    public SharedPreferences prefs;
    public CardStackLayout mCardStackLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup inflation = (ViewGroup) inflater.inflate(
                R.layout.fragment_creator, container, false);

        mCardStackLayout = (CardStackLayout) inflation.findViewById(R.id.creatorCardStack);
        if (mCardStackLayout != null) {
            mCardStackLayout.setShowInitAnimation(CardStackPrefs.isShowInitAnimationEnabled());

            mCardStackLayout.setParallaxEnabled(CardStackPrefs.isParallaxEnabled());
            mCardStackLayout.setParallaxScale(CardStackPrefs.getParallaxScale());

            mCardStackLayout.setCardGap(Units.dpToPx(getActivity(),
                    CardStackPrefs.getCardGap(getActivity())));
            mCardStackLayout.setCardGapBottom(Units.dpToPx(getActivity(),
                    CardStackPrefs.getCardGapBottom(getActivity())));

            mCardStackLayout.setAdapter(new CreativeMode(getActivity()));
        } else {
            Log.e("MainActivity", "Unable to locate cardStack...");
        }

        return inflation;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public int getTitle() {
        return R.string.home;
    }
}
