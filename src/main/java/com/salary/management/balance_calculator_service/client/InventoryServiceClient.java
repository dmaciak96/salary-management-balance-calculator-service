package com.salary.management.balance_calculator_service.client;

import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Primary
@FeignClient(value = "inventory-service", fallback = InventoryServiceFallback.class)
public interface InventoryServiceClient {

    @GetMapping("/api/balance-groups/{balanceGroupId}/expenses")
    List<ExpenseDto> findAllExpensesFromBalanceGroup(@PathVariable UUID balanceGroupId);

    @GetMapping("/api/balance-groups/{balanceGroupId}/members")
    List<BalanceGroupMemberDto> findAllBalanceGroupMembers(@PathVariable UUID balanceGroupId);
}
