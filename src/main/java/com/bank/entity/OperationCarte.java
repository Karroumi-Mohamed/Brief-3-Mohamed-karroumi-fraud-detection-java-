package com.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationCarte(
    int id,
    LocalDateTime date,
    BigDecimal amount,
    TypeOperation type,
    String location,
    int cardId
) {}
