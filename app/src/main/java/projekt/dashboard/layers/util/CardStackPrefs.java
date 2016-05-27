package projekt.dashboard.layers.util;

import android.content.Context;

import com.mutualmobile.cardstack.CardStackLayout;
import com.tramsun.libs.prefcompat.Pref;

import projekt.dashboard.layers.R;

public class CardStackPrefs {

    public static final String SHOW_INIT_ANIMATION = "showInitAnimation";
    public static final String PARALLAX_ENABLED = "parallaxEnabled";
    public static final String PARALLAX_SCALE = "parallaxScale";
    public static final String CARD_GAP = "cardGap";
    public static final String CARD_GAP_BOTTOM = "cardGapBottom";

    public static boolean isShowInitAnimationEnabled() {
        return Pref.getBoolean(SHOW_INIT_ANIMATION, CardStackLayout.SHOW_INIT_ANIMATION_DEFAULT);
    }

    public static boolean isParallaxEnabled() {
        return true;
    }

    public static int getParallaxScale() {
        return -10;
    }

    public static int getCardGap(Context context) {
        int cardGapDimenInDp = (int) (context.getResources().getDimension(R.dimen.card_gap) /
                context.getResources().getDisplayMetrics().density);
        return Pref.getInt(CARD_GAP, cardGapDimenInDp);
    }

    public static int getCardGapBottom(Context context) {
        int cardGapBottomDimenInDp = (int) (context.getResources().getDimension(R.dimen
                .card_gap_bottom) / context.getResources().getDisplayMetrics().density);
        return Pref.getInt(CARD_GAP_BOTTOM, cardGapBottomDimenInDp);
    }
}
