package com.filipe.molder.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.filipe.molder.R;

/*
 * This activity is used to request the user to grant the permissions
 * required in order to run the app.
 */
public class PermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
    }

    public void grantPermissionsButtonOnClick(View v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showPermissionRationaleDialog();
        } else {
            getPermissions();
        }
    }

    private void getPermissions()
    {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this,
                    "You must grant permissions in order to use this app.",
                    Toast.LENGTH_SHORT).show();
        } else if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showPermissionRationaleDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String permissionsRationale =
                "The app needs to access your files for the following reasons:" +
                "\n- To access the music on your device." +
                "\n- To edit the meta data of your MP3 files." +
                "\n- To perform mFile browser functionality.";

        builder.setMessage(permissionsRationale);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPermissions();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed()
    {
        /*
         * finishAffinity() used to close down entire app to prevent
         * user to exit this activity and go back to the main activity.
         * The user should only have access ton the app once the
         * permissions have been granted.
         */
        finishAffinity();
    }
}
