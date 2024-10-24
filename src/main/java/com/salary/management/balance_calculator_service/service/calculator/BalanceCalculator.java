package com.salary.management.balance_calculator_service.service.calculator;

import com.salary.management.balance_calculator_service.model.ExpenseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface BalanceCalculator {

    boolean isApplicable(UUID balanceGroupMemberId, int balanceGroupMembersCount, ExpenseDto expense);

    BigDecimal calculateBalanceAmount(UUID balanceGroupMemberId,
                                      int balanceGroupMembersCount,
                                      ExpenseDto expense);
}
