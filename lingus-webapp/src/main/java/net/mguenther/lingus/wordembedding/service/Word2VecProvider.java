package net.mguenther.lingus.wordembedding.service;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@ApplicationScoped
class Word2VecProvider {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Word2Vec model;

    private ModelMetadata currentModelMetadata;

    boolean hasWord(final String term) {
        lock.readLock().lock();
        try {
            if (model == null) {
                throw new NoModelLoadedException();
            }
            return model.hasWord(term);
        } finally {
            lock.readLock().unlock();
        }
    }

    Collection<String> wordsNearestSum(final String word, final int howMany) {
        lock.readLock().lock();
        try {
            if (model == null) {
                throw new NoModelLoadedException();
            }
            return model.wordsNearestSum(word, howMany);
        } finally {
            lock.readLock().unlock();
        }
    }

    double similarity(final String word, final String otherWord) {
        lock.readLock().lock();
        try {
            if (model == null) {
                throw new NoModelLoadedException();
            }
            return model.similarity(word, otherWord);
        } finally {
            lock.readLock().unlock();
        }
    }

    void load(final ModelMetadata modelMetadata) {
        final String locationOnFS = modelMetadata.getLocationOnFS();
        lock.writeLock().lock();
        try {
            final File modelFile = new File(locationOnFS);
            if (!modelFile.exists()) {
                throw new UnableToActivateModelException(locationOnFS);
            }
            log.info("Received a request to load the model from file '{}'.", locationOnFS);
            this.model = null;
            this.model = WordVectorSerializer.readWord2VecModel(modelFile);
            this.currentModelMetadata = modelMetadata;
            log.info("Successfully loaded Word2Vec model from file '{}'.", locationOnFS);
        } catch (Exception e) {
            throw new UnableToActivateModelException(locationOnFS, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    ModelMetadata getCurrentModel() {
        return currentModelMetadata;
    }
}
