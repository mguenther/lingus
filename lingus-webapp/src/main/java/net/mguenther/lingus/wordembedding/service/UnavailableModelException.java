package net.mguenther.lingus.wordembedding.service;

public class UnavailableModelException extends RuntimeException {

    private static final String ERROR_MESSAGE = "The requested model '%s' is not available.";

    public UnavailableModelException(final String filename) {
        super(String.format(ERROR_MESSAGE, filename));
    }
}
