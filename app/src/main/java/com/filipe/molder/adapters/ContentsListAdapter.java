package com.filipe.molder.adapters;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.filipe.molder.R;
import com.filipe.molder.activities.MainActivity;
import com.filipe.molder.interfaces.Content;
import com.filipe.molder.models.MetaData;
import com.filipe.molder.utils.MetaDataUtils;

import org.jaudiotagger.tag.images.Artwork;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContentsListAdapter extends RecyclerView.Adapter {

    private List<Content> mContentsList;
    private ExecutorService mThreadPool;
    private MainActivity mContext;

    public ContentsListAdapter(MainActivity context) {
        mThreadPool = Executors.newCachedThreadPool();
        mContext = context;
    }

    public void setContentsList(List<Content> contentsList) {
        mContentsList = contentsList;
        refreshList();
    }

    public void removeContent(List<Content> contents) {
        mContentsList.removeAll(contents);
    }

    public void refreshList() {
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_list_item, parent, false);
        return new ContentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ContentsViewHolder)holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mContentsList.size();
    }

    private class ContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnLongClickListener {

        private View mItemView;
        private TextView mName;
        private TextView mFooter;
        private ImageView mContentImageView;
        private Content mContent;

        public ContentsViewHolder(View itemView) {
            super(itemView);

            mItemView = itemView;

            mName = itemView.findViewById(R.id.name);
            mFooter = itemView.findViewById(R.id.footer);
            mContentImageView = itemView.findViewById(R.id.contentImageView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindView(final int position) {
            mContent = mContentsList.get(position);

            if(mContent.isSelected()) {
                mItemView.setBackgroundColor(Color.LTGRAY);
            } else {
                mItemView.setBackgroundColor(Color.TRANSPARENT);
            }

            mThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    if(mContent.getFile().isDirectory()) {
                        bindDirectory();
                    } else {
                        bindSong();
                    }
                }
            });
        }

        private void bindDirectory() {
            final String fileName = mContent.getFile().getName();
            final String numberOfItems = mContent.getNumberOfItems() + " Items";

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mName.setText(fileName);
                    mFooter.setText(numberOfItems);
                    Glide.with(mContext).load(R.drawable.placeholder).into(mContentImageView);
                }
            });
        }

        private void bindSong() {
            MetaData metaData = mContent.getMetaData();

            if(!metaData.isMetaDataSet()) {
                MetaDataUtils.generateSongMetaData(mContent, metaData);
            }

            final String songName = metaData.getSongName();
            final String artistName =  metaData.getArtistName();

            /*
             * Song name and artist name are set separately from album art so that the longer
             * running tasks of decoding the file to get the bitmap, and then setting the image
             * don't cause the setting of the song name and artist name to be done later than it
             * could have been done.
             */
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mName.setText(songName);
                    mFooter.setText(artistName);
                }
            });

            Artwork albumArt = metaData.getAlbumArt();
            Bitmap bitmap = null;
            if(albumArt != null) {
                byte[] data = albumArt.getBinaryData();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }

            final Bitmap finalBitmap = bitmap;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalBitmap != null) {
                        Glide.with(mContext).load(finalBitmap).into(mContentImageView);
                    } else {
                        Glide.with(mContext).load(R.drawable.placeholder).into(mContentImageView);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            mContext.contentOnClick(mContent, view);
        }

        @Override
        public boolean onLongClick(View view) {
            mContext.contentOnLongClick(mContent, view);

            return true;
        }
    }
}
