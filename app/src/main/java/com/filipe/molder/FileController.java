package com.filipe.molder;


import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;

/*
 * Handles tasks to do with the file system such as moving to
 * a specific directory, copying files, etc.
 */
public class FileController {

    private static MainActivity mContext;
    private static ContentsListAdapter sContentsListAdapter;
    private static NavigationBarAdapter sNavBarAdapter;
    private static Content currentDir;

    public static void setContext(MainActivity context) {
        mContext = context;
    }

    public static void setContentsListAdapter(ContentsListAdapter contentsListAdapter) {
        sContentsListAdapter = contentsListAdapter;
    }

    public static void setNavBarAdapter(NavigationBarAdapter navBarAdapter) {
        FileController.sNavBarAdapter = navBarAdapter;
    }

    public static void constructFileTree(File rootFileObject) {
        Content rootDir = new Directory(rootFileObject, null);

        addContentsToDir(rootDir, rootFileObject);
        moveToDirectory(rootDir, true);
    }

    //Adds directories and songs to a specific directory
    private static void addContentsToDir(Content rootDir, File rootFileObject) {
        File[] files = rootFileObject.listFiles();

        for(File file : files) {
            if(!file.isHidden()) {
                if(file.isDirectory()) {
                    if (containsSongs(file)) {
                        Content dir = new Directory(file, rootDir);
                        rootDir.addFile(dir);

                        addContentsToDir(dir, file);
                        addSongsToDir(dir, file);

                        //This call to sort, sorts songs within a directory
                        dir.getFiles().sort(new ContentComparator());
                    } else {
                        addContentsToDir(rootDir, file);
                    }
                }
            }
        }

        //This call to sort, sorts directories within a directory
        rootDir.getFiles().sort(new ContentComparator());
    }

    private static boolean containsSongs(File dir) {
        boolean containsSongs = false;
        File[] files = dir.listFiles();

        for(File file : files) {
            if(!file.isHidden() && file.isFile()) {
                String fileExtension = FilenameUtils.getExtension(file.getAbsolutePath());

                if (fileExtension.equals("mp3")) {
                    containsSongs = true;

                    //As soon as an mp3 is found, the loop is exited
                    break;
                }
            }
        }

        return containsSongs;
    }

    private static void addSongsToDir(Content dir, File driFileObject) {
        /*
         * Here the Media Store is used in order to retrieve cached meta data in order
         * to make the process of getting an mp3 file's meta data faster.
         *
         * The selection works by finding entries in the Media Store that are like
         * selectionArgs[0] and not like selectionArgs[1], meaning all mp3 files
         * in a directory, but none from its subdirectories.
         */
        String dirFilePath = driFileObject.getAbsolutePath();
        String selection = MediaStore.Audio.Media.DATA + " LIKE ? AND " +
                MediaStore.Audio.Media.DATA + " NOT LIKE ? ";
        String[] selectionArgs = new String[]{
                "%" + dirFilePath + "/%",
                "%" + dirFilePath + "/%/%"
        };
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection,
                selectionArgs, null);

        if(cursor != null && cursor.moveToFirst()) {
            do {
                //These refer to the column number where certain information is stored:
                int songIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int songNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int songNumberColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
                int recordingDateColumn  = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
                int filePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                int songId = cursor.getInt(songIdColumn);
                int albumArtId = cursor.getInt(albumIdColumn);

                String songName = cursor.getString(songNameColumn);
                songName = (songName == null) ? "" : songName;

                String artistName = cursor.getString(artistNameColumn);
                artistName = (artistName == null) ? "" : artistName;

                String albumName = cursor.getString(albumNameColumn);
                albumName = (albumName == null) ? "" : albumName;

                String songNumber = cursor.getString(songNumberColumn);
                songNumber = (songNumber == null) ? "" : songNumber;

                String recordingDate = cursor.getString(recordingDateColumn);
                recordingDate = (recordingDate == null) ? "" : recordingDate;

                String songFilePath = cursor.getString(filePathColumn);
                File songFileObject = new File(songFilePath);

                MetaData metaData = new MetaData(songId, albumArtId, songName, artistName, albumName, songNumber,
                        recordingDate);
                Song song = new Song(songFileObject, metaData);

                dir.addFile(song);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    public static void moveToDirectory(Content dir, boolean addDirToNavBar) {
        /*
         * You might not want a directory to be added to the navigation bar
         * every time you move to a directory. For example, when moving to
         * previous directories.
         */
        if(addDirToNavBar) {
            sNavBarAdapter.addDir(dir.getFile());
        }

        currentDir = dir;
        sContentsListAdapter.setContentsList(dir.getFiles());
    }

    public static void moveBackDirOnce() {
        sNavBarAdapter.removeDir(currentDir.getFile());
        moveToDirectory(currentDir.getParentDir(), false);
    }

    public static void moveBackToDir(File targetDir) {
        boolean done = false;
        File currentDirFileObject = currentDir.getFile();

        if(!currentDirFileObject.equals(targetDir)) {
            while (!done && sNavBarAdapter.getItemCount() > 0) {
                if (targetDir.getPath().equals(currentDir.getFile().getPath())) {
                    done = true;
                } else {
                    sNavBarAdapter.removeDir(currentDirFileObject);
                    currentDir = currentDir.getParentDir();
                    currentDirFileObject = currentDir.getFile();
                }
            }

            moveToDirectory(currentDir, false);
        }
    }

    public static boolean atRootDir() {
        boolean atRootDir = false;

        if(sNavBarAdapter.getItemCount() == 1) {
            atRootDir = true;
        }

        return atRootDir;
    }
}
