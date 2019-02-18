package com.filipe.molder;


import java.io.File;
import java.util.List;

public interface Content {

    void addFile(Content file);
    void setIsSelected(boolean isSelected);
    File getFile();
    void setFile(File file);
    List<Content> getFiles();
    int getNumberOfItems();
    Content getParentDir();
    MetaData getMetaData();
    boolean isSelected();
}
