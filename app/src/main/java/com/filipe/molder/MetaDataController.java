package com.filipe.molder;


import android.media.MediaScannerConnection;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;

/*
 * Handles tasks to do with song metadata, mainly writing changes made
 * to the metadata of a given song.
 */
public class MetaDataController {
    private static MainActivity mContext;

    public static void setContext(MainActivity context) {
        mContext = context;
    }

    public static void generateSongMetaData(Content song, MetaData metaData) {
        try {
            File songFileObject = song.getFile();
            AudioFile audioFile = AudioFileIO.read(songFileObject);
            Tag tag = audioFile.getTag();

            Artwork albumArt = tag.getFirstArtwork();

            String songName = tag.getFirst(FieldKey.TITLE);
            songName = (songName == null) ? "" : songName;

            String artistName = tag.getFirst(FieldKey.ARTIST);
            artistName = (artistName == null) ? "" : artistName;

            String albumName = tag.getFirst(FieldKey.ALBUM);
            albumName = (albumName == null) ? "" : albumName;

            String songGenre = tag.getFirst(FieldKey.GENRE);
            songGenre = (songGenre == null) ? "" : songGenre;

            String songNumber = tag.getFirst(FieldKey.TRACK);
            songNumber = (songNumber == null) ? "" : songNumber;

            String recordingDate = tag.getFirst(FieldKey.YEAR);
            recordingDate = (recordingDate == null) ? "" : recordingDate;

            metaData.setAudioFile(audioFile);
            metaData.setTag(tag);
            metaData.setMetaData(albumArt, songName, artistName, albumName, songGenre, songNumber,
                    recordingDate);
        } catch (IOException | CannotReadException e) {
            generateErrorBoundMetaData(song, metaData);
            metaData.errorHasOccurred("This MP3 file could not be read.");
        } catch (TagException | ReadOnlyFileException e) {
            generateErrorBoundMetaData(song, metaData);
            metaData.errorHasOccurred("There is a problem with this MP3's metadata.");
        } catch (InvalidAudioFrameException e) {
            generateErrorBoundMetaData(song, metaData);
            metaData.errorHasOccurred("This MP3 has an invalid file format");
        }
    }

    /*
     * Used when there was a problem reading an MP3's metadata. Here the metadata
     * is filled with more generic values so that problematic MP3's can show up
     * with the rest of the content in a way that makes sense.
     */
    private static void generateErrorBoundMetaData(Content song, MetaData metaData) {
        File songFileObject = song.getFile();
        String fileName = songFileObject.getName();
        //The last 4 characters of the file name are removed to remove the extension
        String songName = fileName.substring(0, fileName.length() - 4);
        String artistName = "Unknown artist";

        metaData.setMetaData(null, songName, artistName, null, null, null, null);
    }

    public static void changeAlbumArt(Content song, File mNewAlbumArtFile)
            throws CannotWriteException, InvalidAlbumArtException, CannotReadAlbumArtException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            Artwork newAlbumArt = ArtworkFactory.createArtworkFromFile(mNewAlbumArtFile);

            tag.deleteArtworkField();
            tag.setField(newAlbumArt);
            audioFile.commit();

            metaData.setAlbumArt(newAlbumArt);
        } catch (TagException e) {
            throw new InvalidAlbumArtException();
        } catch (IOException e) {
            throw new CannotReadAlbumArtException();
        }
    }

    public static void changeSongName(Content song, String newSongName)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.TITLE, newSongName);
            audioFile.commit();

            metaData.setSongName(newSongName);
            /*
             * When changing the name of a song, the media store needs to be updated so that
             * the sorting order is updated as it is dependent on the name of the song.
             */
            scanSong(song);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("song name");
        }
    }

    public static void changeArtistName(Content song, String newArtistName)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.ARTIST, newArtistName);
            audioFile.commit();

            metaData.setArtistName(newArtistName);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("artist name");
        }
    }

    public static void changeAlbumName(Content song, String newAlbumName)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.ALBUM, newAlbumName);
            audioFile.commit();

            metaData.setAlbumName(newAlbumName);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("album name");
        }
    }

    public static void changeSongGenre(Content song, String newSongGenre)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.GENRE, newSongGenre);
            audioFile.commit();

            metaData.setSongGenre(newSongGenre);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("genre");
        }
    }

    public static void changeSongNumber(Content song, String newSongNumber)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.TRACK, newSongNumber);
            audioFile.commit();

            metaData.setSongNumber(newSongNumber);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("song number");
        }
    }

    public static void changeRecordingDate(Content song, String newRecordingDate)
            throws CannotWriteException, InvalidCharactersUsedException {
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = metaData.getAudioFile();
            Tag tag = metaData.getTag();

            tag.setField(FieldKey.YEAR, newRecordingDate);
            audioFile.commit();

            metaData.setRecordingDate(newRecordingDate);
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("recording date");
        }
    }

    public static void scanSong(Content song) {
        File songFile = song.getFile();
        String songFilePath = songFile.getPath();

        MediaScannerConnection.scanFile(mContext,
                new String[] { songFilePath }, null,
                null);
    }
}
