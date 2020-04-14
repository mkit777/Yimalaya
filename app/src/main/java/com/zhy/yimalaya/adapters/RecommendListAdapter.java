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

import java.util.List;


public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mDataList;


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
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.updateData(mDataList.get(position));
    }


    public void setData(List<Album> albums) {
        this.mDataList = albums;
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "InnerHolder";
        private ImageView mAlbumCover;
        private TextView mAlbumTitle;
        private TextView mAlbumDescription;
        private TextView mAlbumPlayCount;
        private TextView mAlbumContentSize;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mAlbumCover = itemView.findViewById(R.id.album_cover);
            mAlbumTitle = itemView.findViewById(R.id.album_title);
            mAlbumDescription = itemView.findViewById(R.id.album_description);
            mAlbumPlayCount = itemView.findViewById(R.id.album_play_count);
            mAlbumContentSize = itemView.findViewById(R.id.album_context_size);
        }

        public void updateData(Album album) {
            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(mAlbumCover);
            mAlbumTitle.setText(album.getAlbumTitle());
            mAlbumContentSize.setText(String.valueOf(album.getIncludeTrackCount()));
            mAlbumDescription.setText(album.getAlbumIntro());
            mAlbumPlayCount.setText(String.valueOf(album.getPlayCount()));
        }
    }
}
