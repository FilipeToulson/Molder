package com.filipe.molder.utils;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.filipe.molder.exceptions.FileCouldNotBeDeletedException;
import com.filipe.molder.activities.MainActivity;
import com.filipe.molder.interfaces.Content;
import com.filipe.molder.interfaces.TaskCompleteListener;

import java.util.List;

public class DeleteWarningDialogBuilder {

    public static AlertDialog.Builder buildDeleteWarningDialog(final MainActivity context,
                                                               final TaskCompleteListener
                                                                       taskCompleteListener,
                                                               final List<Content> content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        int numberOfItems = content.size();
        if(numberOfItems == 1) {
            builder.setMessage("Delete 1 item?");
        } else {
            builder.setMessage("Delete " + numberOfItems + " items?");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    FileUtils.deleteFiles(null, content);
                } catch (FileCouldNotBeDeletedException e) {
                    context.showErrorMessage("Could not delete " + e.getMessage() + ".");
                }

                taskCompleteListener.taskComplete(true, true);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return  builder;
    }
}
