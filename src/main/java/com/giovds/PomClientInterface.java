package com.giovds;

import com.giovds.dto.PomResponse;

import java.io.IOException;

public interface PomClientInterface {
    PomResponse getPom(String group, String artifact, String version) throws IOException, InterruptedException;
}
