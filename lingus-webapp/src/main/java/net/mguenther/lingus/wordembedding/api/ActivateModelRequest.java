package net.mguenther.lingus.wordembedding.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ActivateModelRequest {

    private final String filename;
}
