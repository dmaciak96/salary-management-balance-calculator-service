package com.salary.management.balance_calculator_service.controller;

import com.salary.management.balance_calculator_service.model.BalanceDto;
import com.salary.management.balance_calculator_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @QueryMapping
    public BalanceDto calculateBalanceByBalanceGroupAndUserId(@Argument UUID balanceGroupId, @Argument UUID userId) {
        return balanceService.calculateBalance(userId, balanceGroupId);
    }
}
