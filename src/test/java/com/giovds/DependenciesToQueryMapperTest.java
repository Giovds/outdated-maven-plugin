package com.giovds;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DependenciesToQueryMapperTest {

    @Test
    void should_return_lucene_query_based_on_given_dependencies() {
        final var luceneQuery = "(g:com.giovds AND a:foo-artifact AND v:1.0.0) OR (g:org.apache.maven AND a:bar-artifact AND v:1.2.3)";
        final var expectedUriQuery = luceneQuery.replace(' ', '+');
        final var firstDep = createDependency("com.giovds", "foo-artifact", "1.0.0");
        final var secondDep = createDependency("org.apache.maven", "bar-artifact", "1.2.3");

        final String actualQuery = DependenciesToQueryMapper.map(List.of(firstDep, secondDep));

        assertThat(actualQuery).isEqualTo(expectedUriQuery);
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
        final var dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }
}