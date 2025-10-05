package com.bank.entity;

import java.time.LocalDate;

public sealed class Carte permits CarteDebit, CarteCredit, CartePrepayee {
    private int id;
    private String number;
    private LocalDate expirationDate;
    private StatutCarte status;
    private int clientId;

    public Carte(int id, String number, LocalDate expirationDate, StatutCarte status, int clientId) {
        this.id = id;
        this.number = number;
        this.expirationDate = expirationDate;
        this.status = status;
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public StatutCarte getStatus() {
        return status;
    }

    public int getClientId() {
        return clientId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setStatus(StatutCarte status) {
        this.status = status;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public TypeCarte getCardType() {
        if (this instanceof CarteDebit) {
            return TypeCarte.DEBIT;
        } else if (this instanceof CarteCredit) {
            return TypeCarte.CREDIT;
        } else if (this instanceof CartePrepayee) {
            return TypeCarte.PREPAYEE;
        }
        throw new IllegalStateException("Unknown card type");
    }
}

