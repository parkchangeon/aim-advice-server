package com.aim.advice.dto.advice;

import com.aim.advice.domain.advice.RiskType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdviceRequest {

    @NotNull(message = "RiskType is required")
    private RiskType riskType;

    @Builder
    private AdviceRequest(RiskType riskType) {
        this.riskType = riskType;
    }

    public static AdviceRequest of(RiskType riskType) {
        return AdviceRequest.builder()
                .riskType(riskType)
                .build();
    }
}
