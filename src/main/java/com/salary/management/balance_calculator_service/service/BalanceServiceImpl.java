package com.salary.management.balance_calculator_service.service;

import com.salary.management.balance_calculator_service.client.InventoryServiceClient;
import com.salary.management.balance_calculator_service.model.BalanceDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.BalanceType;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final InventoryServiceClient inventoryServiceClient;
    private final Executor virtualThreadExecutor;

    @Override
    public BalanceDto calculateBalance(UUID userId, UUID balanceGroupId) {
        var expensesFuture = CompletableFuture.supplyAsync(() ->
                inventoryServiceClient.findAllExpensesFromBalanceGroup(balanceGroupId), virtualThreadExecutor);

        var membersFuture = CompletableFuture.supplyAsync(() ->
                inventoryServiceClient.findAllBalanceGroupMembers(balanceGroupId), virtualThreadExecutor);

        return expensesFuture.thenCombine(membersFuture,
                (expenses, members) -> {
                    var balanceAmount = calculateBalanceAmount(userId, members.size(), expenses);
                    return mapAmountToBalanceDto(balanceAmount, userId, balanceGroupId);
                }).join();
    }

    private BigDecimal calculateBalanceAmount(UUID currentUserId,
                                              int balanceGroupMembersCount,
                                              Collection<ExpenseDto> expenses) {
        if (balanceGroupMembersCount == 0) {
            return BigDecimal.ZERO;
        }
        return expenses.stream()
                .filter(expense -> !expense.isResolved())
                .map(expense -> calculateAmount(currentUserId, balanceGroupMembersCount, expense))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.CEILING);
    }

    private BigDecimal calculateAmount(UUID currentUserId,
                                       int balanceGroupMembersCount,
                                       ExpenseDto expense) {
        if (currentUserPaidFullAmount(expense, currentUserId)) {
            return expense.getAmount();
        } else if (currentUserNeedToPayFullAmount(expense, currentUserId) || balanceGroupMembersCount == 1) {
            return expense.getAmount().multiply(BigDecimal.valueOf(-1));
        } else if (currentUserPaidSplitExpense(expense, currentUserId)) {
            return expense.getAmount().subtract(expense.getAmount().divide(BigDecimal.valueOf(balanceGroupMembersCount), 0, RoundingMode.CEILING));
        } else if (currentUserNeedToPaySplitExpense(expense, currentUserId)) {
            return expense.getAmount().divide(BigDecimal.valueOf(balanceGroupMembersCount)).multiply(BigDecimal.valueOf(-1));
        }
        return BigDecimal.ZERO;
    }

    private boolean currentUserPaidFullAmount(ExpenseDto expense, UUID currentUser) {
        return expense.getSplitType() == SplitType.FullAmountForSingleGroupMember && expense.getPaidByUserId() == currentUser;
    }

    private boolean currentUserNeedToPayFullAmount(ExpenseDto expense, UUID currentUser) {
        return expense.getSplitType() == SplitType.FullAmountForSingleGroupMember && expense.getNeedToPayUserId() == currentUser;
    }

    private boolean currentUserPaidSplitExpense(ExpenseDto expense, UUID currentUser) {
        return expense.getSplitType() == SplitType.SplitBetweenGroupMembers && expense.getPaidByUserId() == currentUser;
    }

    private boolean currentUserNeedToPaySplitExpense(ExpenseDto expense, UUID currentUser) {
        return expense.getSplitType() == SplitType.SplitBetweenGroupMembers && expense.getPaidByUserId() != currentUser;
    }

    private BalanceDto mapAmountToBalanceDto(BigDecimal expenseAmount, UUID currentUserId, UUID balanceGroupId) {
        if (expenseAmount.doubleValue() < 0) {
            return new BalanceDto(currentUserId, balanceGroupId, expenseAmount.multiply(BigDecimal.valueOf(-1)), BalanceType.Minus);
        } else if (expenseAmount.doubleValue() == 0) {
            return new BalanceDto(currentUserId, balanceGroupId, expenseAmount, BalanceType.Resolved);
        } else {
            return new BalanceDto(currentUserId, balanceGroupId, expenseAmount, BalanceType.Plus);
        }
    }
}
