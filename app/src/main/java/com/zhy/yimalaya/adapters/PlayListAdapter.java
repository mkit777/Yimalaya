package com.zhy.yimalaya.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private static final String TAG = "PlayListAdapter";
    private List<Track> mData = new ArrayList<>();
    private int selectedIndex = 0;

    private ClickListener mClickListener;

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_list, parent, false);
        return new InnerHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        LogUtil.d(TAG, "onBindViewHolder");
        holder.updateData();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }


    private static final int DEFAULT_COLOR = Color.parseColor("#8f8f8f");
    private static final int SELECTED_COLOR = Color.parseColor("#EA5830");

    public class InnerHolder extends RecyclerView.ViewHolder {

        private final TextView mTrackName;
        private final ImageView mIcon;

        InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTrackName = itemView.findViewById(R.id.play_track_title);
            mIcon = itemView.findViewById(R.id.play_item_icon);

            itemView.setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onPlayListItemClicked(getLayoutPosition());
                }
            });
        }

        void updateData() {
            mTrackName.setText(mData.get(getLayoutPosition()).getTrackTitle());
            boolean isSelected = getLayoutPosition() == selectedIndex;
            mTrackName.setTextColor(isSelected ? SELECTED_COLOR : DEFAULT_COLOR);
            mIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        }
    }




    public interface ClickListener {
        void onPlayListItemClicked(int index);
    }
}
