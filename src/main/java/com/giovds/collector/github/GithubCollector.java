package com.giovds.collector.github;

import com.fasterxml.jackson.jr.annotationsupport.JacksonAnnotationExtension;
import com.fasterxml.jackson.jr.ob.JSON;
import com.giovds.dto.github.extenal.CommitActivity;
import com.giovds.dto.github.extenal.ContributorStat;
import com.giovds.dto.github.extenal.Repository;
import com.giovds.dto.github.internal.*;
import com.giovds.http.JsonBodyHandler;
import org.apache.maven.plugin.logging.Log;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GithubCollector implements GithubCollectorInterface {
    private static final String GITHUB_API = "https://api.github.com";
    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    private final String baseUrl;
    private final HttpClient httpClient;
    private final Log log;

    public GithubCollector(Log log) {
        this(GITHUB_API, HttpClient.newHttpClient(), log);
    }

    public GithubCollector(String baseUrl, HttpClient httpClient, Log log) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.log = log;

        if (GITHUB_TOKEN == null || GITHUB_TOKEN.isEmpty()) {
            log.warn("No GitHub token provided, rate limits will be enforced. Provide a token by setting the GITHUB_TOKEN environment variable.");
        } else {
            log.info("GitHub token provided");
        }
    }

    @Override
    public Collected collect(String owner, String repo) throws InterruptedException, ExecutionException {
        var repository = getRepository(owner, repo).get();
        var contributors = getContributors(repository).get();
        var commitActivity = getCommitActivity(repository).get();

        var summary = extractCommits(commitActivity);

        return Collected.builder()
                .homepage(repository.homepage())
                .starsCount(repository.stargazersCount())
                .forksCount(repository.forksCount())
                .subscribersCount(repository.subscribersCount())
                .contributors(Arrays.stream(contributors).map(
                        contributor -> Contributor.builder()
                                .username(contributor.author().login())
                                .commitsCount(contributor.total())
                                .build()
                ).toList().reversed())
                .commits(summary)
                .build();
    }

    private CompletableFuture<Repository> getRepository(String owner, String repo) throws InterruptedException {
        return requestAsync(String.format("repos/%s/%s", owner, repo), Repository.class);
    }

    private CompletableFuture<ContributorStat[]> getContributors(Repository repository) throws InterruptedException {
        return requestAsync("%s/stats/contributors".formatted(repository.url()), ContributorStat[].class, true);
    }

    private CompletableFuture<CommitActivity[]> getCommitActivity(Repository repository) throws InterruptedException {
        return requestAsync("%s/stats/commit_activity".formatted(repository.url()), CommitActivity[].class, true);
    }

    private <R> CompletableFuture<R> requestAsync(String path, Class<R> responseType) throws InterruptedException {
        return requestAsync(path, responseType, false);
    }

    private <R> CompletableFuture<R> requestAsync(String path, Class<R> responseType, boolean isUri) throws InterruptedException {
        var res = sendRequestAsync(path, responseType, isUri);

        var remaining = Integer.parseInt(res.join().headers().firstValue("X-RateLimit-Remaining").orElse("0"));
        if (remaining == 0) {
            long delay = Math.max(0, Long.parseLong(res.join().headers().firstValue("X-RateLimit-Reset").orElse("0")) - System.currentTimeMillis());

            log.info("Rate limit exceeded, waiting for %s ms".formatted(delay));
            Thread.sleep(delay);

            return requestAsync(path, responseType, isUri);
        }

        if (res.join().statusCode() == 202) {
            Thread.sleep(10);
            return requestAsync(path, responseType, isUri);
        }

        return res.thenApply(HttpResponse::body);
    }

    private <R> CompletableFuture<HttpResponse<R>> sendRequestAsync(String pathOrUri, Class<R> responseType) {
        return sendRequestAsync(pathOrUri, responseType, false);
    }

    private <R> CompletableFuture<HttpResponse<R>> sendRequestAsync(String pathOrUri, Class<R> responseType, boolean isUri) {
        String url = isUri ? pathOrUri : String.format("%s/%s", baseUrl, pathOrUri);
        var requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .timeout(Duration.ofSeconds(30));

        if (GITHUB_TOKEN != null && !GITHUB_TOKEN.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer %s".formatted(GITHUB_TOKEN));
        }

        var request = requestBuilder.build();

        var json = JSON.builder()
                .register(JacksonAnnotationExtension.std)
                .build();

        return httpClient.sendAsync(request, new JsonBodyHandler<>(json, responseType));
    }

    private static List<RangeSummary> extractCommits(CommitActivity[] commitActivity) {
        List<Point> points = Arrays.stream(commitActivity)
                .map(entry -> Point.builder()
                        .date(Instant.ofEpochSecond(entry.week()).atZone(ZoneOffset.UTC).toOffsetDateTime())
                        .total(entry.total())
                        .build())
                .toList();

        var ranges = pointsToRanges(points, bucketsFromBreakpoints(List.of(7, 30, 90, 180, 365)));

        return ranges.stream()
                .map(range -> {
                    int count = range.points().stream()
                            .mapToInt(Point::total)
                            .sum();

                    return RangeSummary.builder()
                            .start(range.start())
                            .end(range.end())
                            .count(count)
                            .build();
                })
                .toList();
    }

    private static List<Range> pointsToRanges(List<Point> points, List<Bucket> buckets) {
        return buckets.stream().map(bucket -> {
            List<Point> filteredPoints = points.stream()
                    .filter(point -> !point.date().isBefore(bucket.start()) && point.date().isBefore(bucket.end()))
                    .collect(Collectors.toList());

            return Range.builder()
                    .start(bucket.start())
                    .end(bucket.end())
                    .points(filteredPoints)
                    .build();
        }).collect(Collectors.toList());
    }

    private static List<Bucket> bucketsFromBreakpoints(List<Integer> breakpoints) {
        OffsetDateTime referenceDate = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);

        return breakpoints.stream()
                .map(breakpoint -> Bucket.builder()
                        .start(referenceDate.minusDays(breakpoint))
                        .end(referenceDate)
                        .build())
                .collect(Collectors.toList());
    }
}
