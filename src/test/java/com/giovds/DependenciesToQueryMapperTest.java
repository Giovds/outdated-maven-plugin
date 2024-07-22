package com.giovds;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DependenciesToQueryMapperTest {

    @Test
    void should_return_lucene_query_based_on_given_dependencies() {
        final var luceneQuery = "(g:com.giovds AND a:foo-artifact AND v:1.0.0) OR (g:org.apache.maven AND a:bar-artifact AND v:1.2.3)";
        final var expectedUriQuery = luceneQuery.replace(' ', '+');
        final var firstDep = createDependency("com.giovds", "foo-artifact", "1.0.0");
        final var secondDep = createDependency("org.apache.maven", "bar-artifact", "1.2.3");

        final List<String> actualQuery = DependenciesToQueryMapper.mapToQueries(2000, List.of(firstDep, secondDep));

        assertThat(actualQuery).hasSize(1)
                .contains(expectedUriQuery);
    }

    @Test
    void should_return_multiple_lucene_query_based_on_given_dependencies_when_exceeding_limit() {
        final var firstDep = createDependency("com.giovds", "foo-artifact", "1.0.0");
        final var firstExpectedQuery = "(g:com.giovds AND a:foo-artifact AND v:1.0.0)";
        final var secondDep = createDependency("org.apache.maven", "bar-artifact", "1.2.3");
        final var secondExpectedQuery = "(g:org.apache.maven AND a:bar-artifact AND v:1.2.3)";

        final List<String> actualQuery = DependenciesToQueryMapper.mapToQueries(50, List.of(firstDep, secondDep));

        assertThat(actualQuery).hasSize(2)
                .contains(firstExpectedQuery.replace(' ', '+'),
                        secondExpectedQuery.replace(' ', '+'));
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
        final var dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }
}