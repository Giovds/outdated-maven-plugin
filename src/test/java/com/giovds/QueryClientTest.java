package com.giovds;

import com.giovds.dto.DependencyResponse;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class QueryClientTest {

    @Test
    void should_return_mapped_result_when_endpoint_called(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
        stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withBodyFile("exampleResponse.json")));

        final Set<DependencyResponse> result = new QueryClient(wmRuntimeInfo.getHttpBaseUrl()).search(List.of("any-query"));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(5);
        assertThat(result).contains(
                new DependencyResponse("org.apache.maven:maven-core:3.9.8", "org.apache.maven", "maven-core", "3.9.8", 1718267050000L),
                new DependencyResponse("org.apache.maven.plugin-tools:maven-plugin-annotations:3.13.1", "org.apache.maven.plugin-tools", "maven-plugin-annotations", "3.13.1", 1716884186000L)
        );
    }

    @Test
    void should_throw_exception_when_failed_to_connect(final WireMockRuntimeInfo wmRuntimeInfo) {
        stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> new QueryClient(wmRuntimeInfo.getHttpBaseUrl()).search(List.of("any-query")))
                .isInstanceOf(MojoExecutionException.class);
    }

    @Test
    void should_report_error_when_failed_to_connect(final WireMockRuntimeInfo wmRuntimeInfo) {
        final String forbiddenBody = """
                </html>
                    <head><title>403 Forbidden</title></head>
                    <body>
                        <center><h1>403 Forbidden</h1></center>
                    </body>
                </html>
                """;
        stubFor(get(anyUrl())
                .willReturn(forbidden()
                        .withBody(forbiddenBody)));

        assertThatThrownBy(() -> new QueryClient(wmRuntimeInfo.getHttpBaseUrl()).search(List.of("any-query")))
                .isInstanceOf(MojoExecutionException.class)
                .rootCause()
                .hasMessageContaining(forbiddenBody);
    }
}
