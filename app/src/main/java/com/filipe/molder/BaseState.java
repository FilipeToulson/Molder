package com.filipe.molder;


import android.util.Log;
import android.view.View;

import java.io.File;

public class BaseState implements AppState {

    private MainActivity mContext;

    public BaseState(MainActivity context) {
        mContext = context;
    }

    @Override
    public void contentOnClick(Content content, View view) {
        if (content.getFile().isDirectory()) {
            FileController.moveToDirectory(content, true);
        } else {
            Log.d("BaseState", "Song Clicked");
        }
    }

    @Override
    public void contentOnLongClick(Content content, View view) {
        mContext.showControlsBar(true);

        AppState appState = new SelectionState(content, view, mContext);
        mContext.setState(appState);
    }

    @Override
    public void navBarOnClick(File directory) {
        FileController.moveBackToDir(directory);
    }

    @Override
    public void editButtonOnClick() {
        //This method is not to be used by this class
    }

    @Override
    public void onBackPressed() {
        if (FileController.atRootDir()) {
            mContext.exitApp();
        } else {
            FileController.moveBackDirOnce();
        }
    }
}
