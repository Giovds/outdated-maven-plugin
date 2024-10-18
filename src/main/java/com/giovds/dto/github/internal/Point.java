package com.giovds.dto.github.internal;

import java.time.OffsetDateTime;

public class Point {
    private OffsetDateTime date;
    private int total;

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Point{" +
                "date=" + date +
                ", total=" + total +
                '}';
    }

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
            Point point = new Point();
            point.setDate(this.date);
            point.setTotal(this.total);
            return point;
        }

        @Override
        public String toString() {
            return "Point.PointBuilder{" +
                    "date=" + date +
                    ", total=" + total +
                    '}';
        }
    }
}
