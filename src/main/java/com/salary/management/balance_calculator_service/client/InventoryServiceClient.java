package com.salary.management.balance_calculator_service.client;

import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface InventoryServiceClient {

    List<ExpenseDto> findAllExpensesFromBalanceGroup(@PathVariable UUID balanceGroupId);

    List<BalanceGroupMemberDto> findAllGroupMembersFromBalanceGroup(@PathVariable UUID balanceGroupId);
}
