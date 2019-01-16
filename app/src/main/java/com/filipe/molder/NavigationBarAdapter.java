package com.filipe.molder;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class NavigationBarAdapter extends RecyclerView.Adapter {

    private List<File> mDirsList;
    private MainActivity mContext;

    public NavigationBarAdapter(MainActivity context) {
        mDirsList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_bar_item, parent, false);
        return new NavigationBarAdapter.NavBarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NavBarViewHolder)holder).bindView(position);
    }

    public void addDir(File dir) {
        mDirsList.add(dir);
        notifyDataSetChanged();
    }

    public void removeDir(File dir) {
        mDirsList.remove(dir);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDirsList.size();
    }

    private class NavBarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mDirName;
        private File mDir;

        public NavBarViewHolder(View itemView) {
            super(itemView);

            mDirName = itemView.findViewById(R.id.dirName);

            itemView.setOnClickListener(this);
        }

        public void bindView(final int position) {
            mDir = mDirsList.get(position);

            mDirName.setText(mDir.getName());
        }

        @Override
        public void onClick(View view) {
            mContext.navBarOnClick(mDir);
        }
    }
}
