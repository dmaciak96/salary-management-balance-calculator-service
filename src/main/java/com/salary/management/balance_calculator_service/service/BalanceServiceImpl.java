package com.salary.management.balance_calculator_service.service;

import com.salary.management.balance_calculator_service.client.InventoryServiceClient;
import com.salary.management.balance_calculator_service.model.BalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final InventoryServiceClient inventoryServiceClient;

    @Override
    public BalanceDto calculateBalance(UUID userId, UUID balanceGroupId) {
        throw new NotImplementedException();
    }
}
