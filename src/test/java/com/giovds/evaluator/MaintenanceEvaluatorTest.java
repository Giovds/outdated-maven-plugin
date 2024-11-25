package com.giovds.evaluator;

import com.giovds.dto.github.internal.Collected;
import com.giovds.dto.github.internal.RangeSummary;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaintenanceEvaluatorTest {
    @Test
    void evaluateCommitsFrequency_withLowMaintenance_expectLowScore() {
        // Arrange
        var evaluator = new MaintenanceEvaluator();
        var collected = Collected.builder()
                .commits(List.of(
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 11, 15, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(0)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 10, 23, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(0)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 8, 24, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(0)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 5, 26, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(0)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2023, 11, 23, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(3)
                                .build()
                ))
                .build();

        // Act
        var result = evaluator.evaluateCommitsFrequency(collected);

        // Assert
        assertThat(result).isEqualTo(0.03452054794520548);
    }

    @Test
    void evaluateCommitsFrequency_withHighMaintenance_expectHighScore() {
        // Arrange
        var evaluator = new MaintenanceEvaluator();
        var collected = Collected.builder()
                .commits(List.of(
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 11, 15, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(0)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 10, 23, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(6)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 8, 24, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(36)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2024, 5, 26, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(79)
                                .build(),
                        RangeSummary.builder()
                                .start(OffsetDateTime.of(2023, 11, 23, 0, 0, 0, 0, ZoneOffset.UTC))
                                .end(OffsetDateTime.of(2024, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC))
                                .count(79)
                                .build()
                ))
                .build();

        // Act
        var result = evaluator.evaluateCommitsFrequency(collected);

        // Assert
        assertThat(result).isEqualTo(0.986472602739726);
    }
}
