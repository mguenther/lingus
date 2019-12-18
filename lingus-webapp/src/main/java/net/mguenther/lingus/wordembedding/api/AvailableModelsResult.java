package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class AvailableModelsResult {

    @JsonProperty("availableModels")
    private List<AvailableModel> availableModels;

    @JsonCreator
    public AvailableModelsResult(@JsonProperty("availableModels") final List<AvailableModel> availableModels) {
        this.availableModels = new ArrayList<>();
        this.availableModels.addAll(availableModels);
    }
}
