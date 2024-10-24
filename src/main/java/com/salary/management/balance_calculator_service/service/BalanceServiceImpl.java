package com.salary.management.balance_calculator_service.service;

import com.salary.management.balance_calculator_service.client.InventoryServiceClient;
import com.salary.management.balance_calculator_service.model.BalanceDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.BalanceType;
import com.salary.management.balance_calculator_service.service.calculator.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final InventoryServiceClient inventoryServiceClient;
    private final Executor virtualThreadExecutor;
    private final List<BalanceCalculator> balanceCalculators;

    @Override
    public BalanceDto calculateBalance(UUID balanceMemberGroupId, UUID balanceGroupId) {
        log.debug("Calculating balance amount for group member {} (balance group ID: {})", balanceMemberGroupId, balanceGroupId);
        var expensesFuture = CompletableFuture.supplyAsync(() ->
                inventoryServiceClient.findAllExpensesFromBalanceGroup(balanceGroupId), virtualThreadExecutor);
        var membersFuture = CompletableFuture.supplyAsync(() ->
                inventoryServiceClient.findAllBalanceGroupMembers(balanceGroupId), virtualThreadExecutor);

        var totalAmount = expensesFuture.thenCombine(membersFuture,
                (expenses, members) -> {
                    var balanceAmount = calculateBalanceAmount(balanceMemberGroupId, members.size(), expenses);
                    return mapAmountToBalanceDto(balanceAmount, balanceMemberGroupId, balanceGroupId);
                }).join();

        log.debug("Total balance amount for group member {} is {} ({}) (balance group ID: {})", balanceMemberGroupId,
                totalAmount.amount(), totalAmount.balanceType(), balanceGroupId);
        return totalAmount;
    }

    private BigDecimal calculateBalanceAmount(UUID currentUserId,
                                              int balanceGroupMembersCount,
                                              Collection<ExpenseDto> expenses) {
        if (balanceGroupMembersCount == 0) {
            log.debug("Balance Group has no members, returning 0 as balance amount");
            return BigDecimal.ZERO;
        }

        return expenses.stream()
                .filter(expense -> !expense.isResolved())
                .map(expense -> calculateAmount(currentUserId, balanceGroupMembersCount, expense))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.CEILING);
    }

    private BigDecimal calculateAmount(UUID balanceGroupMemberId,
                                       int balanceGroupMembersCount,
                                       ExpenseDto expense) {
        log.debug("Calculating amount for expense {} (group member ID: {})", expense.getId(), balanceGroupMemberId);
        var balanceCalculator = balanceCalculators.stream()
                .filter(calculator -> calculator.isApplicable(balanceGroupMemberId, balanceGroupMembersCount, expense))
                .findFirst()
                .orElseThrow();
        log.debug("Balance calculator {} will be used to calculate balance amount for expense {} (group member ID: {})",
                balanceCalculator.getClass().getName(), expense.getId(), balanceGroupMemberId);

        var amount = balanceCalculator.calculateBalanceAmount(balanceGroupMemberId, balanceGroupMembersCount, expense);
        log.debug("Balance amount was calculated {} (expense ID: {}, group member ID: {}))",
                amount, expense.getId(), balanceGroupMemberId);
        return amount;
    }

    private BalanceDto mapAmountToBalanceDto(BigDecimal expenseAmount, UUID balanceGroupMember, UUID balanceGroupId) {
        if (expenseAmount.doubleValue() < 0) {
            return new BalanceDto(balanceGroupMember, balanceGroupId, expenseAmount.multiply(BigDecimal.valueOf(-1)), BalanceType.Minus);
        } else if (expenseAmount.doubleValue() == 0) {
            return new BalanceDto(balanceGroupMember, balanceGroupId, expenseAmount, BalanceType.Resolved);
        } else {
            return new BalanceDto(balanceGroupMember, balanceGroupId, expenseAmount, BalanceType.Plus);
        }
    }
}
