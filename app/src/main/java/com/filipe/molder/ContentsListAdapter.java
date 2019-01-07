package com.filipe.molder;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

    private class ContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mName;
        private TextView mFooter;
        private ImageView mContentImageView;
        private Content mContent;

        public ContentsViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.name);
            mFooter = itemView.findViewById(R.id.footer);
            mContentImageView = itemView.findViewById(R.id.contentImageView);

            itemView.setOnClickListener(this);
        }

        public void bindView(final int position) {
            mContent = mContentsList.get(position);

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
                    mContentImageView.setImageResource(R.drawable.placeholder);
                }
            });
        }

        private void bindSong() {
            MetaData metaData = mContent.getMetaData();

            /*
             * Song name and artist name are set separately so that the longer running
             * tasks of getting the album art path, decoding the file to get the
             * bitmap, and then setting the image don't cause the setting of the song
             * name and artist name to be done later than it could have been done.
             */
            final String songName = metaData.getSongName();
            final String artistName = metaData.getArtistName();

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mName.setText(songName);
                    mFooter.setText(artistName);
                }
            });

            final String albumArtPath = metaData.getAlbumArtPath(mContext);
            final Bitmap bitmap = BitmapFactory.decodeFile(albumArtPath);

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        Glide.with(mContext).load(bitmap).into(mContentImageView);
                    } else {
                        Glide.with(mContext).load(R.drawable.placeholder).into(mContentImageView);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mContent.getFile().isDirectory()) {
                FileController.moveToDirectory(mContent, true);
            } else {
                Log.d("CLA", "Song Clicked");
            }
        }
    }
}
