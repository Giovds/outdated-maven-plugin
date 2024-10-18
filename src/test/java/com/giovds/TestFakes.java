package com.giovds;

import com.giovds.dto.DependencyResponse;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TestFakes {
    public static Dependency createDependency() {
        return createDependency("com.giovds", "test-example", "1.0.0");
    }

    public static Dependency createDependency(String groupId, String artifactId, String version) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);

        return dependency;
    }

    public static MavenProject createProjectWithDependency(Dependency... dependencies) {
        final MavenProject project = new MavenProject();
        project.setDependencies(List.of(dependencies));
        return project;
    }

    public static void mockClientResponseForDependency(QueryClient client, Dependency dependency, LocalDate date) throws MojoExecutionException {
        var epochMilliseconds = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        var response = new DependencyResponse(createDependencyKey(dependency), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), epochMilliseconds);
        when(client.search(any())).thenReturn(Set.of(response));
    }

    private static String createDependencyKey(final Dependency dependency) {
        return String.format("%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    }
}
