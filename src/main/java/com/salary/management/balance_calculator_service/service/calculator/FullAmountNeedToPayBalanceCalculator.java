package com.salary.management.balance_calculator_service.service.calculator;

import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class FullAmountNeedToPayBalanceCalculator implements BalanceCalculator {

    @Override
    public boolean isApplicable(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return currentUserNeedToPayFullAmount(expense, currentUserId) || balanceGroupMembersCount == 1;
    }

    private boolean currentUserNeedToPayFullAmount(ExpenseDto expense, UUID currentUser) {
        return expense.getSplitType() == SplitType.FullAmountForSingleGroupMember && expense.getNeedToPayUserId() == currentUser;
    }

    @Override
    public BigDecimal calculateBalanceAmount(UUID currentUserId, int balanceGroupMembersCount, ExpenseDto expense) {
        return expense.getAmount().multiply(BigDecimal.valueOf(-1));
    }
}
