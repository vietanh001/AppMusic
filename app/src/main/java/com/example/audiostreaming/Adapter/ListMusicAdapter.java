package com.example.audiostreaming.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiostreaming.Model.Music;
import com.example.audiostreaming.R;

import java.util.ArrayList;

public class ListMusicAdapter extends RecyclerView.Adapter<ListMusicAdapter.ListMusicHolder>{

    private Context context;
    private ArrayList<Music> mList;
    private ItemListener itemListener;

    public ListMusicAdapter(Context context, ArrayList<Music> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void setmList(ArrayList<Music> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public Music getMusic(int position){
        return mList.get(position);
    }

    public void setItemListener(ItemListener itemListener){
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ListMusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new ListMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMusicHolder holder, int position) {
        Music music = mList.get(position);

        if(!music.getVideo().equals("")) {
            Glide.with(context)
                    .load(mList.get(position).getImage())
                    .into(holder.ivImage);
        }

        else{
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, Uri.parse(music.getImage()));
            byte[] bgp_cover = mediaMetadataRetriever.getEmbeddedPicture();
            if(bgp_cover != null){
                holder.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(bgp_cover,0, bgp_cover.length));
            } else {
                holder.ivImage.setImageResource(R.drawable.music);
            }
        }

        holder.tvName.setText(music.getName());
        holder.tvAuthor.setText(music.getAuthor());
        holder.tvTime.setText(music.getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ListMusicHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivImage;
        private TextView tvName, tvAuthor, tvTime;

        public ListMusicHolder(@NonNull View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivImage);
            tvName = view.findViewById(R.id.tvName);
            tvAuthor = view.findViewById(R.id.tvAuthor);
            tvTime = view.findViewById(R.id.tvTime);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemListener != null){
                itemListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemListener{
        void onItemClick(View view, int position);
    }
}
