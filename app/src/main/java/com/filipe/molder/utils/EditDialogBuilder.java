package com.filipe.molder.utils;


import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.filipe.molder.exceptions.CannotReadAlbumArtException;
import com.filipe.molder.interfaces.Content;
import com.filipe.molder.exceptions.CouldNotRenameFolderException;
import com.filipe.molder.interfaces.TaskCompleteListener;
import com.filipe.molder.models.Directory;
import com.filipe.molder.exceptions.FileAlreadyExistsException;
import com.filipe.molder.exceptions.InvalidAlbumArtException;
import com.filipe.molder.exceptions.InvalidCharactersUsedException;
import com.filipe.molder.models.MetaData;
import com.filipe.molder.R;
import com.filipe.molder.models.Song;
import com.filipe.molder.activities.MainActivity;

import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.util.List;

public class EditDialogBuilder {

    private static final int ONE_DIR_SELECTED = 0;
    private static final int ONE_SONG_SELECTED = 1;
    private static final int MULTIPLE_SONGS_SELECTED = 2;
    private static TaskCompleteListener mTaskCompleteListener;
    private static ImageView mEditAlbumArt;
    private static File mNewAlbumArtFile;

    public static AlertDialog.Builder buildEditDialog(MainActivity context,
                                                      TaskCompleteListener taskCompleteListener,
                                                      List<Content> content, int dialogCode) {
        mTaskCompleteListener = taskCompleteListener;
        AlertDialog.Builder builder = null;

        if(dialogCode == ONE_DIR_SELECTED) {
            builder = buildDirEditDialog(context, content);
        } else if(dialogCode == ONE_SONG_SELECTED) {
            builder = buildSongEditDialog(context, content);
        } else if(dialogCode == MULTIPLE_SONGS_SELECTED) {
            builder = buildMultipleSongsEditDialog(context, content);
        }

        return builder;
    }

