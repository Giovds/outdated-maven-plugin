package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public record Bucket(OffsetDateTime start, OffsetDateTime end) {
    public static BucketBuilder builder() {
        return new BucketBuilder();
    }

    public static class BucketBuilder {
        private OffsetDateTime start;
        private OffsetDateTime end;

        BucketBuilder() {
        }

        public BucketBuilder start(OffsetDateTime start) {
            this.start = start;
            return this;
        }

        public BucketBuilder end(OffsetDateTime end) {
            this.end = end;
            return this;
        }

        public Bucket build() {
            return new Bucket(start, end);
        }
    }
}
