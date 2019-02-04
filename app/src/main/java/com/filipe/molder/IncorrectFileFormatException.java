package com.filipe.molder;

/*
 * Thrown when a file that is not a song is attempted to be read.
 */
class IncorrectFileFormatException extends Exception {
    public IncorrectFileFormatException() {
        super();
    }
}
