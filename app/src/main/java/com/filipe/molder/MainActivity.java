package com.filipe.molder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

//Takes care of handling the main UI.
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_ACTIVITY_REQUEST_CODE = 0;
    private static final int PHOTO_PICKER_ACTIVITY_REQUEST_CODE = 1;
    private ContentsListAdapter mContentsListAdapter;
    private AppState mCurrentState;
    private ConstraintLayout mControlsBar;
    private int mControlsBarHeight;

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

                break;
            } case PHOTO_PICKER_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    //An image has been picked by the user
                    Uri newAlbumArtUri = data.getData();

                    EditDialogBuilder.setNewAlbumArtFile(this, newAlbumArtUri);
                }
            }
        }
    }

    private void init() {
        long time= System.currentTimeMillis();
        Log.d("TIME_COUNT", "The time is: " + time);

        mControlsBar = findViewById(R.id.controlsBar);
        mControlsBarHeight = mControlsBar.getMaxHeight();
        mControlsBar.setMaxHeight(0);

        RecyclerView contentsList = findViewById(R.id.contentsList);
        contentsList.setLayoutManager(new LinearLayoutManager(this));
        mContentsListAdapter = new ContentsListAdapter(this);
        contentsList.setAdapter(mContentsListAdapter);

        RecyclerView navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        NavigationBarAdapter navigationBarAdapter = new NavigationBarAdapter(this);
        navigationBar.setAdapter(navigationBarAdapter);

        File root = Environment.getExternalStorageDirectory();
        FileController.setContext(this);
        FileController.setContentsListAdapter(mContentsListAdapter);
        FileController.setNavBarAdapter(navigationBarAdapter);
        FileController.constructFileTree(root);

        MetaDataController.setContext(this);

        mCurrentState = new BaseState(this);

        long newTime= System.currentTimeMillis();
        Log.d("TIME_COUNT", "The time now is: " + newTime);

        long diff = newTime - time;
        Log.d("TIME_COUNT", "The time diff is: " + diff);
    }

    public void contentOnClick(Content content, View view) {
        mCurrentState.contentOnClick(content, view);
    }

    public void contentOnLongClick(Content content, View view) {
        mCurrentState.contentOnLongClick(content, view);
    }

    public void navBarOnClick(File directory) {
        mCurrentState.navBarOnClick(directory);
    }

    public void deleteButtonOnClick(View view) {
        mCurrentState.deleteButtonOnClick();
    }

    public void editButtonOnClick(View view) {
        mCurrentState.editButtonOnClick();
    }

    public void albumArtOnClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_ACTIVITY_REQUEST_CODE);
    }

    public void removeContent(List<Content> contents) {
        mContentsListAdapter.removeContent(contents);
    }

    public void refreshContentsList() {
        mContentsListAdapter.refreshList();
    }

    public void showControlsBar(boolean showControlsBar) {
        if(showControlsBar) {
            mControlsBar.setMaxHeight(mControlsBarHeight);
        } else {
            mControlsBar.setMaxHeight(0);
        }
    }

    public void enableEditButton(boolean enableEditButton) {
        ImageButton editButton = findViewById(R.id.editButton);
        TextView editButtonLabel = findViewById(R.id.editButtonLabel);

        editButton.setEnabled(enableEditButton);

        if(enableEditButton) {
            editButtonLabel.setVisibility(View.VISIBLE);
        } else {
            editButtonLabel.setVisibility(View.INVISIBLE);
        }
    }

    public void showDeleteWarningDialog(DeleteCompleteListener deleteCompleteListener,
                                        List<Content> content) {
        AlertDialog.Builder deleteWarningDialog = DeleteWarningDialogBuilder.
                buildDeleteWarningDialog(this, deleteCompleteListener, content);
        deleteWarningDialog.show();
    }

    public void showEditDialog(EditCompleteListener editCompleteListener,
                               List<Content> content, int dialogCode) {
        AlertDialog.Builder editDialog = EditDialogBuilder.buildEditDialog(this,
                editCompleteListener, content, dialogCode);
        editDialog.show();
    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void setState(AppState newAppState) {
        mCurrentState = newAppState;
    }

    @Override
    public void onBackPressed() {
        mCurrentState.onBackPressed();
    }

    public void exitApp() {
        super.onBackPressed();
    }
}
