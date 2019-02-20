package com.filipe.molder.exceptions;


public class FileCouldNotBeDeletedException extends Exception {
    public FileCouldNotBeDeletedException(String fileName) {
        super(fileName);
    }
}
