package com.filipe.molder.utils;


import com.filipe.molder.interfaces.Content;
import com.filipe.molder.models.Directory;
import com.filipe.molder.models.MetaData;
import com.filipe.molder.models.Song;

import java.io.File;
import java.util.Comparator;

/*
 * Compares content to each other.
 *
 * Directories are ordered in alphabetical order in relation to other directories.
 * Songs are ordered in alphabetical order in relation to other songs.
 * Directories are placed first in the list in relation to songs, no matter their name.
 */
public class ContentComparator implements Comparator<Content> {

    private static final int A_GREATER_THAN_B = -1;
    private static final int A_EQUALS_B = 0;
    private static final int B_GREATER_THAN_A = 1;

    @Override
    public int compare(Content contentA, Content contentB) {
        int compareResult = A_EQUALS_B;

        if(contentA instanceof Directory && contentB instanceof Directory) {
            File fileA = contentA.getFile();
            File fileB = contentB.getFile();

            compareResult = fileA.getName().compareTo(fileB.getName());
        } else if (contentA instanceof Song && contentB instanceof Song) {
            MetaData metaDataA = contentA.getMetaData();
            MetaData metaDataB = contentB.getMetaData();

            String songNameA = metaDataA.getSongOrder();
            String songNameB = metaDataB.getSongOrder();
            compareResult = songNameA.compareTo(songNameB);
        }  else if (contentA instanceof Directory && contentB instanceof Song) {
            compareResult = A_GREATER_THAN_B;
        } else if (contentA instanceof Song && contentB instanceof Directory) {
            compareResult = B_GREATER_THAN_A;
        }

        return compareResult;
    }
}
