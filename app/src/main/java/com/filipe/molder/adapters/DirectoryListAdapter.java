package com.filipe.molder.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.filipe.molder.R;
import com.filipe.molder.activities.DirectorySelectorActivity;

import java.io.File;
import java.util.List;

public class DirectoryListAdapter extends RecyclerView.Adapter{

    private List<File> mDirsList;
    private DirectorySelectorActivity mContext;

    public DirectoryListAdapter(List<File> dirsList, DirectorySelectorActivity context) {
        mDirsList = dirsList;
        mContext = context;
    }

    public void setDirectoryList(List<File> dirsList) {
        mDirsList = dirsList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.directory_list_item, parent, false);
        return new DirectoryListAdapter.DirectoryLisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DirectoryListAdapter.DirectoryLisViewHolder)holder).bindView(position);
    }

    public void addDir(File dir) {
        mDirsList.add(dir);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDirsList.size();
    }

    private class DirectoryLisViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mIcon;
        private TextView mName;
        private File mDir;

        public DirectoryLisViewHolder(View itemView) {
            super(itemView);

            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }

        public void bindView(final int position) {
            mDir = mDirsList.get(position);

            if(mDir == null) {
                mName.setText("Create Folder");
                Glide.with(mContext).load(R.drawable.placeholder_new_dir_icon).into(mIcon);
            } else {
                mName.setText(mDir.getName());
                Glide.with(mContext).load(R.drawable.placeholder).into(mIcon);
            }
        }

        @Override
        public void onClick(View view) {
            if(mDir == null) {
                mContext.createFolderOnClick();
            } else {
                mContext.dirOnClick(mDir);
            }
        }
    }
}
