package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;
import java.util.List;

public class Range {
    private OffsetDateTime start;
    private OffsetDateTime end;
    private List<Point> points;

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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                ", points=" + points +
                '}';
    }

    public static RangeBuilder builder() {
        return new RangeBuilder();
    }

    public static class RangeBuilder {
        private OffsetDateTime start;
        private OffsetDateTime end;
        private List<Point> points;

        RangeBuilder() {
        }

        public RangeBuilder start(OffsetDateTime start) {
            this.start = start;
            return this;
        }

        public RangeBuilder end(OffsetDateTime end) {
            this.end = end;
            return this;
        }

        public RangeBuilder points(List<Point> points) {
            this.points = points;
            return this;
        }

        public Range build() {
            Range range = new Range();
            range.setStart(this.start);
            range.setEnd(this.end);
            range.setPoints(this.points);
            return range;
        }

        @Override
        public String toString() {
            return "Range.RangeBuilder{" +
                    "start=" + start +
                    ", end=" + end +
                    ", points=" + points +
                    '}';
        }
    }
}
