package com.bank.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class CartePrepayee extends Carte {
    private BigDecimal availableBalance;

    public CartePrepayee(int id, String number, LocalDate expirationDate, StatutCarte status, int clientId, BigDecimal availableBalance) {
        super(id, number, expirationDate, status, clientId);
        this.availableBalance = availableBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
}
