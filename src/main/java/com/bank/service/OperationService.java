package com.bank.service;

import com.bank.dao.OperationDAO;
import com.bank.entity.OperationCarte;
import com.bank.entity.TypeOperation;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OperationService {
    private final OperationDAO operationDAO;
    private final CarteService cardService;

    public OperationService() {
        this.operationDAO = new OperationDAO();
        this.cardService = new CarteService();
    }

    public OperationCarte recordOperation(int cardId, BigDecimal amount, TypeOperation type, String location) throws SQLException {
        if (!cardService.verifyLimit(cardId, amount)) {
            throw new IllegalArgumentException("Operation refused: limit exceeded or card inactive");
        }

        OperationCarte operation = new OperationCarte(
            0,
            LocalDateTime.now(),
            amount,
            type,
            location,
            cardId
        );

        return operationDAO.save(operation);
    }

    public OperationCarte recordOperationWithDate(int cardId, BigDecimal amount, TypeOperation type, String location, LocalDateTime date) throws SQLException {
        if (!cardService.verifyLimit(cardId, amount)) {
            throw new IllegalArgumentException("Operation refused: limit exceeded or card inactive");
        }

        OperationCarte operation = new OperationCarte(
            0,
            date,
            amount,
            type,
            location,
            cardId
        );

        return operationDAO.save(operation);
    }

    public Optional<OperationCarte> getOperation(int id) throws SQLException {
        return operationDAO.findById(id);
    }

    public List<OperationCarte> getAllOperations() throws SQLException {
        return operationDAO.findAll();
    }

    public List<OperationCarte> getCardOperations(int cardId) throws SQLException {
        return operationDAO.findByCardId(cardId);
    }

    public List<OperationCarte> getOperationsByType(TypeOperation type) throws SQLException {
        return operationDAO.findByType(type);
    }

    public List<OperationCarte> getOperationsByPeriod(LocalDateTime start, LocalDateTime end) throws SQLException {
        return operationDAO.findByDateRange(start, end);
    }

    public List<OperationCarte> getCardOperationsByPeriod(int cardId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return operationDAO.findByCardAndDateRange(cardId, start, end);
    }

    public boolean deleteOperation(int id) throws SQLException {
        return operationDAO.delete(id);
    }
}
