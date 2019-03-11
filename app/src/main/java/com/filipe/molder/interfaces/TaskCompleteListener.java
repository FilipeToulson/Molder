package com.filipe.molder.interfaces;


public interface TaskCompleteListener {
    /*
     * Called when the deleting, copying, moving, and renaming of files
     * has been complete.
     */
    void taskComplete(boolean removeContent, boolean refreshContents);
}
