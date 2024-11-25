package com.giovds.dto.github.internal;

public record Contributor(String username, int commitsCount) {
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
            return new Contributor(username, commitsCount);
        }
    }
}
