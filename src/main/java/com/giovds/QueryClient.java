package com.giovds;

import com.fasterxml.jackson.jr.ob.JSON;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Search Maven Central with a Lucene query
 */
public class QueryClient {
    private final String main_uri;

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
     * @param query The Lucene query as {@link String}
     * @return A set of dependencies returned by Maven Central mapped to {@link FoundDependency}
     * @throws MojoExecutionException when something failed when sending the request
     */
    public Set<FoundDependency> search(final String query) throws MojoExecutionException {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = buildHttpRequest(query);
            return client.send(request, new SearchResponseBodyHandler()).body();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to connect.", e);
        }
    }

    private HttpRequest buildHttpRequest(String query) {
        final String uri = String.format("%s?q=%s&wt=json", main_uri, query);
        return HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create(uri))
                .build();
    }

    private static class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Set<FoundDependency>> {
        @Override
        public HttpResponse.BodySubscriber<Set<FoundDependency>> apply(final HttpResponse.ResponseInfo responseInfo) {
            if (responseInfo.statusCode() > 201) {
                return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), s -> {
                    throw new RuntimeException("Search failed: status: " + responseInfo.statusCode() + " body: " + s);
                });
            }
            HttpResponse.BodySubscriber<InputStream> stream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(stream, this::toSearchResponse);
        }

        /**
         * Jackson-jr supports record deserialization in version <a href="https://github.com/FasterXML/jackson-jr/issues/162">2.18</a>
         * which isn't released yet. Therefore, manual mapping is required.
         */
        Set<FoundDependency> toSearchResponse(final InputStream inputStream) {
            try (final InputStream input = inputStream) {
                final Map<String, Object> httpResponse = JSON.std.mapFrom(input);
                Map<String, Object> response = (Map<String, Object>) httpResponse.get("response");
                Collection<Map<String, Object>> dependencies = (Collection<Map<String, Object>>) response.get("docs");
                return dependencies.stream().map(QueryClient::mapToSearch).collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * The dependency returned by the Maven Central response
     *
     * @param id        GAV (groupId:artifactId:version)
     * @param g         groupId
     * @param a         artifactId
     * @param v         version
     * @param timestamp The date this GAV id was released to Maven Central
     */
    public record FoundDependency(String id, String g, String a, String v, LocalDate timestamp) {
    }

    static FoundDependency mapToSearch(Map<String, Object> doc) {
        return new FoundDependency(
                (String) doc.get("id"),
                (String) doc.get("g"),
                (String) doc.get("a"),
                (String) doc.get("v"),
                Instant.ofEpochMilli((long) doc.get("timestamp")).atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }

}
