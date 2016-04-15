package colorswitcher.chummy.aditya.colorswitcher.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import colorswitcher.chummy.aditya.colorswitcher.MainActivity;
import colorswitcher.chummy.aditya.colorswitcher.R;

/**
 * Created by adity on 4/11/2016.
 */
public class SecondFragment extends Fragment {
    public static ImageView imageView;
    TextView finalise_text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2, container, false);
        imageView = (ImageView) v.findViewById(R.id.accent);
        imageView.setColorFilter(Color.argb(255,255,255,255));
        finalise_text = (TextView) v.findViewById(R.id.finalize_title);;
        Typeface custom = Typeface.createFromAsset(getActivity().getAssets(), "fonts/productsans.ttf");
        finalise_text.setTypeface(custom);
        return v;
    }

    public static void changeColor(int i){
        imageView.setColorFilter(i);
    }

    public static SecondFragment newInstance() {

        return new SecondFragment();

    }
}