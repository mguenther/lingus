package net.mguenther.lingus.wordembedding.service;

import lombok.extern.slf4j.Slf4j;
import net.mguenther.lingus.wordembedding.api.AvailableModel;
import net.mguenther.lingus.wordembedding.api.AvailableModelsResult;
import net.mguenther.lingus.wordembedding.api.FindSimilarWordsRequest;
import net.mguenther.lingus.wordembedding.api.FindSimilarWordsResult;
import net.mguenther.lingus.wordembedding.api.ActivateModelRequest;
import net.mguenther.lingus.wordembedding.api.ActivateModelResult;
import net.mguenther.lingus.wordembedding.api.MeasureSimilarityRequest;
import net.mguenther.lingus.wordembedding.api.MeasureSimilarityResult;
import net.mguenther.lingus.wordembedding.api.SimilarWord;
import net.mguenther.lingus.wordembedding.api.Term;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class WordEmbeddingService {

    private final Word2VecProvider modelProvider;

    private final List<ModelMetadata> availableModels;

    public WordEmbeddingService() {
        // NO-OP (this unfortunately has to be done to make CDI happy)
        availableModels = Collections.emptyList();
        modelProvider = null;
    }

    @Inject
    public WordEmbeddingService(@ConfigProperty(name = "lingus.data.dir") final String locationOnFS,
                                final Word2VecProvider modelProvider) {
        this.availableModels = scan(locationOnFS);
        this.modelProvider = modelProvider;
    }

    private List<ModelMetadata> scan(final String locationOnFS) {
        try {
            final List<ModelMetadata> found = Files.list(Paths.get(locationOnFS))
                    .map(pathToFile -> new ModelMetadata(pathToFile.getFileName().toString(), pathToFile.toString(), false))
                    .collect(Collectors.toList());
            found.forEach(model -> log.info("Found model at '{}'.", model.getLocationOnFS()));
            return found;
        } catch (IOException e) {
            final String message = "Application startup has been interrupted, as the model location at '%s' does not exist.";
            throw new RuntimeException(String.format(message, locationOnFS));
        }
    }

    /**
     * Finds similar words to the given set of terms (cf. {@link FindSimilarWordsRequest#getTerms()}) and
     * gives information about their similarity measure wrt. the associated original term. The knowledge
     * base for this is a vector model based on the {@code Word2Vec} implementation of Deeplearning4J.
     *
     * The resulting list of similar words per term is ordered by the top {@code N} results according to the
     * underlying model, where {@code N} is {@link FindSimilarWordsRequest#getSuggestionsPerTerm()}.
     *
     * @param request
     *      contains the set of terms for which similar words shall be determined, along with other
     *      parameters
     * @return
     *      similar words for the set of original terms
     */
    public FindSimilarWordsResult similar(final FindSimilarWordsRequest request) {
        final Set<Term> resolvedTerms = new HashSet<>();
        for (String term : request.getTerms()) {
            if (!modelProvider.hasWord(term)) {
                log.warn("The model does not recognize the term '{}'.", term);
                resolvedTerms.add(noResolution(term));
            } else {
                resolvedTerms.add(resolveTerm(term, request.getSuggestionsPerTerm()));
            }
        }
        return new FindSimilarWordsResult(Collections.unmodifiableSet(resolvedTerms));
    }

    private Term noResolution(final String term) {
        return new Term(term, Collections.unmodifiableList(new ArrayList<>()));
    }

    private Term resolveTerm(final String term, final int maxSuggestions) {
        final List<SimilarWord> similarWords = Collections.unmodifiableList(modelProvider.wordsNearestSum(term, maxSuggestions)
                .stream()
                .filter(similarWord -> !similarWord.equalsIgnoreCase(term))
                .map(similarWord -> new SimilarWord(similarWord, modelProvider.similarity(term, similarWord)))
                .collect(Collectors.toList()));
        return new Term(term, similarWords);
    }

    /**
     * Obtains a similarity measurement between both input words (cf. {@link MeasureSimilarityRequest}). The
     * knowledge base for this is a vector model based on the {@code Word2Vec} implementation of Deeplearning4J.
     *
     * @param request
     *      contains the pair of input words for which a similarity measurement shall be obtained
     * @return
     *      pair of words enriched by their similarity measurement
     */
    public MeasureSimilarityResult measureSimilarity(final MeasureSimilarityRequest request) {
        if (!modelProvider.hasWord(request.getWord1())) {
            throw new UnknownTermException(request.getWord1());
        }
        if (!modelProvider.hasWord(request.getWord2())) {
            throw new UnknownTermException(request.getWord2());
        }
        final double similarity = modelProvider.similarity(request.getWord1(), request.getWord2());
        return new MeasureSimilarityResult(request, similarity);
    }

    /**
     * Collects all available files that may represent word2vec models and returns them to the caller.
     *
     * @return
     *      list of all available models
     */
    public AvailableModelsResult availableModels() {
        return new AvailableModelsResult(availableModels.stream()
                .map(model -> new AvailableModel(model.getFilename(), model.isActive()))
                .collect(Collectors.toList()));
    }

    /**
     * Activates (loads) the requested model. Deactivates (unloads) the model that is currently in use (if any).
     *
     * @param request
     *      contains information about the model that ought to be activated
     * @return
     *      status information on which model got activated and which model got deactivated (if any)
     */
    public ActivateModelResult activate(final ActivateModelRequest request) {
        final ModelMetadata availableModel = availableModels.stream()
                .filter(m -> m.getFilename().equals(request.getFilename()))
                .findFirst()
                .orElseThrow(() -> new UnavailableModelException(request.getFilename()));
        final Optional<ModelMetadata> removedModel = Optional.ofNullable(modelProvider.getCurrentModel());
        modelProvider.load(availableModel);
        availableModel.activate();
        removedModel.ifPresent(ModelMetadata::deactivate);
        return new ActivateModelResult(availableModel.getFilename(), removedModel.map(ModelMetadata::getFilename).orElse("<<undefined>>"));
    }
}
