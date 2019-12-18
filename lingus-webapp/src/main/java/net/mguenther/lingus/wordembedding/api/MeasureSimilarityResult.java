package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@JsonPropertyOrder({"word1", "word2", "similarity"})
public class MeasureSimilarityResult {

    @JsonProperty("word1")
    private final String word1;

    @JsonProperty("word2")
    private final String word2;

    @JsonProperty("similarity")
    private final double similarity;

    public MeasureSimilarityResult(final MeasureSimilarityRequest request, final double similarity) {
        this(request.getWord1(), request.getWord2(), similarity);
    }
}
