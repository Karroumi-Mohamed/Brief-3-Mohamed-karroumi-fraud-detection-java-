package com.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class CarteDebit extends Carte {
    private BigDecimal dailyLimit;

    public CarteDebit(int id, String number, LocalDate expirationDate, StatutCarte status, int clientId, BigDecimal dailyLimit) {
        super(id, number, expirationDate, status, clientId);
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
