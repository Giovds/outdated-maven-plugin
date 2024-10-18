package com.giovds;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.giovds.TestFakes.createProjectWithDependencies;
import static com.giovds.TestFakes.createDependency;
import static com.giovds.TestFakes.mockClientResponseForDependency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AverageAgeMojoTest {
    private final QueryClient client = mock(QueryClient.class);
    private final AverageAgeMojo mojo = new AverageAgeMojo(client);

    @Test
    void should_not_perform_query_when_no_dependencies() throws MojoExecutionException {
        // Arrange
        var project = createProjectWithDependencies();

        var logger = new TestLogger();

        // Act
        mojo.setLog(logger);
        mojo.setProject(project);
        mojo.execute();

        // Assert
        assertThat(logger.getInfoLogs()).containsExactly("No dependencies found");
    }

    @Test
    void should_not_consider_dependencies_with_same_groupId() throws MojoExecutionException {
        // Arrange
        var dependency1 = createDependency("com.external", "external-library", "1.0.0");
        var dependency2 = createDependency("com.giovds", "test-library", "1.0.0");
        var project = createProjectWithDependencies("pom", "com.giovds", "test-app", "1.0.0", dependency1, dependency2);
        mockClientResponseForDependency(client, dependency1, LocalDate.now().minusYears(1));

        var logger = new TestLogger();

        // Act
        mojo.setLog(logger);
        mojo.setProject(project);
        mojo.execute();

        // Assert
        assertThat(logger.getInfoLogs()).contains(
                "Dependency com.external:external-library:1.0.0 is 365 days old",
                "Total age: 365 days",
                "Average age: 365 days"
        );
    }
}
