package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonPropertyOrder({"word", "similarity"})
public class SimilarWord {

    @JsonProperty("word")
    private String word;

    @JsonProperty("similarity")
    private double similarity;

    @JsonCreator
    public SimilarWord(@JsonProperty("word") final String word,
                       @JsonProperty("similarity") final double similarity) {
        this.word = word;
        this.similarity = similarity;
    }
}
