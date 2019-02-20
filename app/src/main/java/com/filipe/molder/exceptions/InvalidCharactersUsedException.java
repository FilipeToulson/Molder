package com.filipe.molder.exceptions;


/*
 * This exception is thrown if the user attempts to change the
 * metadata of a song, such as the song name, to a string that
 * contains characters that can't be written.
 */
public class InvalidCharactersUsedException extends Exception {
    public InvalidCharactersUsedException(String msg) {
        super(msg);
    }
}
