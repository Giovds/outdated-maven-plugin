package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public class Bucket {
    private OffsetDateTime start;
    private OffsetDateTime end;

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

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

        public String toString() {
            return "Bucket.BucketBuilder(start=" + this.start + ", end=" + this.end + ")";
        }
    }

    public Bucket(OffsetDateTime start, OffsetDateTime end) {
        this.start = start;
        this.end = end;
    }
}
