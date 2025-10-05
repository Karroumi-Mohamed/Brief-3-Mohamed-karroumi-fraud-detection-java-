package com.bank.entity;

import java.time.LocalDateTime;

public record AlerteFraude(
    int id,
    String description,
    NiveauAlerte level,
    int cardId,
    LocalDateTime creationDate
) {}
