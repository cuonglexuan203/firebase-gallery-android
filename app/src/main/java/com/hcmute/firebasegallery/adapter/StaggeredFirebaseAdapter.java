package com.hcmute.firebasegallery.adapter;


import android.content.Context;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hcmute.firebasegallery.model.DataClass;
import com.hcmute.firebasegallery.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class StaggeredFirebaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private ArrayList<DataClass> dataList;
    private Context context;
    private boolean isLoading = false;

    public StaggeredFirebaseAdapter(Context context, ArrayList<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.staggere_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            DataClass data = dataList.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.bind(data);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingViewHolder.progressBar.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + (isLoading ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position < dataList.size() ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView staggeredImages;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            staggeredImages = itemView.findViewById(R.id.staggeredImages);
        }
        public void bind(DataClass data) {
            Glide.with(itemView.getContext())
                    .load(data.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(staggeredImages);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar_cyclic);
        }
    }
}
