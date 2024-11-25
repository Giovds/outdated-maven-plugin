package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Repository(
        @JsonProperty("contributors_url") String contributorsUrl,
        @JsonProperty("forks_count") int forksCount,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("has_issues") boolean hasIssues,
        @JsonProperty("homepage") String homepage,
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("node_id") String nodeId,
        @JsonProperty("owner") Owner owner,
        @JsonProperty("private") boolean _private,
        @JsonProperty("stargazers_count") int stargazersCount,
        @JsonProperty("subscribers_count") int subscribersCount,
        @JsonProperty("url") String url
) {
}
