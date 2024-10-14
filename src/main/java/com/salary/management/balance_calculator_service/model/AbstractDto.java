package com.salary.management.balance_calculator_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractDto {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
}
