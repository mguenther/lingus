package net.mguenther.lingus.wordembedding.service;

public class UnableToActivateModelException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Unable to load Word2Vec model from file '%s'.";

    public UnableToActivateModelException(final String locationOnFS) {
        this(locationOnFS, null);
    }

    public UnableToActivateModelException(final String locationOnFS, final Throwable cause) {
        super(String.format(ERROR_MESSAGE, locationOnFS), cause);
    }
}
