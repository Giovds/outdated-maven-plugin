package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitActivity(
        @JsonProperty("days") List<Integer> days,
        @JsonProperty("total") int total,
        @JsonProperty("week") long week
) {
}
