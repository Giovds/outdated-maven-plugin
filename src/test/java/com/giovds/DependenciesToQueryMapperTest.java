package com.giovds;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DependenciesToQueryMapperTest {

    @Test
    void should_return_lucene_query_based_on_given_dependencies() {
        final var luceneQuery = "(g:com.giovds AND a:foo-artifact AND v:1.0.0) OR (g:org.apache.maven AND a:bar-artifact AND v:1.2.3)";
        final var expectedUriQuery = luceneQuery.replace(' ', '+');
        final var firstDep = createDependency("com.giovds", "foo-artifact", "1.0.0");
        final var secondDep = createDependency("org.apache.maven", "bar-artifact", "1.2.3");

        final List<String> actualQuery = DependenciesToQueryMapper.mapToQueries(List.of(firstDep, secondDep));

        assertThat(actualQuery).hasSize(1)
                .contains(expectedUriQuery);
    }

    @Test
    void should_return_multiple_lucene_query_based_on_given_dependencies_when_exceeding_limit() {
        final var firstDep = createDependency("com.giovds", "foo-artifact", "1.0.0");
        final var otherDeps = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> String.format("bar-artifact-%d", i))
                .map(artifactId -> createDependency("org.apache.maven", artifactId, "1.2.3"))
                .collect(Collectors.toSet());

        final var allDependencies = new ArrayList<Dependency>();
        allDependencies.add(firstDep);
        allDependencies.addAll(otherDeps);

        final List<String> actualQueries = DependenciesToQueryMapper.mapToQueries(allDependencies);

        assertThat(actualQueries).hasSize(2).allMatch(query -> query.endsWith(")"));
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
        final var dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }
}