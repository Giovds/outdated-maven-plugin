package com.giovds.collector.github;

import com.giovds.dto.github.internal.Collected;

import java.util.concurrent.ExecutionException;

public interface GithubCollectorInterface {
    Collected collect(String owner, String repo) throws ExecutionException, InterruptedException;
}
