package com.giovds;

import org.apache.maven.model.Dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts Maven {@link Dependency} to a single Lucene query that can be sent to Maven Central.
 * This in order to not spam the endpoint with a request per dependency.
 */
public class DependenciesToQueryMapper {
    private static final int maxItemsPerQuery = 20;
    private static final String JOIN_CHARS = "+OR+";

    private DependenciesToQueryMapper() {
    }

    /**
     * Creates multiple <a href="https://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene</a> queries from
     * a collection of {@link org.apache.maven.project.MavenProject} {@link Dependency}
     *
     * @param dependencies A collection of Maven {@link Dependency} to convert to query params
     * @return A {@link List} of Lucene queries of {@link String} with spaces escaped by '+'
     */
    public static List<String> mapToQueries(final Collection<Dependency> dependencies) {
        final List<String> result = new ArrayList<>();

        int itemsInQuery = 0;
        StringBuilder query = new StringBuilder();
        for (Dependency dependency : dependencies) {
            final String nextQueryParam = GroupArtifactIdMapper.toQueryString(dependency);
            query.append(nextQueryParam);
            itemsInQuery++;

            if (itemsInQuery == maxItemsPerQuery) {
                result.add(query.toString());
                itemsInQuery = 0;
                query = new StringBuilder();
            } else {
                query.append(JOIN_CHARS);
            }
        }

        removeTrailingJoin(query);
        if (!query.isEmpty()) {
            result.add(query.toString());
        }

        return result;
    }

    private static void removeTrailingJoin(final StringBuilder query) {
        if (query.length() > JOIN_CHARS.length()) {
            query.setLength(query.length() - JOIN_CHARS.length());
        }
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
