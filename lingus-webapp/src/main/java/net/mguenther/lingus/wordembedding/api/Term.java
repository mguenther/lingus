package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonPropertyOrder({"term", "similar"})
public class Term {

    @JsonProperty("term")
    private String term;

    @JsonProperty("similar")
    private List<SimilarWord> similar;

    @JsonCreator
    public Term(@JsonProperty("term") final String term,
                @JsonProperty("similar") final List<SimilarWord> similar) {
        this.term = term;
        this.similar = similar;
    }
}
