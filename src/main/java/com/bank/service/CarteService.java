package com.bank.service;

import com.bank.dao.CarteDAO;
import com.bank.entity.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CarteService {
    private final CarteDAO carteDAO;
    private final Random random;

    public CarteService() {
        this.carteDAO = new CarteDAO();
        this.random = new Random();
    }

    private String generateCardNumber() {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    public CarteDebit createDebitCard(int clientId, BigDecimal dailyLimit) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        CarteDebit card = new CarteDebit(0, number, expiration, StatutCarte.ACTIVE, clientId, dailyLimit);
        return (CarteDebit) carteDAO.save(card);
    }

    public CarteCredit createCreditCard(int clientId, BigDecimal monthlyLimit, BigDecimal interestRate) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        CarteCredit card = new CarteCredit(0, number, expiration, StatutCarte.ACTIVE, clientId, monthlyLimit, interestRate);
        return (CarteCredit) carteDAO.save(card);
    }

    public CartePrepayee createPrepaidCard(int clientId, BigDecimal initialBalance) throws SQLException {
        String number = generateCardNumber();
        LocalDate expiration = LocalDate.now().plusYears(3);

        CartePrepayee card = new CartePrepayee(0, number, expiration, StatutCarte.ACTIVE, clientId, initialBalance);
        return (CartePrepayee) carteDAO.save(card);
    }

    public boolean activateCard(int cardId) throws SQLException {
        Optional<Carte> cardOpt = carteDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Carte card = cardOpt.get();
        if (card.getStatus() == StatutCarte.ACTIVE) {
            throw new IllegalStateException("Card is already active");
        }

        card.setStatus(StatutCarte.ACTIVE);
        return carteDAO.update(card);
    }

    public boolean suspendCard(int cardId) throws SQLException {
        Optional<Carte> cardOpt = carteDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Carte card = cardOpt.get();
        card.setStatus(StatutCarte.SUSPENDED);
        return carteDAO.update(card);
    }

    public boolean blockCard(int cardId) throws SQLException {
        Optional<Carte> cardOpt = carteDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Carte card = cardOpt.get();
        card.setStatus(StatutCarte.BLOCKED);
        return carteDAO.update(card);
    }

    public boolean verifyLimit(int cardId, BigDecimal amount) throws SQLException {
        Optional<Carte> cardOpt = carteDAO.findById(cardId);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card not found");
        }

        Carte card = cardOpt.get();

        if (card.getStatus() != StatutCarte.ACTIVE) {
            return false;
        }

        if (card instanceof CarteDebit cd) {
            return amount.compareTo(cd.getDailyLimit()) <= 0;
        } else if (card instanceof CarteCredit cc) {
            return amount.compareTo(cc.getMonthlyLimit()) <= 0;
        } else if (card instanceof CartePrepayee cp) {
            return amount.compareTo(cp.getAvailableBalance()) <= 0;
        }

        return false;
    }

    public Optional<Carte> getCard(int id) throws SQLException {
        return carteDAO.findById(id);
    }

    public List<Carte> getClientCards(int clientId) throws SQLException {
        return carteDAO.findByClientId(clientId);
    }

    public List<Carte> getAllCards() throws SQLException {
        return carteDAO.findAll();
    }

    public boolean updateCard(Carte card) throws SQLException {
        return carteDAO.update(card);
    }

    public boolean deleteCard(int id) throws SQLException {
        return carteDAO.delete(id);
    }
}
