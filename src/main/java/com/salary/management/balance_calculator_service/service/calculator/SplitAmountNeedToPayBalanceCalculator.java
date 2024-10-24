package com.salary.management.balance_calculator_service.service.calculator;

import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class SplitAmountNeedToPayBalanceCalculator implements BalanceCalculator {

    @Override
    public boolean isApplicable(UUID balanceGroupMemberId, int balanceGroupMembersCount, ExpenseDto expense) {
        return expense.getSplitType() == SplitType.SplitBetweenGroupMembers && expense.getPaidByGroupMember().getId() != balanceGroupMemberId;
    }

    @Override
    public BigDecimal calculateBalanceAmount(UUID balanceGroupMemberId, int balanceGroupMembersCount, ExpenseDto expense) {
        return expense.getAmount().divide(BigDecimal.valueOf(balanceGroupMembersCount)).multiply(BigDecimal.valueOf(-1));
    }
}
