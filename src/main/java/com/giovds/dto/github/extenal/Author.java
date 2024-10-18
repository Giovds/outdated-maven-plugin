package com.giovds.dto.github.extenal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
    @JsonProperty("login")
    private String login = "";

    public String getLogin() {
        return login;
    }
}
