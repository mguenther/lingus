package net.mguenther.lingus.wordembedding.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ModelMetadata {

    private final String filename;
    private final String locationOnFS;
    private boolean active;

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }
}
