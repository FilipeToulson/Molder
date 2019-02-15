package com.filipe.molder;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
            MetaData metaData = content.getMetaData();

            if(metaData.hasErrorOccurred()) {
                Toast.makeText(mContext, metaData.getErrorMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Log.d("BaseState", "Song Clicked");
            }
        }
    }

    @Override
    public void contentOnLongClick(Content content, View view) {
        MetaData metaData = content.getMetaData();

        if(!content.getFile().isDirectory() && metaData.hasErrorOccurred()) {
            //This is a song that had an error when metadata was attempted to be generated for it

            Toast.makeText(mContext, metaData.getErrorMessage(), Toast.LENGTH_SHORT).show();
        } else {
            //This is a song or directory

            mContext.showControlsBar(true);

            AppState appState = new SelectionState(content, view, mContext);
            mContext.setState(appState);
        }
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
