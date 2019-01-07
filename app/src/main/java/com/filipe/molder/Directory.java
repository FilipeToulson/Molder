package com.filipe.molder;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory implements Content {

    private File mFile;
    private Content mParentDir;
    private List<Content> mFiles;

    public Directory(File file, Content parentDir) {
        mFile = file;
        mParentDir = parentDir;
        mFiles = new ArrayList<Content>();
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
        //This method is to be used by this class
        return null;
    }
}
