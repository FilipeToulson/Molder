package com.filipe.molder.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filipe.molder.R;
import com.filipe.molder.interfaces.NavBarOnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NavigationBarAdapter extends RecyclerView.Adapter {

    private List<File> mDirsList;
    private NavBarOnClickListener mNavBarOnClickListener;

    public NavigationBarAdapter(NavBarOnClickListener navBarOnClickListener) {
        mDirsList = new ArrayList<>();
        mNavBarOnClickListener = navBarOnClickListener;
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

    public void removeDir(int index) {
        mDirsList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDirsList.size();
    }

    public File getCurrentDir() {
        int currentDirIndex = mDirsList.size() - 1;
        return mDirsList.get(currentDirIndex);
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
            mNavBarOnClickListener.navBarOnClick(mDir);
        }
    }
}
