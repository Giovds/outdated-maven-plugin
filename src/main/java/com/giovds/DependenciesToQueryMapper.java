package com.giovds;

import org.apache.maven.model.Dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Maven {@link Dependency} to a single Lucene query that can be sent to Maven Central.
 * This in order to not spam the endpoint with a request per dependency.
 */
public class DependenciesToQueryMapper {
    private static final String JOIN_CHARS = "+OR+";

    private DependenciesToQueryMapper() {
    }

    /**
     * Creates multiple <a href="https://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene</a> queries from
     * a collection of {@link org.apache.maven.project.MavenProject} {@link Dependency}
     *
     * @param maxQueryLength The maximum allowed query length before adding a new query to the list
     * @param dependencies   A collection of Maven {@link Dependency} to convert to query params
     * @return A {@link List} of Lucene queries of {@link String} with spaces escaped by '+'
     */
    public static List<String> mapToQueries(final int maxQueryLength, final List<Dependency> dependencies) {
        final List<String> result = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        for (int i = 0; i < dependencies.size(); i++) {
            final Dependency dependency = dependencies.get(i);
            final String nextQueryParam = GroupArtifactIdMapper.toQueryString(dependency);

            if (isWithinMaxLength(maxQueryLength, query, nextQueryParam)) {
                query.append(nextQueryParam).append(JOIN_CHARS);
            } else {
                removeTrailingJoin(query);
                result.add(query.toString());
                query = new StringBuilder(nextQueryParam).append(JOIN_CHARS);
            }

            if (i == dependencies.size() - 1) {
                removeTrailingJoin(query);
                result.add(query.toString());
            }
        }

        return result;
    }

    private static void removeTrailingJoin(final StringBuilder query) {
        query.setLength(query.length() - JOIN_CHARS.length());
    }

    private static boolean isWithinMaxLength(final int maxQueryLength, final StringBuilder query, final String nextQueryParam) {
        return query.length() + nextQueryParam.length() + JOIN_CHARS.length() <= maxQueryLength;
    }

    private static class GroupArtifactIdMapper {
        private static String toQueryString(final String groupId, final String artifactId, final String version) {
            return String.format("(g:%s+AND+a:%s+AND+v:%s)", groupId, artifactId, version);
        }

        public static String toQueryString(final Dependency dependency) {
            return GroupArtifactIdMapper.toQueryString(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
        }
    }
}
