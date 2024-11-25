package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Owner(
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("email") String email,
        @JsonProperty("events_url") String eventsUrl,
        @JsonProperty("followers_url") String followersUrl,
        @JsonProperty("following_url") String followingUrl,
        @JsonProperty("gists_url") String gistsUrl,
        @JsonProperty("gravatar_id") String gravatarId,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("id") long id,
        @JsonProperty("login") String login,
        @JsonProperty("name") String name,
        @JsonProperty("node_id") String nodeId,
        @JsonProperty("organizations_url") String organizationsUrl,
        @JsonProperty("received_events_url") String receivedEventsUrl,
        @JsonProperty("repos_url") String reposUrl,
        @JsonProperty("site_admin") boolean siteAdmin,
        @JsonProperty("starred_at") OffsetDateTime starredAt,
        @JsonProperty("starred_url") String starredUrl,
        @JsonProperty("subscriptions_url") String subscriptionsUrl,
        @JsonProperty("type") String type,
        @JsonProperty("url") String url
) {
}
