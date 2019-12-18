package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ActivateModelResult {

    @JsonProperty("activated")
    private String currentModel;

    @JsonProperty("deactivated")
    private String removedModel;

    public ActivateModelResult(@JsonProperty("activated") final String currentModel,
                               @JsonProperty("deactivated") final String removedModel) {
        this.currentModel = currentModel;
        this.removedModel = removedModel;
    }
}
