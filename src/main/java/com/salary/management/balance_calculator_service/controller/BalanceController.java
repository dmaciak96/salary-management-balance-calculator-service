package com.salary.management.balance_calculator_service.controller;

import com.salary.management.balance_calculator_service.model.BalanceDto;
import com.salary.management.balance_calculator_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping("/balance-group/{balanceGroupId}/members/{balanceMemberGroupId}/balance")
    public BalanceDto calculateBalanceByBalanceGroupAndUserId(@PathVariable UUID balanceGroupId,
                                                              @PathVariable UUID balanceMemberGroupId) {
        return balanceService.calculateBalance(balanceMemberGroupId, balanceGroupId);
    }
}
