package com.salary.management.balance_calculator_service.model;

import com.salary.management.balance_calculator_service.model.types.BalanceType;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDto(UUID userId,
                         UUID balanceGroupId,
                         BigDecimal amount,
                         BalanceType balanceType) {
}
