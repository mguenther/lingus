package net.mguenther.lingus.wordembedding.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FindSimilarWordsRequest {

    public static final int DEFAULT_SUGGESTIONS_PER_TERM = 5;

    public static final int MIN_SUGGESTIONS_PER_TERM = 1;

    public static final int MAX_SUGGESTIONS_PER_TERM = 10;

    public static class FindSimilarWordsRequestBuilder {

        private final Set<String> terms = new HashSet<>();

        private int suggestionsPerTerm = DEFAULT_SUGGESTIONS_PER_TERM;

        public FindSimilarWordsRequestBuilder forTerm(final String term) {
            this.terms.add(term);
            return this;
        }

        public FindSimilarWordsRequestBuilder forTerm(final Set<String> terms) {
            this.terms.addAll(terms);
            return this;
        }

        public FindSimilarWordsRequestBuilder limit(final int suggestionsPerTerm) {
            if (suggestionsPerTerm < MIN_SUGGESTIONS_PER_TERM || suggestionsPerTerm > MAX_SUGGESTIONS_PER_TERM) {
                this.suggestionsPerTerm = DEFAULT_SUGGESTIONS_PER_TERM;
            } else {
                this.suggestionsPerTerm = suggestionsPerTerm;
            }
            return this;
        }

        public FindSimilarWordsRequest build() {
            return new FindSimilarWordsRequest(terms, suggestionsPerTerm);
        }
    }

    private final Set<String> terms;

    private final int suggestionsPerTerm;

    public static FindSimilarWordsRequestBuilder create() {
        return new FindSimilarWordsRequestBuilder();
    }
}
