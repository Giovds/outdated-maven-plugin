package com.giovds.collector.github;

import java.util.regex.Pattern;

public class GithubGuesser {

    private final Pattern githubRepoPattern = Pattern.compile("^[a-zA-Z]+://github\\.com/([^/]+)/([^/]+)(/.*)?");

    public Repository guess(String url) {
        var matcher = githubRepoPattern.matcher(url);
        if (matcher.matches()) {
            return new Repository(matcher.group(1), matcher.group(2));
        }

        return null;
    }

    public record Repository(String owner, String repo) {
    }
}
