package com.filipe.molder;


import android.graphics.Color;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectionState implements AppState, EditCompleteListener {

    private static final int ONE_DIR_SELECTED = 0;
    private static final int ONE_SONG_SELECTED = 1;
    private static final int MULTIPLE_SONGS_SELECTED = 2;
    private int mNumberOfSongsSelected;
    private int mNumberOfDirsSelected;
    private List<Content> mSelectedContentList;
    private List<View> mSelectedViewsList;
    private MainActivity mContext;

    public SelectionState(Content selectedContent, View view, MainActivity context) {
        mNumberOfSongsSelected = 0;
        mNumberOfDirsSelected = 0;

        mSelectedContentList = new ArrayList<>();
        mSelectedViewsList = new ArrayList<>();
        mContext = context;

        contentOnClick(selectedContent, view);
    }

    @Override
    public void contentOnClick(Content content, View view) {
        if(content.isSelected()) {
            content.setIsSelected(false);
            view.setBackgroundColor(Color.TRANSPARENT);

            mSelectedContentList.remove(content);
            mSelectedViewsList.remove(view);

            if (content instanceof Song) {
                mNumberOfSongsSelected--;
            } else if (content instanceof Directory){
                mNumberOfDirsSelected--;
            }
        } else {
            content.setIsSelected(true);
            view.setBackgroundColor(Color.LTGRAY);

            mSelectedContentList.add(content);
            mSelectedViewsList.add(view);

            if (content instanceof Song) {
                mNumberOfSongsSelected++;
            } else if (content instanceof Directory){
                mNumberOfDirsSelected++;
            }
        }

        if((mNumberOfSongsSelected == 0 && mNumberOfDirsSelected == 0) ||
                (mNumberOfSongsSelected > 0 && mNumberOfDirsSelected > 0) ||
                mNumberOfDirsSelected > 1) {
            mContext.enableEditButton(false);
        } else {
            mContext.enableEditButton(true);
        }
    }

    @Override
    public void contentOnLongClick(Content content, View view) {
        contentOnClick(content, view);
    }

    @Override
    public void navBarOnClick(File directory) {
        /*
         * This method is left blank on purpose as to not allow
         * the user to go back to previous directories while in
         * the selection state using the navigation bar.
         */
    }

    @Override
    public void editButtonOnClick() {
        int dialogCode = ONE_DIR_SELECTED;

        /*
         * There is no need to check the number of files selected
         * as this method would only be able to be called if the
         * edit button was enabled. This means that there would
         * not be more than one directory selected, or songs and
         * directories selected.
         */
        if(mNumberOfSongsSelected == 1) {
            dialogCode = ONE_SONG_SELECTED;
        } else if(mNumberOfSongsSelected > 1) {
            dialogCode = MULTIPLE_SONGS_SELECTED;
        }

        mContext.showEditDialog(this, mSelectedContentList, dialogCode);
    }

    //Called when the user sets the changes made from an edit dialog
    @Override
    public void editComplete() {
        exitSelectionState();
    }

    @Override
    public void onBackPressed() {
        exitSelectionState();
    }

    private void exitSelectionState() {
        for(Content content : mSelectedContentList) {
            content.setIsSelected(false);
        }

        int i = 0;

        for(View view : mSelectedViewsList) {
            Log.d("SelectionState", "To transparent " + i);
            view.setBackgroundColor(Color.TRANSPARENT);
            i++;
        }

        mSelectedContentList.clear();
        mSelectedViewsList.clear();

        mContext.showControlsBar(false);

        AppState appState = new BaseState(mContext);
        mContext.setState(appState);
    }
}
