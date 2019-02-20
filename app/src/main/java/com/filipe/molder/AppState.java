package com.filipe.molder;


import android.view.View;

import java.io.File;

public interface AppState {
    void contentOnClick(Content content, View view);
    void contentOnLongClick(Content content, View view);
    void navBarOnClick(File directory);
    void deleteButtonOnClick();
    void editButtonOnClick();
    void onBackPressed();
}
