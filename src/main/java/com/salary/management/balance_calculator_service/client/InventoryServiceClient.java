package com.salary.management.balance_calculator_service.client;

import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Primary
@RequestMapping("/api/balance-groups")
@FeignClient(value = "inventory-service", fallback = InventoryServiceFallback.class)
public interface InventoryServiceClient {

    @GetMapping("/{balanceGroupId}/expenses")
    List<ExpenseDto> findAllExpensesFromBalanceGroup(@PathVariable UUID balanceGroupId);

    @GetMapping("/{balanceGroupId}/members")
    List<BalanceGroupMemberDto> findAllBalanceGroupMembers(@PathVariable UUID balanceGroupId);
}
