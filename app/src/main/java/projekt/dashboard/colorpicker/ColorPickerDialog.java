package projekt.dashboard.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import projekt.dashboard.R;

public class ColorPickerDialog extends Dialog implements ColorPickerView.OnColorChangedListener,
        View.OnClickListener {
    private ColorPickerView mColorPicker;
    private ColorPickerPanelView mOldColor;
    private ColorPickerPanelView mNewColor;
    private EditText mHexVal;
    private boolean mHexInternalTextChange;
    private boolean mHexValueEnabled = false;
    private ColorStateList mHexDefaultTextColor;
    private OnColorChangedListener mListener;

    public ColorPickerDialog(Context context, int initialColor) {
        super(context);
        init(initialColor);
    }

    private void init(int color) {
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setUp(color);
    }

    private void setUp(int color) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.colorpicker, null);
        setContentView(layout);
        mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
        mOldColor = (ColorPickerPanelView) layout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPickerPanelView) layout.findViewById(R.id.new_color_panel);
        mHexVal = (EditText) layout.findViewById(R.id.hex_val);
        mHexDefaultTextColor = mHexVal.getTextColors();
        mHexVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mHexValueEnabled) {
                    if (mHexInternalTextChange) return;

                    if (s.length() > 5 || s.length() < 10) {
                        try {
                            int c = ColorPickerPreference.convertToColorInt(s.toString());
                            mHexInternalTextChange = true;
                            mColorPicker.setColor(c, true);
                            mHexInternalTextChange = false;
                            mHexVal.setTextColor(mHexDefaultTextColor);
                        } catch (NumberFormatException e) {
                            mHexVal.setTextColor(Color.RED);
                        }
                    } else
                        mHexVal.setTextColor(Color.RED);
                }
            }
        });
        setHexValueEnabled(true);
        ((LinearLayout) mOldColor.getParent()).setPadding(
                Math.round(mColorPicker.getDrawingOffset()),
                0,
                Math.round(mColorPicker.getDrawingOffset()),
                0
        );
        mOldColor.setOnClickListener(this);
        mNewColor.setOnClickListener(this);
        mColorPicker.setOnColorChangedListener(this);
        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);
    }

    @Override
    public void onColorChanged(int color) {
        mNewColor.setColor(color);
        if (mHexValueEnabled)
            updateHexValue(color);
    }

    public boolean getHexValueEnabled() {
        return mHexValueEnabled;
    }

    public void setHexValueEnabled(boolean enable) {
        mHexValueEnabled = enable;
        if (enable) {
            mHexVal.setVisibility(View.VISIBLE);
            updateHexLengthFilter();
            updateHexValue(getColor());
        } else
            mHexVal.setVisibility(View.GONE);
    }

    private void updateHexLengthFilter() {
        if (getAlphaSliderVisible())
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        else
            mHexVal.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateHexValue(int color) {
        if (mHexInternalTextChange) return;
        mHexInternalTextChange = true;
        if (getAlphaSliderVisible())
            mHexVal.setText(ColorPickerPreference.convertToARGB(color));
        else
            mHexVal.setText(ColorPickerPreference.convertToRGB(color));
        mHexInternalTextChange = false;
    }

    public boolean getAlphaSliderVisible() {
        return mColorPicker.getAlphaSliderVisible();
    }

    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
        if (mHexValueEnabled) {
            updateHexLengthFilter();
            updateHexValue(getColor());
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mListener = listener;
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_color_panel) {
            if (mListener != null) {
                mListener.onColorChanged(mNewColor.getColor());
            }
        }
        dismiss();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("old_color", mOldColor.getColor());
        state.putInt("new_color", mNewColor.getColor());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOldColor.setColor(savedInstanceState.getInt("old_color"));
        mColorPicker.setColor(savedInstanceState.getInt("new_color"), true);
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }
}