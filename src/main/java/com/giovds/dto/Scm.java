package com.giovds.dto;

public record Scm(String url) {
    public static Scm empty() {
        return new Scm(null);
    }
}
