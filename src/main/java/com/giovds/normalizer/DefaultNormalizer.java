package com.giovds.normalizer;

import java.util.List;
import java.util.function.Predicate;

public class DefaultNormalizer {
    public double normalizeValue(double value, List<NormalizeStep> steps) {
        var index = findLastIndex(steps, step -> step.value() <= value);

        if (index == -1) {
            return steps.getFirst().norm();
        }
        if (index == steps.size() - 1) {
            return steps.getLast().norm();
        }

        var stepLow = steps.get(index);
        var stepHigh = steps.get(index + 1);

        return stepLow.norm() + ((stepHigh.norm - stepLow.norm) * (value - stepLow.value)) / (stepHigh.value - stepLow.value);
    }

    private static int findLastIndex(List<NormalizeStep> list, Predicate<NormalizeStep> predicate) {
        List<NormalizeStep> reversed = list.reversed();
        int reverseIdx = -1;

        for (int i = 0; i < reversed.size(); i++) {
            if (predicate.test(reversed.get(i))) {
                reverseIdx = i;
                break;

            }
        }

        return reverseIdx == -1 ? -1 : reversed.size() - (reverseIdx + 1);
    }

    public record NormalizeStep(double value, double norm) {
    }
}
