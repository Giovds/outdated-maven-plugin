package com.giovds;

import com.giovds.dto.DependencyResponse;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CheckMojoTest {

    @Mock
    private QueryClient client = mock(QueryClient.class);

    @InjectMocks
    private CheckMojo mojo = new CheckMojo(client);

    @Test
    void should_throw_exception_when_shouldFailBuild_and_outdatedDependencies() throws Exception {
        final MavenProject projectWithDependency = createProjectWithDependencyOfAge(LocalDate.now().minusYears(10));
        mojo.setProject(projectWithDependency);

        mojo.setShouldFailBuild(true);
        assertThatThrownBy(() -> mojo.execute())
                .isInstanceOf(MojoFailureException.class)
                .hasMessage("There are dependencies that are outdated.");
    }

    @Test
    void should_not_throw_exception_by_default_when_outdatedDependencies() throws Exception {
        final MavenProject projectWithDependency = createProjectWithDependencyOfAge(LocalDate.now().minusYears(10));
        mojo.setProject(projectWithDependency);

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
        final MavenProject projectWithDependency = createProjectWithDependencyOfAge(LocalDate.of(1998, 2, 19));
        mojo.setProject(projectWithDependency);
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
        final MavenProject projectWithDependency = createProjectWithDependencyOfAge(LocalDate.of(1998, 2, 19));
        mojo.setProject(projectWithDependency);
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

    private static class TestLogger extends SystemStreamLog {
        private final List<String> warningLogs = new ArrayList<>();
        private final List<String> errorLogs = new ArrayList<>();

        @Override
        public void warn(final CharSequence content) {
            warningLogs.add(content.toString());
        }

        @Override
        public void error(final CharSequence content) {
            errorLogs.add(content.toString());
        }

        public List<String> getWarningLogs() {
            return warningLogs;
        }

        public List<String> getErrorLogs() {
            return errorLogs;
        }
    }

    private MavenProject createProjectWithDependencyOfAge(final LocalDate date) throws MojoExecutionException {
        final Dependency dependency = new Dependency();
        long epochMilliseconds = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        dependency.setGroupId("com.giovds");
        dependency.setArtifactId("test-example");
        dependency.setVersion("1.0.0");
        when(client.search(any())).thenReturn(Set.of(
                new DependencyResponse(createDependencyKey(dependency), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), epochMilliseconds)
        ));

        final MavenProject project = new MavenProject();
        project.setDependencies(List.of(dependency));
        return project;
    }

    private static String createDependencyKey(final Dependency dependency) {
        return String.format("%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    }
}
