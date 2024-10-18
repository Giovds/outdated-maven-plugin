package com.giovds;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Collections;

import static com.giovds.TestFakes.createDependency;
import static com.giovds.TestFakes.createProjectWithDependencies;
import static com.giovds.TestFakes.mockClientResponseForDependency;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CheckMojoTest {

    @Mock
    private QueryClient client = mock(QueryClient.class);

    @InjectMocks
    private CheckMojo mojo = new CheckMojo(client);

    @Test
    void should_throw_exception_when_shouldFailBuild_and_outdatedDependencies() throws Exception {
        var dependency = createDependency();
        var project = createProjectWithDependencies(dependency);
        mojo.setProject(project);

        mockClientResponseForDependency(client, dependency, LocalDate.now().minusYears(10));

        mojo.setProject(project);

        mojo.setShouldFailBuild(true);
        assertThatThrownBy(() -> mojo.execute())
                .isInstanceOf(MojoFailureException.class)
                .hasMessage("There are dependencies that are outdated.");
    }

    @Test
    void should_not_throw_exception_by_default_when_outdatedDependencies() throws Exception {
        var dependency = createDependency();
        var project = createProjectWithDependencies(dependency);
        mojo.setProject(project);

        mockClientResponseForDependency(client, dependency, LocalDate.now().minusYears(10));

        mojo.setProject(project);

        assertThatCode(() -> mojo.execute()).doesNotThrowAnyException();
    }

    @Test
    void should_not_throw_exception_when_no_dependencies() throws MojoExecutionException {
        final MavenProject project = new MavenProject();
        project.setDependencies(Collections.emptyList());
        mojo.setProject(project);

        verify(client, never()).search(anyList());
    }

    @Test
    void should_log_warning_when_dependency_is_outdated() throws MojoExecutionException {
        var dependency = createDependency();
        var project = createProjectWithDependencies(dependency);
        mojo.setProject(project);

        mockClientResponseForDependency(client, dependency, LocalDate.of(1998, 2, 19));

        TestLogger logger = new TestLogger();
        mojo.setLog(logger);

        assertThatCode(() -> mojo.execute()).doesNotThrowAnyException();
        assertThat(logger.getWarningLogs()).containsExactly(
                "Dependency 'com.giovds:test-example:1.0.0' has not received an update since version '1.0.0' was last uploaded '1998-02-19'."
        ).hasSize(1);
        assertThat(logger.getErrorLogs()).isEmpty();
    }

    @Test
    void should_log_error_when_dependency_is_outdated_and_shouldFailBuild() throws MojoExecutionException {
        var dependency = createDependency();
        var project = createProjectWithDependencies(dependency);
        mojo.setProject(project);

        mockClientResponseForDependency(client, dependency, LocalDate.of(1998, 2, 19));

        TestLogger logger = new TestLogger();
        mojo.setLog(logger);
        mojo.setShouldFailBuild(true);

        assertThatThrownBy(() -> mojo.execute())
                .isInstanceOf(MojoFailureException.class)
                .hasMessage("There are dependencies that are outdated.");
        assertThat(logger.getWarningLogs()).isEmpty();
        assertThat(logger.getErrorLogs()).containsExactly(
                "Dependency 'com.giovds:test-example:1.0.0' has not received an update since version '1.0.0' was last uploaded '1998-02-19'."
        ).hasSize(1);
    }
}
