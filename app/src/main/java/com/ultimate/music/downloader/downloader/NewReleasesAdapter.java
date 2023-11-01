package com.ultimate.music.downloader.downloader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ultimate.music.downloader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewReleasesAdapter extends RecyclerView.Adapter<NewReleasesAdapter.mViewHolder> {

    private ArrayList<Item> mList3;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
       mListener = listener;
    }

    public static class mViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public mViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_cover);
            mTextView = itemView.findViewById(R.id.item_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public NewReleasesAdapter(ArrayList<Item> list) {
        mList3 = list;

    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new mViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Item currentItem = mList3.get(position);

        String imgUrl = currentItem.getImageUrl();
        Picasso.get().load(imgUrl).fit().placeholder(R.drawable.progress_animation).error(R.drawable.not_loading).into(holder.mImageView);

        holder.mTextView.setText(currentItem.getTextCover());
    }

    @Override
    public int getItemCount() {
        return  mList3.size();
    }


}
