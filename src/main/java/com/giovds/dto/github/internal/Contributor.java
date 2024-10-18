package com.giovds.dto.github.internal;

public class Contributor {
    private String username;
    private int commitsCount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCommitsCount() {
        return commitsCount;
    }

    public void setCommitsCount(int commitsCount) {
        this.commitsCount = commitsCount;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "username='" + username + '\'' +
                ", commitsCount=" + commitsCount +
                '}';
    }

    public static ContributorBuilder builder() {
        return new ContributorBuilder();
    }

    public static class ContributorBuilder {
        private String username;
        private int commitsCount;

        ContributorBuilder() {
        }

        public ContributorBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ContributorBuilder commitsCount(int commitsCount) {
            this.commitsCount = commitsCount;
            return this;
        }

        public Contributor build() {
            Contributor contributor = new Contributor();
            contributor.setUsername(this.username);
            contributor.setCommitsCount(this.commitsCount);
            return contributor;
        }

        @Override
        public String toString() {
            return "Contributor.ContributorBuilder{" +
                    "username='" + username + '\'' +
                    ", commitsCount=" + commitsCount +
                    '}';
        }
    }
}
