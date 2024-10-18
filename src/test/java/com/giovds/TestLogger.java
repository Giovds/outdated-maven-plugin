package com.giovds;

import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.ArrayList;
import java.util.List;

class TestLogger extends SystemStreamLog {
    private final List<String> infoLogs = new ArrayList<>();
    private final List<String> warningLogs = new ArrayList<>();
    private final List<String> errorLogs = new ArrayList<>();

    @Override
    public void info(final CharSequence content) {
        infoLogs.add(content.toString());
    }

    @Override
    public void warn(final CharSequence content) {
        warningLogs.add(content.toString());
    }

    @Override
    public void error(final CharSequence content) {
        errorLogs.add(content.toString());
    }

    public List<String> getInfoLogs() {
        return infoLogs;
    }

    public List<String> getWarningLogs() {
        return warningLogs;
    }

    public List<String> getErrorLogs() {
        return errorLogs;
    }
}
