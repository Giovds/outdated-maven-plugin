package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public record RangeSummary(
        OffsetDateTime start,
        OffsetDateTime end,
        int count
) {
    public static RangeSummaryBuilder builder() {
        return new RangeSummaryBuilder();
    }

    public static class RangeSummaryBuilder {
        private OffsetDateTime start;
        private OffsetDateTime end;
        private int count;

        RangeSummaryBuilder() {
        }

        public RangeSummaryBuilder start(OffsetDateTime start) {
            this.start = start;
            return this;
        }

        public RangeSummaryBuilder end(OffsetDateTime end) {
            this.end = end;
            return this;
        }

        public RangeSummaryBuilder count(int count) {
            this.count = count;
            return this;
        }

        public RangeSummary build() {
            return new RangeSummary(start, end, count);
        }

        @Override
        public String toString() {
            return "RangeSummary.RangeSummaryBuilder{" +
                    "start=" + start +
                    ", end=" + end +
                    ", count=" + count +
                    '}';
        }
    }
}