    private static AlertDialog.Builder buildDirEditDialog(final MainActivity context,
                                                          final List<Content> content) {
        final Directory directory = (Directory)content.get(0);
        final File oldFile = directory.getFile();
        final String dirName = oldFile.getName();

        final View editDirDialogLayout = context.getLayoutInflater().inflate(
                R.layout.edit_dir_dialog, null, false);

        final EditText dirNameEdit = editDirDialogLayout.findViewById(R.id.dirNameEdit);
        dirNameEdit.setText(dirName);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Folder");
        builder.setView(editDirDialogLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newFileName = dirNameEdit.getText().toString();

                try {
                    FileUtils.changeDirectoryName(directory, newFileName);
                } catch (FileAlreadyExistsException e) {
                    context.showErrorMessage("A folder with name \"" + newFileName +
                            "\" already exists.");
                } catch (CouldNotRenameFolderException e) {
                    context.showErrorMessage("Could not rename folder.");
                }

                mTaskCompleteListener.taskComplete(false, true);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder;
    }

    private static AlertDialog.Builder buildSongEditDialog(final MainActivity context,
                                                           List<Content> content) {
        final Song song = (Song)content.get(0);
        final MetaData metaData = song.getMetaData();
        final String songName = metaData.getSongName();
        final String artistName = metaData.getArtistName();
        final String albumName  = metaData.getAlbumName();
        final String songGenre = metaData.getSongGenre();
        final String songNumber = metaData.getSongNumber();
        final String recordingDate = metaData.getRecordingDate();
        Artwork albumArt = metaData.getAlbumArt();

        final View editSongDialogLayout = context.getLayoutInflater().inflate(
                R.layout.edit_song_dialog, null, false);

        final EditText songNameEdit = editSongDialogLayout.findViewById(R.id.songNameEdit);
        songNameEdit.setText(songName);

        final EditText artistNameEdit = editSongDialogLayout.findViewById(R.id.artistNameEdit);
        artistNameEdit.setText(artistName);

        final EditText albumNameEdit = editSongDialogLayout.findViewById(R.id.albumNameEdit);
        albumNameEdit.setText(albumName);

        final EditText songGenreEdit = editSongDialogLayout.findViewById(R.id.songGenreEdit);
        songGenreEdit.setText(songGenre);

        final EditText songNumberEdit = editSongDialogLayout.findViewById(R.id.songNumberEdit);
        songNumberEdit.setText(songNumber);

        final EditText recordingDateEdit = editSongDialogLayout.findViewById(R.id.recordingDateEdit);
        recordingDateEdit.setText(recordingDate);

        mEditAlbumArt = editSongDialogLayout.findViewById(R.id.albumArt);
        Bitmap bitmap = null;
        if(albumArt != null) {
            byte[] data = albumArt.getBinaryData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        if (bitmap != null) {
            Glide.with(context).load(bitmap).into(mEditAlbumArt);
        } else {
            Glide.with(context).load(R.drawable.placeholder).into(mEditAlbumArt);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Song");
        builder.setView(editSongDialogLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newSongName = songNameEdit.getText().toString();
                String newArtistName = artistNameEdit.getText().toString();
                String newAlbumName = albumNameEdit.getText().toString();
                String newSongGenre = songGenreEdit.getText().toString();
                String newSongNumber = songNumberEdit.getText().toString();
                String newRecordingDate = recordingDateEdit.getText().toString();

                try {
                    if(!songName.equals(newSongName)) {
                        MetaDataUtils.changeSongName(song, newSongName);
                    }

                    if(!artistName.equals(newArtistName)) {
                        MetaDataUtils.changeArtistName(song, newArtistName);
                    }

                    if(!albumName.equals(newAlbumName)) {
                        MetaDataUtils.changeAlbumName(song, newAlbumName);
                    }

                    if(!songGenre.equals(newSongGenre)) {
                        MetaDataUtils.changeSongGenre(song, newSongGenre);
                    }

                    if(!songNumber.equals(newSongNumber)) {
                        MetaDataUtils.changeSongNumber(song, newSongNumber);
                    }

                    if(!recordingDate.equals(newRecordingDate)) {
                        MetaDataUtils.changeRecordingDate(song, newRecordingDate);
                    }

                    if(mNewAlbumArtFile != null) {
                        MetaDataUtils.changeAlbumArt(song, mNewAlbumArtFile);

                        mNewAlbumArtFile = null;
                    }

                    mTaskCompleteListener.taskComplete(false, true);
                } catch(CannotWriteException e) {
                    context.showErrorMessage("Can't change meta data.");
                } catch (InvalidCharactersUsedException e) {
                    context.showErrorMessage("Invalid characters used for " + e.getMessage() + ".");
                } catch (InvalidAlbumArtException e) {
                    context.showErrorMessage("Invalid file used for album art.");
                } catch (CannotReadAlbumArtException e) {
                    context.showErrorMessage("Could not read image chosen for album art.");
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNewAlbumArtFile = null;
                dialogInterface.cancel();
            }
        });

        return builder;
    }

    private static AlertDialog.Builder buildMultipleSongsEditDialog(final MainActivity context,
                                                                    final List<Content> content) {
        final View editSongsDialogLayout = context.getLayoutInflater().inflate(
                R.layout.edit_songs_dialog, null, false);

        final EditText artistNameEdit = editSongsDialogLayout.findViewById(R.id.artistNameEdit);
        artistNameEdit.setText("");

        final EditText albumNameEdit = editSongsDialogLayout.findViewById(R.id.albumNameEdit);
        albumNameEdit.setText("");

        final EditText songGenreEdit = editSongsDialogLayout.findViewById(R.id.songGenreEdit);
        songGenreEdit.setText("");

        final EditText recordingDateEdit = editSongsDialogLayout.findViewById(R.id.recordingDateEdit);
        recordingDateEdit.setText("");

        mEditAlbumArt = editSongsDialogLayout.findViewById(R.id.albumArt);
        Glide.with(context).load(R.drawable.placeholder).into(mEditAlbumArt);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Songs");
        builder.setView(editSongsDialogLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newArtistName = artistNameEdit.getText().toString();
                String newAlbumName = albumNameEdit.getText().toString();
                String newSongGenre = songGenreEdit.getText().toString();
                String newRecordingDate = recordingDateEdit.getText().toString();

                try {
                    for (Content song : content) {
                        if(!newArtistName.equals("")) {
                            MetaDataUtils.changeArtistName(song, newArtistName);
                        }

                        if(!newAlbumName.equals("")) {
                            MetaDataUtils.changeAlbumName(song, newAlbumName);
                        }

                        if(!newSongGenre.equals("")) {
                            MetaDataUtils.changeSongGenre(song, newSongGenre);
                        }

                        if(!newRecordingDate.equals("")) {
                            MetaDataUtils.changeRecordingDate(song, newRecordingDate);
                        }

                        if (mNewAlbumArtFile != null) {
                            MetaDataUtils.changeAlbumArt(song, mNewAlbumArtFile);
                        }
                    }

                    mNewAlbumArtFile = null;
                    mTaskCompleteListener.taskComplete(false, true);
                } catch(CannotWriteException e) {
                    context.showErrorMessage("Can't change meta data.");
                } catch (InvalidCharactersUsedException e) {
                    context.showErrorMessage("Invalid characters used for " + e.getMessage() + ".");
                } catch (InvalidAlbumArtException e) {
                    context.showErrorMessage("Invalid file used for album art.");
                } catch (CannotReadAlbumArtException e) {
                    context.showErrorMessage("Could not read image chosen for album art.");
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNewAlbumArtFile = null;
                dialogInterface.cancel();
            }
        });

        return builder;
    }

    public static void setNewAlbumArtFile(MainActivity context, Uri newAlbumArtUri) {
        Glide.with(context).load(newAlbumArtUri).into(mEditAlbumArt);
        mNewAlbumArtFile = new File(getPathFromURI(context, newAlbumArtUri));
    }

    private static String getPathFromURI(MainActivity context, Uri imageURI) {
        String filePath = "";

        Cursor cursor = context.getContentResolver().query(imageURI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int filePathColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(filePathColumn);

            cursor.close();
        }

        return filePath;
    }
}
