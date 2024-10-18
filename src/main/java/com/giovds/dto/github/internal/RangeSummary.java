package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public class RangeSummary {
    private OffsetDateTime start;
    private OffsetDateTime end;
    private int count;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RangeSummary{" +
                "start=" + start +
                ", end=" + end +
                ", count=" + count +
                '}';
    }

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
            RangeSummary rangeSummary = new RangeSummary();
            rangeSummary.setStart(this.start);
            rangeSummary.setEnd(this.end);
            rangeSummary.setCount(this.count);
            return rangeSummary;
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
