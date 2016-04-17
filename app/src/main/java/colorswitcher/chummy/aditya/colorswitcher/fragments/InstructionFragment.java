package colorswitcher.chummy.aditya.colorswitcher.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import colorswitcher.chummy.aditya.colorswitcher.R;

/**
 * Created by adity on 4/11/2016.
 */
public class InstructionFragment extends Fragment {
    TextView title_pick;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.instfragment, container, false);
        title_pick = (TextView) v.findViewById(R.id.title_pick);
        Typeface custom = Typeface.createFromAsset(getActivity().getAssets(),"fonts/productsans.ttf");
        title_pick.setTypeface(custom);
        return v;
    }


    public static InstructionFragment newInstance() {

        return new InstructionFragment();

    }
}