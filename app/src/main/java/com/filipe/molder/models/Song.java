package com.filipe.molder.models;


import com.filipe.molder.interfaces.Content;

import java.io.File;
import java.util.List;

public class Song implements Content {

    private File mFile;
    private MetaData mMetaData;
    private boolean mIsSelected;

    public Song(File file, MetaData metaData) {
        mFile = file;
        mMetaData = metaData;
    }

    @Override
    public void addFile(Content file) {
        //This method is not to be used by this class
    }

    @Override
    public void removeFile(Content file) {
        //This method is not to be used by this class
    }

    @Override
    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public File getFile() {
        return mFile;
    }

    @Override
    public void setFile(File file) {
        mFile = file;
    }


    @Override
    public List<Content> getFiles() {
        //This method is not to be used by this class
        return null;
    }

    @Override
    public int getNumberOfItems() {
        //This method is not to be used by this class
        return 0;
    }

    @Override
    public Directory getParentDir() {
        //This method is not to be used by this class
        return null;
    }

    @Override
    public MetaData getMetaData() {
        return mMetaData;
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }
}
