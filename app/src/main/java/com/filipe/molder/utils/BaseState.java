package com.filipe.molder.utils;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.filipe.molder.activities.MainActivity;
import com.filipe.molder.interfaces.AppState;
import com.filipe.molder.interfaces.Content;
import com.filipe.molder.models.MetaData;

import java.io.File;

public class BaseState implements AppState {

    private MainActivity mContext;

    public BaseState(MainActivity context) {
        mContext = context;
    }

    @Override
    public void contentOnClick(Content content, View view) {
        if (content.getFile().isDirectory()) {
            FileUtils.moveToDirectory(content, true);
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
        mContext.showControlsBar(true);

        AppState appState = new SelectionState(content, view, mContext);
        mContext.setState(appState);
    }

    @Override
    public void navBarOnClick(File directory) {
        FileUtils.moveBackToDir(directory);
    }

    @Override
    public void deleteButtonOnClick() {
        //This method is not to be used by this class
    }

    @Override
    public void copyButtonOnClick() {
        //This method is not to be used by this class
    }

    @Override
    public void moveButtonOnClick() {
        //This method is not to be used by this class
    }

    @Override
    public void editButtonOnClick() {
        //This method is not to be used by this class
    }

    @Override
    public void onBackPressed() {
        if (FileUtils.atRootDir()) {
            mContext.exitApp();
        } else {
            FileUtils.moveBackToPreviousDir();
        }
    }
}
