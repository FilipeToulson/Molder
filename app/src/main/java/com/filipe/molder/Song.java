package com.filipe.molder;


import java.io.File;
import java.util.List;

public class Song implements Content {

    private File mFile;
    private MetaData mMetaData;

    public Song(File file, MetaData metaData) {
        mFile = file;
        mMetaData = metaData;
    }

    @Override
    public void addFile(Content file) {
        //This method is to be used by this class
    }

    @Override
    public File getFile() {
        return mFile;
    }

    @Override
    public List<Content> getFiles() {
        //This method is to be used by this class
        return null;
    }

    @Override
    public int getNumberOfItems() {
        //This method is to be used by this class
        return 0;
    }

    @Override
    public Directory getParentDir() {
        //This method is to be used by this class
        return null;
    }

    @Override
    public MetaData getMetaData() {
        return mMetaData;
    }
}
