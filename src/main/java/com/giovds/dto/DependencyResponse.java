package com.giovds.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * The dependency returned by the Maven Central response
 *
 * @param id        GAV (groupId:artifactId:version)
 * @param g         groupId
 * @param a         artifactId
 * @param v         version
 * @param timestamp The date this GAV id was released to Maven Central
 * @param p         packaging type
 */
public record DependencyResponse(String id, String g, String a, String v, long timestamp, String p) {
    /**
     * Return the timestamp as {@link LocalDate}
     *
     * @param timestamp the timestamp to convert
     * @return the {@link LocalDate}
     */
    public static LocalDate getDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Check if the dependency is a plugin
     *
     * @return true if the packaging type is "maven-plugin"
     */
    public boolean isPlugin() {
        return "maven-plugin".equals(p);
    }
}
