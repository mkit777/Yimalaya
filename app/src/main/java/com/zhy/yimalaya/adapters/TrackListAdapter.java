package com.zhy.yimalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

    private List<Track> mTrackList;


    private OnItemClickListener mOnItemClickListener = null;

    public void setTrackList(List<Track> trackList) {
        mTrackList = trackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mTrackList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrackList == null ? 0 : mTrackList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class InnerHolder extends RecyclerView.ViewHolder {

        private TextView mTackOrder;
        private TextView mTrackTitle;
        private TextView mTrackPlayCount;
        private TextView mTrackCreatedTime;
        private TextView mTrackDuration;

        private DateTimeFormatter mDateTimeFormatter;


        InnerHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
            setUpEvent();
            mDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        private void setUpEvent() {
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(getLayoutPosition(), mTrackList.get(getLayoutPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClicked(getLayoutPosition(), mTrackList.get(getLayoutPosition()));
                }
                return true;
            });
        }

        private void initView(@NonNull View itemView) {
            mTackOrder = itemView.findViewById(R.id.track_number);
            mTrackTitle = itemView.findViewById(R.id.track_title);
            mTrackPlayCount = itemView.findViewById(R.id.track_play_count);
            mTrackCreatedTime = itemView.findViewById(R.id.track_crated_time);
            mTrackDuration = itemView.findViewById(R.id.track_duration);
        }

        void setData(Track track) {
            mTackOrder.setText(String.valueOf(getLayoutPosition() + 1));
            mTackOrder.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            mTrackTitle.setText(track.getTrackTitle());
            mTrackCreatedTime.setText(timeFromLong2String(track.getCreatedAt()));
            mTrackPlayCount.setText(String.valueOf(track.getPlayCount()));
            mTrackDuration.setText(formatDuration(track.getDuration()));
        }

        String timeFromLong2String(long unixTimestamp) {
            Instant instant = Instant.ofEpochMilli(unixTimestamp);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            return mDateTimeFormatter.format(dateTime);
        }

        String formatDuration(int duration) {
            LocalTime localTime = LocalTime.ofSecondOfDay(duration);
            return localTime.format(FORMATTER);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int index, Track track);

        default void onItemLongClicked(int index, Track track) {
        }
    }
}
