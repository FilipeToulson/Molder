package com.filipe.molder;


import android.graphics.Color;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectionState implements AppState {

    private List<Content> mSelectedContentList;
    private List<View> mSelectedViewsList;
    private MainActivity mContext;

    public SelectionState(Content selectedContent, View view, MainActivity context) {
        mSelectedContentList = new ArrayList<>();
        mSelectedViewsList = new ArrayList<>();
        mContext = context;

        mSelectedContentList.add(selectedContent);
        mSelectedViewsList.add(view);
    }

    @Override
    public void contentOnClick(Content content, View view) {
        if(content.isSelected()) {
            content.setIsSelected(false);
            view.setBackgroundColor(Color.TRANSPARENT);

            mSelectedContentList.remove(content);
            mSelectedViewsList.remove(view);
        } else {
            content.setIsSelected(true);
            view.setBackgroundColor(Color.LTGRAY);

            mSelectedContentList.add(content);
            mSelectedViewsList.add(view);
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
    public void onBackPressed() {
        for(Content content : mSelectedContentList) {
            content.setIsSelected(false);
        }

        for(View view : mSelectedViewsList) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        mSelectedContentList.clear();
        mSelectedViewsList.clear();

        AppState appState = new BaseState(mContext);
        mContext.setState(appState);
    }
}
