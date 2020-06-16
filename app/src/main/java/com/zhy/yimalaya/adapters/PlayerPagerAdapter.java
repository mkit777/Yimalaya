package com.zhy.yimalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerPagerAdapter extends PagerAdapter {


    private static final String TAG = "PlayerPagerAdapter";

    private List<Track> mData = new ArrayList<>();


    public List<Track> getData() {
        return mData;
    }

    public void updateData(List<Track> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_player_pager, container, false);
        ImageView album = view.findViewById(R.id.play_track_album);
        Picasso.with(container.getContext()).load(mData.get(position).getAlbum().getCoverUrlLarge()).into(album);
        LogUtil.d(TAG, "instantiateItem ==>pos=" + position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((View) object));
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
