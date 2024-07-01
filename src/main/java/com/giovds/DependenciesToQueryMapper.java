package com.giovds;

import org.apache.maven.model.Dependency;

import java.util.Collection;
import java.util.stream.Collectors;

public class DependenciesToQueryMapper {
    /**
     * Create a <a href="https://lucene.apache.org/core/2_9_4/queryparsersyntax.html">Lucene</a> query from
     * a collection of {@link org.apache.maven.project.MavenProject} {@link Dependency}
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
