package projekt.dashboard.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mutualmobile.cardstack.CardStackAdapter;
import com.tramsun.libs.prefcompat.Pref;

import java.io.File;

import projekt.dashboard.R;

public class HeaderCreatorMode extends CardStackAdapter implements
        CompoundButton.OnCheckedChangeListener {
    private static int[] bgColorIds;
    private final LayoutInflater mInflater;
    private final Context mContext;
    public Runnable updateSettingsView;
    public SharedPreferences prefs;

    private Logger log = new Logger(HeaderCreatorMode.class.getSimpleName());

    public HeaderCreatorMode(Activity activity) {
        super(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
        bgColorIds = new int[]{
                R.color.hc_card1_bg, // Framework
                R.color.hc_card2_bg, // Settings
                R.color.hc_card3_bg, // SystemUI
                R.color.hc_card4_bg, // Final Card
                R.color.hc_card5_bg, // Final Card
                R.color.hc_card6_bg, // Final Card
                R.color.hc_card7_bg, // Final Card
                R.color.hc_card8_bg, // Final Card
                R.color.hc_card9_bg, // Final Card
        };
    }

    public void cleanTempFolder() {
        File dir = mContext.getCacheDir();
        deleteRecursive(dir);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public int getCount() {
        return bgColorIds.length;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        log.d("onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], " +
                "isChecked = [" + isChecked + "]");
        Pref.putBoolean(CardStackPrefs.PARALLAX_ENABLED, isChecked);
        Pref.putBoolean(CardStackPrefs.SHOW_INIT_ANIMATION, isChecked);
        updateSettingsView.run();
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View createView(int position, ViewGroup container) {
        if (position == 0) return getAfternoon(container);
        if (position == 1) return getChristmas(container);
        if (position == 2) return getMorning(container);
        if (position == 3) return getNewYearsEve(container);
        if (position == 4) return getNight(container);
        if (position == 5) return getNoon(container);
        if (position == 6) return getSunrise(container);
        if (position == 7) return getSunset(container);
        if (position == 8) return getFinalCard(container);

        CardView root = (CardView) mInflater.inflate(R.layout.card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[position]));
        TextView cardTitle = (TextView) root.findViewById(R.id.card_title);
        cardTitle.setText(mContext.getString(R.string.card_title, position));

        return root;
    }

    private View getAfternoon(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_afternoon_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[0]));

        return root;
    }

    private View getChristmas(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_christmas_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[1]));

        return root;
    }

    private View getMorning(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_morning_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[2]));

        return root;
    }

    private View getNewYearsEve(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_newyearseve_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[3]));

        return root;
    }

    private View getNight(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_night_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[4]));

        return root;
    }

    private View getNoon(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_noon_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[5]));

        return root;
    }

    private View getSunrise(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_sunrise_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[6]));

        return root;
    }

    private View getSunset(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.hc_sunset_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[7]));

        return root;
    }

    private View getFinalCard(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.final_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[8]));

        return root;
    }

}