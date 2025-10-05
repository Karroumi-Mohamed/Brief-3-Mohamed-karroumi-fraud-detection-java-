package com.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class CarteCredit extends Carte {
    private BigDecimal monthlyLimit;
    private BigDecimal interestRate;

    public CarteCredit(int id, String number, LocalDate expirationDate, StatutCarte status, int clientId, BigDecimal monthlyLimit, BigDecimal interestRate) {
        super(id, number, expirationDate, status, clientId);
        this.monthlyLimit = monthlyLimit;
        this.interestRate = interestRate;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
