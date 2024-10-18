package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitActivity {
    /**
     * The total number of commits for the week.
     */
    @JsonProperty("total")
    private int total;

    /**
     * The start of the week as a UNIX timestamp.
     */
    @JsonProperty("week")
    private long week;

    /**
     * The number of commits for each day of the week. 0 = Sunday, 1 = Monday, etc.
     */
    @JsonProperty("days")
    private final List<Integer> days = List.of();

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getWeek() {
        return week;
    }

    public void setWeek(long week) {
        this.week = week;
    }

    public List<Integer> getDays() {
        return days;
    }
}
