package com.filipe.molder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;

//Takes care of handling the main UI.
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionsResult = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionsResult != PackageManager.PERMISSION_GRANTED) {
            //Permissions have not yet been granted by user. Request them
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivityForResult(intent, PERMISSIONS_ACTIVITY_REQUEST_CODE);
        } else {
            init();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    //Permissions have been granted from PermissionsActivity
                    init();
                }
            }
        }
    }

    private void init() {
        RecyclerView contentsList = findViewById(R.id.contentsList);
        contentsList.setLayoutManager(new LinearLayoutManager(this));
        ContentsListAdapter contentsListAdapter = new ContentsListAdapter(this);
        contentsList.setAdapter(contentsListAdapter);

        RecyclerView navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        NavigationBarAdapter navigationBarAdapter = new NavigationBarAdapter();
        navigationBar.setAdapter(navigationBarAdapter);

        File root = Environment.getExternalStorageDirectory();
        FileController.setContext(this);
        FileController.setContentsListAdapter(contentsListAdapter);
        FileController.setNavBarAdapter(navigationBarAdapter);
        FileController.constructFileTree(root);
    }

    @Override
    public void onBackPressed() {
        if (FileController.atRootDir()) {
            //Exit the app
            super.onBackPressed();
        } else {
            FileController.moveBackDirOnce();
        }
    }
}
