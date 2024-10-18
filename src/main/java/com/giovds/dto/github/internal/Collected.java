package com.giovds.dto.github.internal;

import java.util.List;

public class Collected {
    private String homepage;
    private int starsCount;
    private int forksCount;
    private int subscribersCount;
    private int issues;
    private List<Contributor> contributors;
    private List<RangeSummary> commits;

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(int starsCount) {
        this.starsCount = starsCount;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public int getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(int subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public int getIssues() {
        return issues;
    }

    public void setIssues(int issues) {
        this.issues = issues;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<RangeSummary> getCommits() {
        return commits;
    }

    public void setCommits(List<RangeSummary> commits) {
        this.commits = commits;
    }

    @Override
    public String toString() {
        return "Collected{" +
                "homepage='" + homepage + '\'' +
                ", starsCount=" + starsCount +
                ", forksCount=" + forksCount +
                ", subscribersCount=" + subscribersCount +
                ", issues=" + issues +
                ", contributors=" + contributors +
                ", commits=" + commits +
                '}';
    }

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
            Collected collected = new Collected();
            collected.setHomepage(this.homepage);
            collected.setStarsCount(this.starsCount);
            collected.setForksCount(this.forksCount);
            collected.setSubscribersCount(this.subscribersCount);
            collected.setIssues(this.issues);
            collected.setContributors(this.contributors);
            collected.setCommits(this.commits);
            return collected;
        }

        @Override
        public String toString() {
            return "Collected.CollectedBuilder{" +
                    "homepage='" + homepage + '\'' +
                    ", starsCount=" + starsCount +
                    ", forksCount=" + forksCount +
                    ", subscribersCount=" + subscribersCount +
                    ", issues=" + issues +
                    ", contributors=" + contributors +
                    ", commits=" + commits +
                    '}';
        }
    }
}
