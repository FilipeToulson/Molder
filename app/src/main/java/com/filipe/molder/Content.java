package com.filipe.molder;


import java.io.File;
import java.util.List;

public interface Content {

    void addFile(Content file);
    File getFile();
    List<Content> getFiles();
    int getNumberOfItems();
    Content getParentDir();
    MetaData getMetaData();
}
