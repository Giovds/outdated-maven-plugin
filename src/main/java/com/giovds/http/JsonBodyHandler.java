package com.giovds.http;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandler<R> implements HttpResponse.BodyHandler<R> {
    private final JSON json;
    private final Class<R> resultClass;

    public JsonBodyHandler(JSON json, Class<R> resultClass) {
        this.json = json;
        this.resultClass = resultClass;
    }

    @Override
    public HttpResponse.BodySubscriber<R> apply(HttpResponse.ResponseInfo res) {
        if (res.statusCode() == 202) {
            return HttpResponse.BodySubscribers.replacing(null);
        }

        var remaining = res.headers().firstValue("X-RateLimit-Remaining").orElse("0");
        if ("0".equals(remaining)) {
            return HttpResponse.BodySubscribers.replacing(null);
        }

        return asJSON(res, resultClass);
    }

    public HttpResponse.BodySubscriber<R> asJSON(HttpResponse.ResponseInfo res, Class<R> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                (String body) -> {
                    try {
                        return json.beanFrom(targetType, body);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
