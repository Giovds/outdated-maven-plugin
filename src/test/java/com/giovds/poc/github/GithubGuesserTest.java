package com.giovds.poc.github;

import com.giovds.collector.github.GithubGuesser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GithubGuesserTest {
    @Test
    public void non_github_url_returns_null() {
        GithubGuesser githubGuesser = new GithubGuesser();
        assertNull(githubGuesser.guess("https://gitlab.com/owner/repo"));
    }

    @Test
    public void github_url_returns_owner_and_repository() {
        GithubGuesser githubGuesser = new GithubGuesser();
        GithubGuesser.Repository repository = githubGuesser.guess("https://github.com/Giovds/outdated-maven-plugin");

        assertNotNull(repository);
        assertEquals("Giovds", repository.owner());
        assertEquals("outdated-maven-plugin", repository.repo());
    }

    @Test
    public void github_url_additional_slash_returns_owner_and_repository() {
        GithubGuesser githubGuesser = new GithubGuesser();
        GithubGuesser.Repository repository = githubGuesser.guess("https://github.com/Giovds/outdated-maven-plugin/");

        assertNotNull(repository);
        assertEquals("Giovds", repository.owner());
        assertEquals("outdated-maven-plugin", repository.repo());
    }
}
