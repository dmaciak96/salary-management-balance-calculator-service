package com.salary.management.balance_calculator_service.service;

import com.salary.management.balance_calculator_service.model.BalanceDto;

import java.util.UUID;

public interface BalanceService {

    BalanceDto calculateBalance(UUID balanceMemberGroupId, UUID balanceGroupId);
}
