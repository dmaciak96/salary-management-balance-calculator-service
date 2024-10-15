package com.salary.management.balance_calculator_service.service.calculator;

import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Component
public class SplitAmountPaidBalanceCalculator implements BalanceCalculator {

    @Override
    public boolean isApplicable(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return currentUserPaidSplitExpense(expense, currentUserId);
    }

    private boolean currentUserPaidSplitExpense(ExpenseDto expense, UUID currentUserId) {
        return expense.getSplitType() == SplitType.SplitBetweenGroupMembers && expense.getPaidByUserId() == currentUserId;
    }

    @Override
    public BigDecimal calculateBalanceAmount(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return expense.getAmount()
                .subtract(expense.getAmount()
                        .divide(BigDecimal.valueOf(balanceGroupMembersCount), 0, RoundingMode.CEILING));
    }
}
