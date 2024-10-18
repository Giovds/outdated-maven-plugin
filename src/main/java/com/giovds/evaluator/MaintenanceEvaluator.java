package com.giovds.evaluator;

import com.giovds.dto.github.internal.Collected;
import com.giovds.dto.github.internal.RangeSummary;
import com.giovds.normalizer.DefaultNormalizer;

import java.time.Duration;
import java.util.List;

public class MaintenanceEvaluator {
    public double evaluateCommitsFrequency(Collected collected) {
        var commits = collected.getCommits();
        if (commits.isEmpty()) {
            return 0;
        }

        var range30 = findRange(commits, 30);
        var range180 = findRange(commits, 180);
        var range365 = findRange(commits, 365);

        var mean30 = range30.getCount();
        var mean180 = range180.getCount() / (180.0d / 30.0d);
        var mean365 = range365.getCount() / (365.0d / 30.0d);

        var monthlyMean = (mean30 * 0.35d) +
                (mean180 * 0.45d) +
                (mean365 * 0.2d);

        var normalizer = new DefaultNormalizer();

        return normalizer.normalizeValue(monthlyMean, List.of(
                new DefaultNormalizer.NormalizeStep(0d, 0d),
                new DefaultNormalizer.NormalizeStep(1d, 0.7d),
                new DefaultNormalizer.NormalizeStep(5d, 0.9d),
                new DefaultNormalizer.NormalizeStep(10d, 1d)
        ));
    }

    private RangeSummary findRange(List<RangeSummary> commits, int days) {
        return commits.stream()
                .filter(range -> Duration.between(range.getStart(), range.getEnd()).toDays() == days)
                .limit(1)
                .toList()
                .getFirst();
    }
}
