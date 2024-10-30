package com.giovds.dto;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

/**
 * The dependency returned by the Maven Central response
 *
 * @param id        GAV (groupId:artifactId:version)
 * @param g         groupId
 * @param a         artifactId
 * @param v         version
 * @param timestamp The date this GAV id was released to Maven Central
 */
public record DependencyResponse(String id, String g, String a, String v, long timestamp) {

    /**
     * Map the response from Maven Central to a {@link DependencyResponse}
     *
     * @param doc The response from Maven Central
     * @return A {@link DependencyResponse} object
     */
    public static DependencyResponse mapResponseToDependency(final Map<String, Object> doc) {

        try {
            DependencyResponse response = JSON.std.beanFrom(DependencyResponse.class, JSON.std.asString(doc));
            return response;
        } catch (IOException e) {
            String errorMessage = "Error mapping response: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }

    }

    /**
     * Return the timestamp as {@link LocalDate}
     *
     * @param timestamp the timestamp to convert
     * @return the {@link LocalDate}
     */
    public static LocalDate getDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
