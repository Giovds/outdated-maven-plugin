package com.giovds;

import org.apache.maven.model.Dependency;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Converts Maven {@link Dependency} to a single Lucene query that can be sent to Maven Central.
 * This in order to not spam the endpoint with a request per dependency.
 */
public class DependenciesToQueryMapper {
    private DependenciesToQueryMapper() {
    }

    /**
     * Create a <a href="https://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene</a> query from
     * a collection of {@link org.apache.maven.project.MavenProject} {@link Dependency}
     *
     * @param dependencies A collection of Maven {@link Dependency} to convert to query params
     * @return A Lucene query as {@link String} with spaces escaped by '+'
     */
    public static String map(final Collection<Dependency> dependencies) {
        return dependencies
                .stream()
                .map(GroupArtifactIdMapper::toQueryString)
                .collect(Collectors.joining("+OR+"));
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
