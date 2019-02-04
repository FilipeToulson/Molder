package com.filipe.molder;


import android.media.MediaScannerConnection;
import android.util.Log;

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

    public static void changeSongName(Content song, String newSongName)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.TITLE, newSongName);
            audioFile.commit();

            metaData.setSongName(newSongName);
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("song name");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeArtistName(Content song, String newArtistName)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.ARTIST, newArtistName);
            audioFile.commit();

            metaData.setArtistName(newArtistName);
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("artist name");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeAlbumName(Content song, String newAlbumName)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.ALBUM, newAlbumName);
            audioFile.commit();

            metaData.setAlbumName(newAlbumName);
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("album name");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeSongGenre(Content song, String newSongGenre)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        /*
         * The MetaData class isn't used here to change the song genre as the song
         * genre is not stored in the MetaData class.
         */

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.GENRE, newSongGenre);
            audioFile.commit();
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("genre");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeSongNumber(Content song, String newSongNumber)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        MetaData metaData = song.getMetaData();

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.TRACK, newSongNumber);
            audioFile.commit();

            metaData.setSongNumber(newSongNumber);
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("song number");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeRecordingDate(Content song, String newRecordingDate)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException {
        File file = song.getFile();
        MetaData metaData = song.getMetaData();

        Log.d("EditDialog", "RD: " + newRecordingDate);

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.setField(FieldKey.YEAR, newRecordingDate);
            audioFile.commit();

            metaData.setRecordingDate(newRecordingDate);
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidCharactersUsedException("recording date");
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
        }
    }

    public static void changeAlbumArt(Content song, Artwork newAlbumArt)
            throws CannotReadException, ReadOnlyFileException, CannotWriteException,
            InvalidCharactersUsedException, IncorrectFileFormatException, InvalidAlbumArtException {
        File file = song.getFile();

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            tag.deleteArtworkField();
            tag.setField(newAlbumArt);
            audioFile.commit();
        } catch (IOException  e) {
            throw new CannotReadException();
        } catch (TagException e) {
            throw new InvalidAlbumArtException();
        } catch (InvalidAudioFrameException e) {
            throw new IncorrectFileFormatException();
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
