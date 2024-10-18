package com.salary.management.balance_calculator_service.service;

import com.salary.management.balance_calculator_service.client.InventoryServiceClient;
import com.salary.management.balance_calculator_service.model.BalanceDto;
import com.salary.management.balance_calculator_service.model.BalanceGroupMemberDto;
import com.salary.management.balance_calculator_service.model.ExpenseDto;
import com.salary.management.balance_calculator_service.model.types.BalanceType;
import com.salary.management.balance_calculator_service.model.types.SplitType;
import com.salary.management.balance_calculator_service.service.calculator.FullAmountNeedToPayBalanceCalculator;
import com.salary.management.balance_calculator_service.service.calculator.FullAmountPaidBalanceCalculator;
import com.salary.management.balance_calculator_service.service.calculator.SplitAmountNeedToPayBalanceCalculator;
import com.salary.management.balance_calculator_service.service.calculator.SplitAmountPaidBalanceCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BalanceServiceTest {
    private static final UUID BALANCE_GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ONE = UUID.randomUUID();
    private static final UUID USER_TWO = UUID.randomUUID();
    private static final UUID USER_THREE = UUID.randomUUID();
    private static final UUID USER_FOUR = UUID.randomUUID();

    private final InventoryServiceClient inventoryServiceClientMock;
    private final BalanceService balanceService;

    public BalanceServiceTest() {
        this.inventoryServiceClientMock = mock(InventoryServiceClient.class);
        var balanceCalculators = List.of(
                new FullAmountNeedToPayBalanceCalculator(),
                new FullAmountPaidBalanceCalculator(),
                new SplitAmountNeedToPayBalanceCalculator(),
                new SplitAmountPaidBalanceCalculator());
        this.balanceService = new BalanceServiceImpl(inventoryServiceClientMock,
                Executors.newVirtualThreadPerTaskExecutor(), balanceCalculators);
    }

    @Test
    void shouldCalculateBalanceForSingleExpenseAndTwoMembers() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());
        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                        .name("test expense 1")
                        .resolved(false)
                        .paidByUserId(USER_ONE)
                        .splitType(SplitType.SplitBetweenGroupMembers)
                        .amount(BigDecimal.valueOf(20.0))
                        .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Minus), resultForUserTwo);
    }

    @Test
    void shouldCalculateBalanceForSingleExpenseAndFourMembers() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createFourMembers());
        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                        .name("test expense 1")
                        .resolved(false)
                        .paidByUserId(USER_ONE)
                        .splitType(SplitType.SplitBetweenGroupMembers)
                        .amount(BigDecimal.valueOf(40.0))
                        .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);
        var resultForUserThree = balanceService.calculateBalance(USER_THREE, BALANCE_GROUP_ID);
        var resultForUserFour = balanceService.calculateBalance(USER_FOUR, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("30.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Minus), resultForUserTwo);
        assertEquals(new BalanceDto(USER_THREE, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Minus), resultForUserThree);
        assertEquals(new BalanceDto(USER_FOUR, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Minus), resultForUserFour);
    }

    @Test
    void shouldCalculateBalanceForThreeExpenseAndTwoMembers() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());
        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(false)
                                .paidByUserId(USER_TWO)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 3")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40.0))
                                .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("20.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("20.00"), BalanceType.Minus), resultForUserTwo);
    }

    @Test
    void shouldCalculateBalanceForThreeExpenseAndFourMembers() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createFourMembers());

        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(false)
                                .paidByUserId(USER_TWO)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 3")
                                .resolved(false)
                                .paidByUserId(USER_THREE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40))
                                .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);
        var resultForUserThree = balanceService.calculateBalance(USER_THREE, BALANCE_GROUP_ID);
        var resultForUserFour = balanceService.calculateBalance(USER_FOUR, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Plus), resultForUserTwo);
        assertEquals(new BalanceDto(USER_THREE, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Plus), resultForUserThree);
        assertEquals(new BalanceDto(USER_FOUR, BALANCE_GROUP_ID, new BigDecimal("30.00"), BalanceType.Minus), resultForUserFour);
    }

    @Test
    void shouldCalculateBalanceForThreeExpenseWithSplitAndFullOwnAndTwoMembers() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());

        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(false)
                                .paidByUserId(USER_TWO)
                                .needToPayUserId(USER_ONE)
                                .splitType(SplitType.FullAmountForSingleGroupMember)
                                .amount(BigDecimal.valueOf(40.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 3")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(40.0))
                                .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("0.00"), BalanceType.Resolved), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("0.00"), BalanceType.Resolved), resultForUserTwo);
    }

    @Test
    void shouldCalculateBalanceForThreeExpensesAndOneMember() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(List.of(BalanceGroupMemberDto.builder()
                        .id(USER_ONE)
                        .build()));

        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(10.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(10.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 3")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(10.0))
                                .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("30.00"), BalanceType.Minus), resultForUserOne);
    }

    @Test
    void shouldCalculateBalanceForEmptyExpensesAndOneMember() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(List.of(BalanceGroupMemberDto.builder()
                        .id(USER_ONE)
                        .build()));
        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of());

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("0.00"), BalanceType.Resolved), resultForUserOne);
    }

    @Test
    void shouldCalculateBalanceForEmptyExpensesAndTwoMember() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());
        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of());

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("0.00"), BalanceType.Resolved), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("0.00"), BalanceType.Resolved), resultForUserTwo);
    }

    @Test
    void shouldSkipResolvedExpenses() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());

        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(20.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(true)
                                .paidByUserId(USER_TWO)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(20.0))
                                .build()));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("10.00"), BalanceType.Minus), resultForUserTwo);
    }

    @Test
    void simpleTestForTwoUsersAndTwoExpenses() {
        when(inventoryServiceClientMock.findAllBalanceGroupMembers(BALANCE_GROUP_ID))
                .thenReturn(createTwoMembers());

        when(inventoryServiceClientMock.findAllExpensesFromBalanceGroup(BALANCE_GROUP_ID))
                .thenReturn(List.of(ExpenseDto.builder()
                                .name("test expense 1")
                                .resolved(false)
                                .paidByUserId(USER_ONE)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(100.0))
                                .build(),
                        ExpenseDto.builder()
                                .name("test expense 2")
                                .resolved(false)
                                .paidByUserId(USER_TWO)
                                .splitType(SplitType.SplitBetweenGroupMembers)
                                .amount(BigDecimal.valueOf(50.0))
                                .build()
                ));

        var resultForUserOne = balanceService.calculateBalance(USER_ONE, BALANCE_GROUP_ID);
        var resultForUserTwo = balanceService.calculateBalance(USER_TWO, BALANCE_GROUP_ID);

        assertEquals(new BalanceDto(USER_ONE, BALANCE_GROUP_ID, new BigDecimal("25.00"), BalanceType.Plus), resultForUserOne);
        assertEquals(new BalanceDto(USER_TWO, BALANCE_GROUP_ID, new BigDecimal("25.00"), BalanceType.Minus), resultForUserTwo);
    }

    private List<BalanceGroupMemberDto> createTwoMembers() {
        return List.of(BalanceGroupMemberDto.builder()
                        .id(USER_ONE)
                        .build(),
                BalanceGroupMemberDto.builder()
                        .id(USER_TWO)
                        .build());
    }

    private List<BalanceGroupMemberDto> createFourMembers() {
        return List.of(BalanceGroupMemberDto.builder()
                        .id(USER_ONE)
                        .build(),
                BalanceGroupMemberDto.builder()
                        .id(USER_TWO)
                        .build(),
                BalanceGroupMemberDto.builder()
                        .id(USER_THREE)
                        .build(),
                BalanceGroupMemberDto.builder()
                        .id(USER_FOUR)
                        .build());
    }
}
