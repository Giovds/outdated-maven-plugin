package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public record Point(OffsetDateTime date, int total) {
    public static PointBuilder builder() {
        return new PointBuilder();
    }

    public static class PointBuilder {
        private OffsetDateTime date;
        private int total;

        PointBuilder() {
        }

        public PointBuilder date(OffsetDateTime date) {
            this.date = date;
            return this;
        }

        public PointBuilder total(int total) {
            this.total = total;
            return this;
        }

        public Point build() {
            return new Point(date, total);
        }
    }
}
