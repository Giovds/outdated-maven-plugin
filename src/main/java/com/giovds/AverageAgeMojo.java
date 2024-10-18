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
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "average",
        defaultPhase = LifecyclePhase.VERIFY,
        requiresOnline = true,
        requiresProject = true,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class AverageAgeMojo extends AbstractMojo {
    private QueryClient client = new QueryClient();

    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    private final LocalDate today = LocalDate.now();

    @Override
    public void execute() throws MojoExecutionException {
        var isAggregatorProject = "pom".equals(project.getPackaging());
        if (isAggregatorProject) {
            getLog().info("Detected aggregator project");
        }

        final Set<Dependency> dependenciesToConsider = project.getArtifacts().stream()
                .peek(artifact -> debug("Found artifact %s", artifact.getId()))
                .filter(this::isCompileTimeDependency)
                .peek(artifact -> debug("Found compile-time artifact %s", artifact.getId()))
                .filter(artifact -> !isAggregatorProject || isDependencyOutsideProject(artifact))
                .peek(artifact -> info("Found compile-time artifact outside this project %s", artifact.getId()))
                .map(this::convertToDependency)
                .collect(Collectors.toSet());

        if (dependenciesToConsider.isEmpty()) {
            info("No dependencies found");
            return;
        }

        final List<String> queries = DependenciesToQueryMapper.mapToQueries(dependenciesToConsider);
        debug("Queries: %s", queries);

        final Set<DependencyResponse> results = client.search(queries);
        debug("Results: %s", results);

        final var stats = results.stream()
                .sorted(Comparator.comparing(DependencyResponse::id))
                .mapToLong(dependency -> {
                    final long ageInDays = this.calculateDependencyAge(dependency);
                    info("Dependency %s is %d days old", dependency.id(), ageInDays);
                    return ageInDays;
                })
                .summaryStatistics();

        var numberOfDependencies = dependenciesToConsider.size();
        info("Found %d dependencies", numberOfDependencies);
        info("Total age: %d days", stats.getSum());
        info("Average age: %d days", (int) stats.getAverage());
    }

    private Dependency convertToDependency(Artifact artifact) {
        var result = new Dependency();
        result.setGroupId(artifact.getGroupId());
        result.setArtifactId(artifact.getArtifactId());
        result.setVersion(artifact.getVersion());
        result.setClassifier(artifact.getClassifier());
        result.setType(artifact.getType());
        return result;
    }


    private long calculateDependencyAge(DependencyResponse dependency) {
        var between = Period.between(dependency.getDateTime(), today);
        return 365L * between.getYears() + (between.getMonths() * 365L / 12L) + between.getDays();
    }

    private boolean isCompileTimeDependency(Artifact artifact) {
        return Artifact.SCOPE_COMPILE.equals(artifact.getScope())
                || Artifact.SCOPE_PROVIDED.equals(artifact.getScope())
                || Artifact.SCOPE_SYSTEM.equals(artifact.getScope());
    }

    private boolean isDependencyOutsideProject(Artifact artifact) {
        return !project.getGroupId().equals(artifact.getGroupId());
    }

    private void debug(String message, Object... args) {
        if (getLog().isDebugEnabled()) {
            getLog().debug(String.format(message, args));
        }
    }

    private void info(String message, Object... args) {
        if (getLog().isInfoEnabled()) {
            getLog().info(String.format(message, args));
        }
    }

    AverageAgeMojo(final QueryClient client) {
        this.client = client;
    }

    void setProject(final MavenProject project) {
        this.project = project;
    }
}