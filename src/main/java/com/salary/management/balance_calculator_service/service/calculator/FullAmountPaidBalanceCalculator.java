package com.salary.management.balance_calculator_service.service.calculator;

import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class FullAmountPaidBalanceCalculator implements BalanceCalculator {

    @Override
    public boolean isApplicable(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return currentUserPaidFullAmount(currentUserId, expense);
    }

    @Override
    public BigDecimal calculateBalanceAmount(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return expense.getAmount();
    }

    private boolean currentUserPaidFullAmount(UUID currentUserId, ExpenseDto expense) {
        return expense.getSplitType() == SplitType.FullAmountForSingleGroupMember && expense.getPaidByUserId() == currentUserId;
    }
}
