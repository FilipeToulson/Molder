package com.filipe.molder;


import android.database.Cursor;
import android.provider.MediaStore;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

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
        rootDir.getFiles().sort(new ContentComparator());
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

                        dir.getFiles().sort(new ContentComparator());
                    } else {
                        addContentsToDir(rootDir, file);
                    }
                }
            }
        }
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
                int filePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int sortOrderColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                /*
                 * Even though the media store is not used to obtain the metadata of a song, it is
                 * still used to get information that is required on app startup quickly. For
                 * instance, song sorting order is obtained in order to not have to read the file
                 * in order to get the name of the song.
                 */
                String songFilePath = cursor.getString(filePathColumn);
                String songOrder = cursor.getString(sortOrderColumn);

                File songFileObject = new File(songFilePath);
                MetaData metaData = new MetaData(songOrder);

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

    public static void changeDirectoryName(Content directory, String newFileName)
            throws FileAlreadyExistsException, CouldNotRenameFolderException {
        File oldFile = directory.getFile();
        String oldFilePath = oldFile.getPath();
        String filePathNoName = oldFilePath.substring(0, oldFilePath.lastIndexOf("/"));
        File newFile = new File(filePathNoName + "/" + newFileName);

        if(newFile.exists()){
            throw new FileAlreadyExistsException();
        } else {
            boolean renamingSuccessful = oldFile.renameTo(newFile);

            if (renamingSuccessful) {
                directory.setFile(newFile);
            } else {
                throw new CouldNotRenameFolderException();
            }

            updateContents(directory, oldFilePath, newFile.getPath());
        }
    }

    /*
     * This method updates the path of the files within a directory to the newly renamed path that
     * was created as a result of renaming the directory.
     */
    private static void updateContents(Content dir, String oldFilePath, String newFilePath) {
        List<Content> contents = dir.getFiles();

        for(Content content : contents) {
            File contentFile = content.getFile();
            String contentFilePath = contentFile.getPath();
            String newContentFilePath = newFilePath +
                    contentFilePath.substring(oldFilePath.length(), contentFilePath.length());
            File newFile = new File(newContentFilePath);
            content.setFile(newFile);

            if(content instanceof Directory) {
                updateContents(content, oldFilePath, newFilePath);
            } else if(content instanceof Song) {
                //Need to scan songs as to update their paths in the media store:
                MetaDataController.scanSong(content);
            }
        }
    }
}
