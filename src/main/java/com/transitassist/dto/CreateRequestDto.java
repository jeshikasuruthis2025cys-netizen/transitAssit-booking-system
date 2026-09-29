package com.transitassist.dto;

import com.transitassist.model.AssistanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record CreateRequestDto(
        @NotNull(message = "userId is required") Long userId,
        @NotBlank(message = "pickupPoint is required") String pickupPoint,
        @NotNull(message = "pickupTime is required") LocalDateTime pickupTime,
        @NotNull(message = "assistanceType is required") AssistanceType assistanceType,
        @Positive(message = "durationMinutes must be positive") Integer durationMinutes) {
}
