package com.musicplayer.yorai.musicplayerapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicplayer.yorai.musicplayerapplication.Model.Song;
import com.musicplayer.yorai.musicplayerapplication.R;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Song> mSongs;
    private OnItemClickListener mOnItemClickListener;

    public SongListAdapter(Context mContext, ArrayList<Song> mSongs) {
        this.mContext = mContext;
        this.mSongs = mSongs;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        RecyclerView.ViewHolder viewHolder = new SongItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SongItem) {
            SongItem viewHolder = (SongItem) holder;

            final Song currSong = mSongs.get(position);
            viewHolder.songView.setText(currSong.getTitle());
            viewHolder.artistView.setText(currSong.getArtist());
            viewHolder.albumImageView.setImageBitmap(currSong.getAlbumImage(mContext));
//            byte[] albumImage = currSong.getAlbumImage();
//            if (albumImage != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0, albumImage.length);
//                viewHolder.albumImageView.setImageBitmap(bitmap);
//            }
//            else{
//                viewHolder.albumImageView.setImageResource(R.drawable.song_cover_null);
//            }

            viewHolder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, mSongs.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public Song getItem(int position) {
        return mSongs.get(position);
    }

    public class SongItem extends RecyclerView.ViewHolder{
        protected View layoutParent;
        protected TextView songView;
        protected TextView artistView;
        protected ImageView albumImageView;

        protected SongItem(View itemView) {
            super(itemView);
            layoutParent = (View) itemView.findViewById(R.id.layout_parent);
            songView = (TextView) itemView.findViewById(R.id.title);
            artistView = (TextView) itemView.findViewById(R.id.artist);
            albumImageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Song obj, int pos);
    }

    public void clearResults() {
        mSongs.clear();
    }
}
