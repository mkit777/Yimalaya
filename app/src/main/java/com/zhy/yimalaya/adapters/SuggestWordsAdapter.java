package com.zhy.yimalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.zhy.yimalaya.R;

import java.util.ArrayList;
import java.util.List;

public class SuggestWordsAdapter extends RecyclerView.Adapter<SuggestWordsAdapter.InnerHolder> {


    private List<QueryResult> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_word, parent, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.updateData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<QueryResult> keywords) {
        mData.clear();
        mData.addAll(keywords);
        notifyDataSetChanged();
    }


    public void setItemClickListener(OnItemClickListener clickListener) {
        mItemClickListener = clickListener;
    }

    class InnerHolder extends RecyclerView.ViewHolder {
        TextView mSuggestWord;

        InnerHolder(@NonNull View itemView) {
            super(itemView);
            mSuggestWord = itemView.findViewById(R.id.suggest_word);
            itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mSuggestWord.getText().toString());
                }
            });
        }

        void updateData(QueryResult queryResult) {
            mSuggestWord.setText(queryResult.getKeyword());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(String keyword);
    }
}
