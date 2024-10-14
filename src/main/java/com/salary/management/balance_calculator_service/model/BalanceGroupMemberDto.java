package com.salary.management.balance_calculator_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BalanceGroupMemberDto extends AbstractDto {

    private UUID userId;
    private String nickname;
}
