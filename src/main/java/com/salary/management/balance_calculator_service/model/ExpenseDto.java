package com.salary.management.balance_calculator_service.model;

import com.salary.management.balance_calculator_service.model.types.SplitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExpenseDto extends AbstractDto {

    private String name;
    private BigDecimal amount;
    private UUID paidByUserId;
    private UUID needToPayUserId;
    private SplitType splitType;
    private boolean resolved;
}
