package com.bank.service;

import com.bank.dao.AlerteDAO;
import com.bank.dao.OperationDAO;
import com.bank.entity.AlerteFraude;
import com.bank.entity.NiveauAlerte;
import com.bank.entity.OperationCarte;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FraudeService {
    private final OperationDAO operationDAO;
    private final AlerteDAO alertDAO;
    private final CarteService cardService;

    private static final BigDecimal SUSPICIOUS_AMOUNT = new BigDecimal("5000");
    private static final long SUSPICIOUS_MINUTES_BETWEEN_OPERATIONS = 30;

    public FraudeService() {
        this.operationDAO = new OperationDAO();
        this.alertDAO = new AlerteDAO();
        this.cardService = new CarteService();
    }

    public void detectFraud(int cardId) throws SQLException {
        List<OperationCarte> operations = operationDAO.findByCardId(cardId);

        if (operations.isEmpty()) {
            return;
        }

        detectHighAmounts(operations);
        detectRapidOperations(operations);
        detectMultipleAttempts(operations);
    }

    private void detectHighAmounts(List<OperationCarte> operations) throws SQLException {
        for (OperationCarte op : operations) {
            if (op.amount().compareTo(SUSPICIOUS_AMOUNT) > 0) {
                String description = String.format(
                    "High amount detected: %.2f EUR at %s on %s",
                    op.amount(),
                    op.location(),
                    op.date()
                );
                createAlert(op.cardId(), description, NiveauAlerte.WARNING);
            }
        }
    }

    private void detectRapidOperations(List<OperationCarte> operations) throws SQLException {
        for (int i = 0; i < operations.size() - 1; i++) {
            OperationCarte op1 = operations.get(i);
            OperationCarte op2 = operations.get(i + 1);

            Duration duration = Duration.between(op2.date(), op1.date());
            long minutesDiff = Math.abs(duration.toMinutes());

            if (minutesDiff <= SUSPICIOUS_MINUTES_BETWEEN_OPERATIONS && !op1.location().equals(op2.location())) {
                String description = String.format(
                    "Suspicious operations: %s at %s and %s at %s in %d minutes",
                    op1.location(),
                    op1.date(),
                    op2.location(),
                    op2.date(),
                    minutesDiff
                );
                createAlert(op1.cardId(), description, NiveauAlerte.CRITICAL);

                cardService.blockCard(op1.cardId());
            }
        }
    }

    private void detectMultipleAttempts(List<OperationCarte> operations) throws SQLException {
        if (operations.size() < 5) {
            return;
        }

        for (int i = 0; i < operations.size() - 4; i++) {
            OperationCarte first = operations.get(i);
            OperationCarte fifth = operations.get(i + 4);

            Duration duration = Duration.between(fifth.date(), first.date());
            long minutesDiff = Math.abs(duration.toMinutes());

            if (minutesDiff <= 60) {
                String description = String.format(
                    "Multiple attempts detected: 5+ operations in %d minutes",
                    minutesDiff
                );
                createAlert(first.cardId(), description, NiveauAlerte.CRITICAL);
                cardService.suspendCard(first.cardId());
            }
        }
    }

    public AlerteFraude createAlert(int cardId, String description, NiveauAlerte level) throws SQLException {
        AlerteFraude alert = new AlerteFraude(
            0,
            description,
            level,
            cardId,
            LocalDateTime.now()
        );
        return alertDAO.save(alert);
    }

    public List<AlerteFraude> getCardAlerts(int cardId) throws SQLException {
        return alertDAO.findByCarteId(cardId);
    }

    public List<AlerteFraude> getAllAlerts() throws SQLException {
        return alertDAO.findAll();
    }

    public List<AlerteFraude> getCriticalAlerts() throws SQLException {
        return alertDAO.findCriticalAlerts();
    }

    public List<AlerteFraude> getAlertsByLevel(NiveauAlerte level) throws SQLException {
        return alertDAO.findByLevel(level);
    }

    public boolean deleteAlert(int id) throws SQLException {
        return alertDAO.delete(id);
    }
}
