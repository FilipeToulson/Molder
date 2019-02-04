package com.filipe.molder;


import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

//Store's an MP3 file's meta data
public class MetaData {

    private int mSongId;
    private String mSongName;
    private String mArtistName;
    private String mAlbumName;
    private String mSongNumber;
    private String mRecordingDate;
    private int mAlbumArtId;


    public MetaData(int songId, int albumArtId, String songNAme, String artistName, String albumName,
                    String songNumber, String recordingDate) {
        mSongId = songId;
        mAlbumArtId = albumArtId;
        mSongName = songNAme;
        mArtistName = artistName;
        mAlbumName = albumName;
        mSongNumber = songNumber;
        mRecordingDate = recordingDate;
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

    public void setSongNumber(String songNumber) {
        this.mSongNumber = songNumber;
    }

    public void setRecordingDate(String recordingDate) {
        this.mRecordingDate = recordingDate;
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

    public String getSongNumber() {
        return mSongNumber;
    }

    public String getRecordingDate() {
        return mRecordingDate;
    }


    public String getGenre(MainActivity context) {
        /*
         * Here the Media Store is being used to get the genre of this song.
         *
         * The query is looking for the genre that has the ID that is the
         * same as the mSongId.
         */
        Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", mSongId);
        Cursor cursor = context.getContentResolver().query(uri,
                new String[] {MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID},
                null, null, null);

        String genre = "";
        if (cursor.moveToNext()) {
            int genreColumn = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME);

            genre = cursor.getString(genreColumn);
        }

        cursor.close();

        return genre;
    }

    public String getAlbumArtPath(MainActivity context) {
        /*
         * Here the Media Store is being used to get the file path of the cached album art.
         *
         * The query is looking for the album art that has the album art ID that is the
         * same as the mAlbumArtId.
         */
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ? ",
                new String[] {String.valueOf(mAlbumArtId)},
                null);

        String albumArtPath = "";
        if (cursor.moveToNext()) {
            int albumArtColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

            albumArtPath = cursor.getString(albumArtColumn);
        }

        cursor.close();

        return albumArtPath;
    }
}
