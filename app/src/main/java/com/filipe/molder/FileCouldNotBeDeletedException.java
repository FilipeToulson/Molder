package com.filipe.molder;


class FileCouldNotBeDeletedException extends Exception {
    public FileCouldNotBeDeletedException(String fileName) {
        super(fileName);
    }
}
