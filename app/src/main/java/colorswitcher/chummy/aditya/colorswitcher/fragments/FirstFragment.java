package colorswitcher.chummy.aditya.colorswitcher.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import colorswitcher.chummy.aditya.colorswitcher.MainActivity;
import colorswitcher.chummy.aditya.colorswitcher.R;

/**
 * Created by adity on 4/11/2016.
 */
public class FirstFragment extends Fragment {
    ImageButton imageButton;
    public static String color_picked;
    TextView accentcolor;
    TextView title;
    TextView title_pick;
    public static String getcolor() {
        return color_picked;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment1, container, false);
        accentcolor = (TextView) v.findViewById(R.id.accentcolor);
        title_pick = (TextView) v.findViewById(R.id.title_pick);;
        title = (TextView) v.findViewById(R.id.title);;
        Typeface custom = Typeface.createFromAsset(getActivity().getAssets(), "fonts/productsans.ttf");
        title.setTypeface(custom);
        title_pick.setTypeface(custom);
        accentcolor.setTypeface(custom);

        imageButton = (ImageButton) v.findViewById(R.id.preview);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int black = Color.argb(255, 0, 0, 0);
                final ColorPickerDialog cpd = new ColorPickerDialog(getActivity(), black);
                cpd.setAlphaSliderVisible(false);
                cpd.setHexValueEnabled(true);
                cpd.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        color_picked = ColorPickerPreference.convertToARGB(color);
                        imageButton.setColorFilter(color);
                        SecondFragment.changeColor(color);
                        accentcolor.setText(color_picked);
                    }
                });
                cpd.show();
            }
        });
        return v;
    }


    public static FirstFragment newInstance() {

        return new FirstFragment();

    }
}