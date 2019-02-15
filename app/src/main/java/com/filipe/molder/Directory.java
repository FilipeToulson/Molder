package com.filipe.molder;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory implements Content {

    private File mFile;
    private Content mParentDir;
    private List<Content> mFiles;
    private boolean mIsSelected;

    public Directory(File file, Content parentDir) {
        mFile = file;
        mParentDir = parentDir;
        mFiles = new ArrayList<>();
        mIsSelected = false;
    }

    @Override
    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public void addFile(Content file) {
        mFiles.add(file);
    }

    @Override
    public File getFile() {
        return mFile;
    }

    @Override
    public List<Content> getFiles() {
        return mFiles;
    }

    @Override
    public int getNumberOfItems() {
        return mFiles.size();
    }

    @Override
    public Content getParentDir() {
        return mParentDir;
    }

    @Override
    public MetaData getMetaData() {
        //This method is not to be used by this class
        return null;
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }
}
