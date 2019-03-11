package com.filipe.molder.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.filipe.molder.R;
import com.filipe.molder.adapters.DirectoryListAdapter;
import com.filipe.molder.adapters.NavigationBarAdapter;
import com.filipe.molder.interfaces.NavBarOnClickListener;
import com.filipe.molder.utils.FileUtils;

import java.io.File;
import java.util.List;

public class DirectorySelectorActivity extends AppCompatActivity implements NavBarOnClickListener {

    private DirectoryListAdapter mDirectoryListAdapter;
    private NavigationBarAdapter mNavigationBarAdapter;
    private String mSelectorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_selector);

        Intent intent = getIntent();
        mSelectorTask = intent.getStringExtra("selectorTask");

        TextView actionLabel = findViewById(R.id.actionLabel);
        actionLabel.setText(mSelectorTask + " to");

        File rootDir = Environment.getExternalStorageDirectory();
        List<File> directories = FileUtils.getDirectories(rootDir);
        directories.add(0, null);

        RecyclerView directoryList = findViewById(R.id.directoryList);
        directoryList.setLayoutManager(new LinearLayoutManager(this));
        mDirectoryListAdapter = new DirectoryListAdapter(directories, this);
        directoryList.setAdapter(mDirectoryListAdapter);

        RecyclerView navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mNavigationBarAdapter = new NavigationBarAdapter(this);
        mNavigationBarAdapter.addDir(rootDir);
        navigationBar.setAdapter(mNavigationBarAdapter);
    }

    @Override
    public void navBarOnClick(File directory) {
        boolean done = false;

        while(!done) {
            File file = mNavigationBarAdapter.getCurrentDir();

            if(file.getPath().equals(directory.getPath())) {
                done = true;
            } else {
                mNavigationBarAdapter.removeDir(file);
            }
        }

        moveToDirectory(directory, false);
    }

    public void dirOnClick(File directory) {
        moveToDirectory(directory, true);
    }

    public void doneOnClick(View view) {
        File destinationFile = mNavigationBarAdapter.getCurrentDir();

        Intent intent = new Intent();
        intent.putExtra("selectorTask", mSelectorTask);
        intent.putExtra("destinationFile", destinationFile);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelOnClick(View view) {
        finish();
    }

    public void moveToDirectory(File directory, boolean addDirToNavBar) {
        /*
         * You might not want a directory to be added to the navigation bar
         * every time you move to a directory. For example, when moving to
         * previous directories.
         */
        if(addDirToNavBar) {
            mNavigationBarAdapter.addDir(directory);
        }

        List<File> directories = FileUtils.getDirectories(directory);
        directories.add(0, null);
        mDirectoryListAdapter.setDirectoryList(directories);
    }

    public void createFolderOnClick() {

    }

    @Override
    public void onBackPressed() {
        if(mNavigationBarAdapter.getItemCount() == 1) {
            finish();
        } else {
            mNavigationBarAdapter.removeDir(mNavigationBarAdapter.getItemCount() - 1);
            File directory = mNavigationBarAdapter.getCurrentDir();

            moveToDirectory(directory, false);
        }
    }
}
