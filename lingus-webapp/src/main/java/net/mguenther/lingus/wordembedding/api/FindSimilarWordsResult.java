package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
public class FindSimilarWordsResult {

    @JsonProperty("terms")
    private Set<Term> terms;

    @JsonCreator
    public FindSimilarWordsResult(@JsonProperty("terms") final Set<Term> terms) {
        this.terms = terms;
    }
}
