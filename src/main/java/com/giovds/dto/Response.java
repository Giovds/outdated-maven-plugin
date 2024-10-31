package com.giovds.dto;

import java.util.List;

/**
 * The dependencies and pagination information from Maven Central
 *
 * @param docs the dependencies from Maven Central
 */
public record Response(List<DependencyResponse> docs) {}
