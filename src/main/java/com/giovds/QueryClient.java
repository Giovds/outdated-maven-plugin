package com.giovds;

import com.fasterxml.jackson.jr.ob.JSON;
import com.giovds.dto.DependencyResponse;
import com.giovds.dto.MavenCentralResponse;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Search Maven Central with a Lucene query
 */
public class QueryClient {
    private final String main_uri;
    private static final HttpClient client = HttpClient.newHttpClient();

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
        try {
            final Set<DependencyResponse> allDependencies = new HashSet<>();
            for (final String query : queries) {
                final HttpRequest request = buildHttpRequest(query);
                allDependencies.addAll(client.send(request, new SearchResponseBodyHandler()).body());
            }
            return allDependencies;
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to connect.", e);
        }
    }

    private HttpRequest buildHttpRequest(final String query) {
        final String uri = String.format("%s?q=%s&wt=json", main_uri, query);
        return HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create(uri))
                .build();
    }

    private static class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Set<DependencyResponse>> {
        @Override
        public HttpResponse.BodySubscriber<Set<DependencyResponse>> apply(final HttpResponse.ResponseInfo responseInfo) {
            if (responseInfo.statusCode() > 201) {
                return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), s -> {
                    throw new RuntimeException("Search failed: status: " + responseInfo.statusCode() + " body: " + s);
                });
            }
            HttpResponse.BodySubscriber<InputStream> stream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(stream, this::toSearchResponse);
        }

        Set<DependencyResponse> toSearchResponse(final InputStream inputStream) {
            try (final InputStream input = inputStream) {
                final MavenCentralResponse mavenCentralResponse = JSON.std.beanFrom(MavenCentralResponse.class, input);
                return new HashSet<>(mavenCentralResponse.response().docs());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
