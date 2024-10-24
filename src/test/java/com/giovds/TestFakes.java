package com.giovds;

import com.giovds.dto.DependencyResponse;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        dependency.setScope("compile");

        return dependency;
    }

    public static Artifact createPlugin(String groupId, String artifactId, String version) {
        return new DefaultArtifact(
                groupId,
                artifactId,
                version,
                "build",
                "maven-plugin",
                null,
                new DefaultArtifactHandler("maven-plugin")
        );
    }

    public static MavenProject createProjectWithDependencies(String groupId, String artifactId, String version, Dependency... dependencies) {
        final MavenProject project = new MavenProject();
        project.setPackaging("jar");
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setVersion(version);
        project.setDependencies(List.of(dependencies));
        // Translate dependencies into (resolved) artifacts
        project.setArtifacts(dependenciesToArtifacts(project.getDependencies()));
        return project;
    }

    public static MavenProject createProjectWithPlugins(String groupId, String artifactId, String version, Artifact... plugins) {
        final MavenProject project = new MavenProject();
        project.setPackaging("jar");
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setVersion(version);
        project.setPluginArtifacts(Set.of(plugins));
        return project;
    }

    private static Set<Artifact> dependenciesToArtifacts(List<Dependency> dependencies) {
        return dependencies.stream()
                .map(dependency -> new DefaultArtifact(
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getVersion(),
                        dependency.getScope(),
                        dependency.getType(),
                        dependency.getClassifier(),
                        new DefaultArtifactHandler(dependency.getType())
                ))
                .collect(Collectors.toSet());
    }

    public static MavenProject createProjectWithDependencies(Dependency... dependencies) {
        return createProjectWithDependencies("com.giovds", "test-project", "1.0.0", dependencies);
    }

    public static void mockClientResponseForDependency(QueryClient client, Dependency dependency, LocalDate date) throws MojoExecutionException {
        var epochMilliseconds = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        var response = new DependencyResponse(createDependencyKey(dependency), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), epochMilliseconds, "jar");
        when(client.search(any())).thenReturn(Set.of(response));
    }

    public static void mockClientResponseForPlugin(QueryClient client, Artifact plugin, LocalDate date) throws MojoExecutionException {
        var epochMilliseconds = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        var response = new DependencyResponse(createPluginKey(plugin), plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(), epochMilliseconds, "maven-plugin");
        when(client.search(any())).thenReturn(Set.of(response));
    }

    private static String createDependencyKey(final Dependency dependency) {
        return String.format("%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    }

    private static String createPluginKey(final Artifact plugin) {
        return String.format("%s:%s:%s", plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion());
    }
}
