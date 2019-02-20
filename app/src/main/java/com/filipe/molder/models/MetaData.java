package com.filipe.molder.models;


import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

//Store's an MP3 file's meta data
public class MetaData {

    /*
     * mAudioFile and mTag are stored for use later when changing the
     * metadata of a song. This way no more is needed to be spent on
     * reading an mp3 file again.
     */
    private AudioFile mAudioFile;
    private Tag mTag;
    private boolean mMetaDataSet;
    private String mSongOrder;
    private Artwork mAlbumArt;
    private String mSongName;
    private String mArtistName;
    private String mAlbumName;
    private String mSongGenre;
    private String mSongNumber;
    private String mRecordingDate;
    /*
     * mErrorOccurred and mErrorMessage are used for when there was an error
     * when attempting to generate the metadata of a song. If there was,
     * an error message that specifies to the user what went wrong with
     * can be given when they try to interact with a file that couldn't
     * be read.
     */
    private boolean mErrorOccurred;
    private String mErrorMessage;

    public MetaData(String songOrder) {
        mMetaDataSet = false;
        mSongOrder = songOrder;
        mErrorOccurred = false;
        mErrorMessage = "";
    }

    public void setAudioFile(AudioFile audioFile) {
        mAudioFile = audioFile;
    }

    public void setTag(Tag tag) {
        mTag = tag;
    }

    public void setMetaData(Artwork albumArt, String songName, String artistName, String albumName,
                            String songGenre, String songNumber, String recordingDate) {
        mAlbumArt = albumArt;
        mSongName = songName;
        mArtistName = artistName;
        mAlbumName = albumName;
        mSongGenre = songGenre;
        mSongNumber = songNumber;
        mRecordingDate = recordingDate;

        mMetaDataSet = true;
    }

    public void setAlbumArt(Artwork albumArt) {
        mAlbumArt = albumArt;
    }

    public void setSongName(String songName) {
        mSongName = songName;
    }

    public void setArtistName(String artistName) {
        mArtistName = artistName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public void setSongGenre(String songGenre) {
        mSongGenre = songGenre;
    }

    public void setSongNumber(String songNumber) {
        mSongNumber = songNumber;
    }

    public void setRecordingDate(String recordingDate) {
        mRecordingDate = recordingDate;
    }

    public void errorHasOccurred(String errorMessage) {
        mErrorOccurred = true;
        mErrorMessage = errorMessage;
    }

    public AudioFile getAudioFile() {
        return mAudioFile;
    }

    public Tag getTag() {
        return mTag;
    }

    public boolean isMetaDataSet() {
        return mMetaDataSet;
    }

    public String getSongOrder() {
        return mSongOrder;
    }

    public Artwork getAlbumArt() {
        return mAlbumArt;
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

    public String getSongGenre() {
        return mSongGenre;
    }

    public String getSongNumber() {
        return mSongNumber;
    }

    public String getRecordingDate() {
        return mRecordingDate;
    }

    public boolean hasErrorOccurred() {
        return mErrorOccurred;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
