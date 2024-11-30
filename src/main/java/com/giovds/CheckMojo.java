package com.giovds;

import com.giovds.dto.DependencyResponse;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.giovds.dto.DependencyResponse.getDateTime;

/**
 * This plugin goal determines if the project dependencies are no longer actively maintained
 * based on a user-defined threshold of inactivity in years.
 */
@Mojo(name = "check",
        defaultPhase = LifecyclePhase.TEST_COMPILE,
        requiresOnline = true,
        requiresProject = true,
        requiresDependencyResolution = ResolutionScope.TEST)
public class CheckMojo extends AbstractMojo {
    private QueryClient client = new QueryClient();

    /**
     * Required for initialization by Maven
     */
    public CheckMojo() {
    }

    /**
     * Visible for testing purposes
     */
    CheckMojo(final QueryClient client) {
        this.client = client;
    }

    @Parameter(property = "shouldFailBuild")
    private boolean shouldFailBuild = false;

    @Parameter(property = "years")
    private int years = 1;

    @Parameter(property = "includePlugins")
    private boolean includePlugins = false;

    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Set<Dependency> dependenciesToConsider = new HashSet<>(project.getDependencies());
        if (includePlugins) {
            dependenciesToConsider.addAll(mapArtifactsToDependencies(project.getPluginArtifacts()));
        }

        if (dependenciesToConsider.isEmpty()) {
            // When building a POM without any dependencies or plugins there will be nothing to query.
            return;
        }

        final List<String> queryForAllDependencies = DependenciesToQueryMapper.mapToQueries(dependenciesToConsider);

        final Set<DependencyResponse> result = client.search(queryForAllDependencies);

        final Collection<DependencyResponse> outdatedDependencies = new ArrayList<>();
        final Collection<DependencyResponse> outdatedPlugins = new ArrayList<>();
        for (final Dependency currentDependency : dependenciesToConsider) {
            result.stream()
                    .filter(dep -> currentDependency.getGroupId().equals(dep.g()) && currentDependency.getArtifactId().equals(dep.a()))
                    .filter(dep -> getDateTime(dep.timestamp()).isBefore(LocalDate.now().minusYears(years)))
                    .findAny()
                    .ifPresent(dep -> {
                        if (dep.isPlugin()) {
                            outdatedPlugins.add(dep);
                        } else {
                            outdatedDependencies.add(dep);
                        }
                    });
        }

        if (!outdatedDependencies.isEmpty()) {
            outdatedDependencies.forEach(this::logWarning);
        }

        if (!outdatedPlugins.isEmpty()) {
            outdatedPlugins.forEach(this::logWarning);
        }

        if (shouldFailBuild && (!outdatedDependencies.isEmpty() || !outdatedPlugins.isEmpty())) {
            throw new MojoFailureException("There are dependencies or plugins that are outdated.");
        }
    }

    private static Set<Dependency> mapArtifactsToDependencies(final Set<Artifact> artifacts) {
        final Set<Dependency> dependencies = new HashSet<>();
        for (Artifact artifact : artifacts) {
            dependencies.add(mapArtifactToDependency(artifact));
        }
        return dependencies;
    }

    private static Dependency mapArtifactToDependency(final Artifact artifact) {
        final Dependency dependency = new Dependency();
        dependency.setGroupId(artifact.getGroupId());
        dependency.setArtifactId(artifact.getArtifactId());
        dependency.setVersion(artifact.getVersion());
        return dependency;
    }

    private void logWarning(final DependencyResponse dep) {
        final String message =
                String.format("%s '%s' has not received an update since version '%s' was last uploaded '%s'.",
                        dep.isPlugin() ? "Plugin" : "Dependency", dep.id(), dep.v(), getDateTime(dep.timestamp()));
        if (shouldFailBuild) {
            getLog().error(message);
        } else {
            getLog().warn(message);
        }
    }

    void setProject(final MavenProject project) {
        this.project = project;
    }

    void setShouldFailBuild(final boolean shouldFailBuild) {
        this.shouldFailBuild = shouldFailBuild;
    }

    void setIncludePlugins(final boolean includePlugins) {
        this.includePlugins = includePlugins;
    }
}
