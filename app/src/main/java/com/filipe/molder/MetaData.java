package com.filipe.molder;


import android.database.Cursor;
import android.provider.MediaStore;

//Store's an MP3 file's meta data
public class MetaData {

    private String mSongName;
    private String mArtistName;
    private String mAlbumName;
    private int mAlbumArtId;


    public MetaData(String songNAme, String artistName, String albumName, int albumArtId) {
        mSongName = songNAme;
        mArtistName = artistName;
        mAlbumName = albumName;
        mAlbumArtId = albumArtId;

    }

    public String getSongName() {
        return mSongName;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public int getAlbumArtId() {
        return mAlbumArtId;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    public void setArtistName(String artistName) {
        this.mArtistName = artistName;
    }

    public void setAlbumName(String albumName) {
        this.mAlbumName = albumName;
    }

    public void setAlbumArtId(int albumArtId) {
        this.mAlbumArtId = albumArtId;
    }

    public String getAlbumArtPath(MainActivity context) {
        /*
         * Here the Media Store is being used to get the file path of the cached album art.
         *
         * The query is looking for the album art that has the album art ID that is the
         * same as the mAlbumArtId.
         */
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ? ",
                new String[] {String.valueOf(mAlbumArtId)},
                null);

        String albumArtPath = "";
        if (cursor.moveToNext()) {
            int i = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            albumArtPath = cursor.getString(i);
        }

        cursor.close();

        return albumArtPath;
    }
}
