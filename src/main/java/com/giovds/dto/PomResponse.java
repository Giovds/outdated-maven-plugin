package com.giovds.dto;

public record PomResponse(String url, Scm scm) {
    public static PomResponse empty() {
        return new PomResponse(null, Scm.empty());
    }

    public String scmUrl() {
        return scm.url();
    }
}
