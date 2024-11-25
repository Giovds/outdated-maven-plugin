package com.giovds.normalizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultNormalizerTest {
    private DefaultNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new DefaultNormalizer();
    }

    @Test
    void normalizeValue_withLowMaintenance_expectLowValueGreaterThanZero() {
        var value = 0.04931506849315069d;

        assertThat(normalizer.normalizeValue(value, createDefaultSteps())).isEqualTo(0.03452054794520548d);
    }

    @Test
    void normalizeValue_withHighMaintenance_expectHighValueLowerThanOne() {
        var value = 9.3236301369863d;

        assertThat(normalizer.normalizeValue(value, createDefaultSteps())).isEqualTo(0.986472602739726d);
    }

    private List<DefaultNormalizer.NormalizeStep> createDefaultSteps() {
        return List.of(
                new DefaultNormalizer.NormalizeStep(0, 0),
                new DefaultNormalizer.NormalizeStep(1, 0.7),
                new DefaultNormalizer.NormalizeStep(5, 0.9),
                new DefaultNormalizer.NormalizeStep(10, 1)
        );
    }
}
