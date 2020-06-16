package com.zhy.yimalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhy.yimalaya.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private List<Album> mDataList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;


    /**
     * 设置列表中每一项的布局
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        holder.updateData(mDataList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, mDataList == null ? null : mDataList.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemLongClicked(position, mDataList == null ? null : mDataList.get(position));
                return true;
            }
            return false;
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<Album> albums) {
        this.mDataList.clear();
        this.mDataList.addAll(albums);
        notifyDataSetChanged();
    }


    public void addData(List<Album> albums) {
        this.mDataList.addAll(albums);
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class InnerHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "InnerHolder";
        private ImageView mAlbumCover;
        private TextView mAlbumTitle;
        private TextView mAlbumIntro;
        private TextView mAlbumPlayCount;
        private TextView mAlbumTrackCount;

        InnerHolder(@NonNull View itemView) {
            super(itemView);
            mAlbumCover = itemView.findViewById(R.id.album_cover);
            mAlbumTitle = itemView.findViewById(R.id.album_title);
            mAlbumIntro = itemView.findViewById(R.id.album_intro);
            mAlbumPlayCount = itemView.findViewById(R.id.album_play_count);
            mAlbumTrackCount = itemView.findViewById(R.id.album_track_count);
        }

        void updateData(Album album) {
            if (album.getCoverUrlSmall() != null && !album.getCoverUrlSmall().isEmpty()) {
                Picasso.with(itemView.getContext()).load(album.getCoverUrlSmall()).into(mAlbumCover);
            } else {
                Picasso.with(itemView.getContext()).load(R.mipmap.logo).into(mAlbumCover);
            }
            mAlbumTitle.setText(album.getAlbumTitle());
            mAlbumTrackCount.setText(String.valueOf(album.getIncludeTrackCount()));
            mAlbumIntro.setText(album.getAlbumIntro());
            mAlbumPlayCount.setText(String.valueOf(album.getPlayCount()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Album album);

        default void onItemLongClicked(int position, Album album) {
        }
    }
}
