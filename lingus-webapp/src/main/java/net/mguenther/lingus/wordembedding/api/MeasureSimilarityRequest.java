package net.mguenther.lingus.wordembedding.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class MeasureSimilarityRequest {

    private final String word1;
    private final String word2;
}
