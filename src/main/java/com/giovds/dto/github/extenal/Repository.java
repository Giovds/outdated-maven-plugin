package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    @JsonProperty("contributors_url")
    private String contributorsUrl = "";

    @JsonProperty("forks_count")
    private int forksCount;

    @JsonProperty("full_name")
    private String fullName = "";

    @JsonProperty("has_issues")
    private boolean hasIssues;

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("id")
    private long id;

    @JsonProperty("node_id")
    private String nodeId = "";

    @JsonProperty("name")
    private String name = "";

    @JsonProperty("owner")
    private Owner owner = new Owner();

    @JsonProperty("private")
    private boolean _private;

    @JsonProperty("stargazers_count")
    private int stargazersCount;

    @JsonProperty("subscribers_count")
    private int subscribersCount;

    @JsonProperty("url")
    private String url = "";

    // Getters and Setters
    public String getContributorsUrl() {
        return contributorsUrl;
    }

    public void setContributorsUrl(String contributorsUrl) {
        this.contributorsUrl = contributorsUrl;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isPrivate() {
        return _private;
    }

    public void setPrivate(boolean _private) {
        this._private = _private;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public int getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(int subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
