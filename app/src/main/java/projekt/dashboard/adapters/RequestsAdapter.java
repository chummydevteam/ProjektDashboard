package projekt.dashboard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter;
import com.afollestad.iconrequest.App;
import com.afollestad.materialdialogs.util.DialogUtils;
import projekt.dashboard.R;
import projekt.dashboard.config.Config;
import projekt.dashboard.config.IConfig;
import projekt.dashboard.util.RequestLimiter;
import projekt.dashboard.util.TintUtils;
import projekt.dashboard.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * @author Aidan Follestad (afollestad)
 */
public class RequestsAdapter extends DragSelectRecyclerViewAdapter<RequestsAdapter.RequestVH> {

    public interface SelectionChangedListener {
        void onClick(int index, boolean longClick);
    }

    private boolean mAllowRequest;
    private final int mMax;
    private ArrayList<App> mApps;
    private final SelectionChangedListener mListener;

    public RequestsAdapter(Context context, SelectionChangedListener listener) {
        final IConfig config = Config.get();
        mAllowRequest = !RequestLimiter.needed(context) || RequestLimiter.get(context).allow();
        mMax = config.iconRequestMaxCount();
        mListener = listener;
    }

    public void setApps(ArrayList<App> apps) {
        mApps = apps;
        notifyDataSetChanged();
    }

    public void invalidateAllowRequest(Context context) {
        mAllowRequest = RequestLimiter.get(context).allow();
        notifyItemChanged(0);
    }

    @Override
    protected boolean isIndexSelectable(int index) {
        return mAllowRequest && index > 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 1;
        return 0;
    }

    @Override
    public RequestVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 1 ? R.layout.list_item_header :
                        R.layout.list_item_iconrequest, parent, false);
        return new RequestVH(view);
    }

    @Override
    public void onBindViewHolder(RequestVH holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == 0) {
            final Context c = holder.itemView.getContext();
            if (!mAllowRequest) {
                final String msg = c.getString(R.string.request_limited,
                        RequestLimiter.get(c).remainingIntervalString());
                holder.title.setText(msg);
            } else if (mMax > -1) {
                holder.title.setText(c.getResources().getString(R.string.tap_to_select_app_withmax, mMax));
            } else {
                holder.title.setText(R.string.tap_to_select_app);
            }
            final int bgColor = DialogUtils.resolveColor(holder.itemView.getContext(), R.attr.window_background_cards);
            final int titleColor = TintUtils.isColorLight(bgColor) ? Color.BLACK : Color.WHITE;
            holder.title.setTextColor(TintUtils.adjustAlpha(titleColor, 0.5f));
            return;
        }

        final App app = mApps.get(position - 1);
        app.loadIcon(holder.image);
        holder.title.setText(app.getName());

        if (holder.card != null) {
            holder.card.setForeground(Utils.createCardSelector(holder.itemView.getContext()));
            holder.card.setActivated(isIndexSelected(position));
        }
    }

    @Override
    public int getItemCount() {
        return mApps != null ? mApps.size() + 1 : 0;
    }

    public class RequestVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final CardView card;
        final TextView title;
        final ImageView image;

        public RequestVH(View itemView) {
            super(itemView);
            card = ButterKnife.findById(itemView, R.id.card);
            title = ButterKnife.findById(itemView, R.id.title);
            image = ButterKnife.findById(itemView, R.id.image);
            if (card != null) {
                card.setOnClickListener(this);
                card.setOnLongClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onClick(getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener != null)
                mListener.onClick(getAdapterPosition(), true);
            return false;
        }
    }
}