package net.mguenther.lingus.wordembedding.service;

public class UnknownTermException extends RuntimeException {

    private static final String ERROR_MESSAGE = "The term '%s' is not recognized by the current model.";

    public UnknownTermException(final String term) {
        super(String.format(ERROR_MESSAGE, term));
    }
}
