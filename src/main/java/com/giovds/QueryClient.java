package com.giovds;

import com.fasterxml.jackson.jr.ob.JSON;
import com.giovds.dto.DependencyResponse;
import com.giovds.dto.MavenCentralResponse;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Search Maven Central with a Lucene query
 */
public class QueryClient {
    private final String main_uri;
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(50))
            .executor(Executors.newFixedThreadPool(8))
            .build();
    private static final int MAX_RETRIES = 5;
    private static final Duration INITIAL_RETRY_DELAY = Duration.ofSeconds(1);

    /**
     * Visible for testing purposes
     */
    QueryClient(final String uri) {
        this.main_uri = uri;
    }

    /**
     * Defaults to connect to actual Maven Central endpoint
     */
    public QueryClient() {
        this("https://search.maven.org/solrsearch/select");
    }

    /**
     * Send a GET request to Maven Central with the provided query to
     *
     * @param queries The Lucene query as {@link String}
     * @return A set of dependencies returned by Maven Central mapped to {@link DependencyResponse}
     * @throws MojoExecutionException when something failed when sending the request
     */
    public Set<DependencyResponse> search(final List<String> queries) throws MojoExecutionException {
        final Set<DependencyResponse> allDependencies = new HashSet<>();
        for (final String query : queries) {
            allDependencies.addAll(searchWithRetry(query));
        }
        return allDependencies;
    }

    private Set<DependencyResponse> searchWithRetry(final String query) throws MojoExecutionException {
        Exception lastException = null;

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                final HttpRequest request = buildHttpRequest(query);
                return client.send(request, new SearchResponseBodyHandler()).body();
            } catch (IOException | InterruptedException e) {
                lastException = e;

                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    throw new MojoExecutionException("Query interrupted", e);
                }

                // Don't retry on last attempt
                if (attempt < MAX_RETRIES - 1) {
                    final long delayMillis = INITIAL_RETRY_DELAY.toMillis() * (long) Math.pow(2, attempt);
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new MojoExecutionException("Query interrupted during retry", ie);
                    }
                }
            }
        }

        throw new MojoExecutionException("Failed to query Maven Central after " + MAX_RETRIES + " attempts", lastException);
    }

    private HttpRequest buildHttpRequest(final String query) {
        final String uri = String.format("%s?q=%s&wt=json", main_uri, query);
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(60))
                .header("Accept", "application/json")
                .header("User-Agent", "outdated-maven-plugin/1.5.0")
                .build();
    }

    private static class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Set<DependencyResponse>> {
        @Override
        public HttpResponse.BodySubscriber<Set<DependencyResponse>> apply(final HttpResponse.ResponseInfo responseInfo) {
            if (responseInfo.statusCode() > 201) {
                return HttpResponse.BodySubscribers.mapping(
                        HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                        s -> {
                            throw new RuntimeException("Search failed: status: " + responseInfo.statusCode() + " body: " + s);
                        }
                );
            }
            // Buffer entire response before processing - avoids stream lifecycle issues
            return HttpResponse.BodySubscribers.mapping(
                    HttpResponse.BodySubscribers.ofByteArray(),
                    this::toSearchResponse
            );
        }

        Set<DependencyResponse> toSearchResponse(final byte[] responseBytes) {
            try {
                final MavenCentralResponse mavenCentralResponse = JSON.std.beanFrom(MavenCentralResponse.class, responseBytes);
                return new HashSet<>(mavenCentralResponse.response().docs());
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse Maven Central response", e);
            }
        }

    }

}
