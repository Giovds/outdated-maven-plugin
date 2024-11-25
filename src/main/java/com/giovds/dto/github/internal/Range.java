package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;
import java.util.List;

public record Range(OffsetDateTime start, OffsetDateTime end, List<Point> points) {
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
            return new Range(start, end, points);
        }
    }
}
