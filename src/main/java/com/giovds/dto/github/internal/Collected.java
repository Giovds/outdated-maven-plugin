package com.giovds.dto.github.internal;

import java.util.List;

public record Collected(
        String homepage,
        int starsCount,
        int forksCount,
        int subscribersCount,
        int issues,
        List<Contributor> contributors,
        List<RangeSummary> commits
) {
    public static CollectedBuilder builder() {
        return new CollectedBuilder();
    }

    public static class CollectedBuilder {
        private String homepage;
        private int starsCount;
        private int forksCount;
        private int subscribersCount;
        private int issues;
        private List<Contributor> contributors;
        private List<RangeSummary> commits;

        CollectedBuilder() {
        }

        public CollectedBuilder homepage(String homepage) {
            this.homepage = homepage;
            return this;
        }

        public CollectedBuilder starsCount(int starsCount) {
            this.starsCount = starsCount;
            return this;
        }

        public CollectedBuilder forksCount(int forksCount) {
            this.forksCount = forksCount;
            return this;
        }

        public CollectedBuilder subscribersCount(int subscribersCount) {
            this.subscribersCount = subscribersCount;
            return this;
        }

        public CollectedBuilder issues(int issues) {
            this.issues = issues;
            return this;
        }

        public CollectedBuilder contributors(List<Contributor> contributors) {
            this.contributors = contributors;
            return this;
        }

        public CollectedBuilder commits(List<RangeSummary> commits) {
            this.commits = commits;
            return this;
        }

        public Collected build() {
            return new Collected(homepage, starsCount, forksCount, subscribersCount, issues, contributors, commits);
        }
    }
}
