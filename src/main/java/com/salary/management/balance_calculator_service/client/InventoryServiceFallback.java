package com.salary.management.balance_calculator_service.client;

import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class InventoryServiceFallback implements InventoryServiceClient {

    @Override
    public List<ExpenseDto> findAllExpensesFromBalanceGroup(UUID balanceGroupId) {
        log.warn("Fallback was invoke for GET /expenses from inventory service");
        return List.of();
    }

    @Override
    public List<BalanceGroupMemberDto> findAllBalanceGroupMembers(UUID balanceGroupId) {
        log.warn("Fallback was invoke for GET /members from inventory service");
        return List.of();
    }
}
