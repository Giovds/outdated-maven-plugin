package com.giovds;

import com.giovds.dto.DependencyResponse;
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
import java.util.List;
import java.util.Set;

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

    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<Dependency> dependencies = project.getDependencies();

        if (dependencies.isEmpty()) {
            // When building a POM without any dependencies there will be nothing to query.
            return;
        }

        final List<String> queryForAllDependencies = DependenciesToQueryMapper.mapToQueries(dependencies);

        final Set<DependencyResponse> result = client.search(queryForAllDependencies);

        final Collection<DependencyResponse> outdatedDependencies = new ArrayList<>();
        for (final Dependency currentDependency : project.getDependencies()) {
            result.stream()
                    .filter(dep -> currentDependency.getGroupId().equals(dep.g()) && currentDependency.getArtifactId().equals(dep.a()))
                    .filter(dep -> dep.getDateTime().isBefore(LocalDate.now().minusYears(years)))
                    .findAny()
                    .ifPresent(outdatedDependencies::add);
        }

        if (!outdatedDependencies.isEmpty()) {
            outdatedDependencies.forEach(this::logWarning);
        }

        if (shouldFailBuild && !outdatedDependencies.isEmpty()) {
            throw new MojoFailureException("There are dependencies that are outdated.");
        }
    }

    private void logWarning(final DependencyResponse dep) {
        final String message =
                String.format("Dependency '%s' has not received an update since version '%s' was last uploaded '%s'.",
                        dep.id(), dep.v(), dep.timestamp());
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
}
