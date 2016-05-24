package projekt.dashboard.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import projekt.dashboard.R;
import projekt.dashboard.util.HeaderParser;

/**
 * @author Nicholas Chum (nicholaschum)
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<HeaderParser> headers;
    private Context context;

    public DataAdapter(Context context, ArrayList<HeaderParser> headers) {
        this.context = context;
        this.headers = headers;

    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.textView.setText(headers.get(i).getHeaderPackName());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                context);
        if (!prefs.getBoolean("header_downloader_low_power_mode", true)) {
            Picasso.with(context).load(headers.get(i).getHeaderPackURL()).into(viewHolder
                    .imageView);
        }

    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                    context);

            textView = (TextView) view.findViewById(R.id.headerPackName);
            imageView = (ImageView) view.findViewById(R.id.headerPackPreview);

            if (prefs.getBoolean("header_downloader_low_power_mode", true)) {
                imageView.setVisibility(View.GONE);
            }
        }
    }
}