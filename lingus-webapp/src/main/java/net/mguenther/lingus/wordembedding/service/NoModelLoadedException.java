package net.mguenther.lingus.wordembedding.service;

public class NoModelLoadedException extends RuntimeException {

    private static final String ERROR_MESSAGE = "There is no Word2Vec model loaded at the present time.";
}
