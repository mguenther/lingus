package net.mguenther.lingus.wordembedding.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AvailableModel {

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("active")
    private boolean active;

    @JsonCreator
    public AvailableModel(@JsonProperty("filename") final String filename,
                          @JsonProperty("active") final boolean active) {
        this.filename = filename;
        this.active = active;
    }
}
