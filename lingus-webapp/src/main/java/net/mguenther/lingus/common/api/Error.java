package net.mguenther.lingus.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class Error {

    @JsonProperty("errorMessage")
    private final String errorMessage;

    @JsonProperty("httpStatus")
    private final Integer httpStatus;
}
