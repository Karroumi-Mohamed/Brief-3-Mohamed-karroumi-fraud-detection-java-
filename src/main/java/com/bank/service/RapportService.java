package com.bank.service;

import com.bank.dao.CarteDAO;
import com.bank.dao.OperationDAO;
import com.bank.entity.Carte;
import com.bank.entity.OperationCarte;
import com.bank.entity.StatutCarte;
import com.bank.entity.TypeOperation;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class RapportService {
    private final CarteDAO carteDAO;
    private final OperationDAO operationDAO;

    public RapportService() {
        this.carteDAO = new CarteDAO();
        this.operationDAO = new OperationDAO();
    }

    public List<Map.Entry<Integer, Long>> getTop5MostUsedCards() throws SQLException {
        List<OperationCarte> operations = operationDAO.findAll();

        Map<Integer, Long> operationsPerCard = operations.stream()
            .collect(Collectors.groupingBy(
                OperationCarte::cardId,
                Collectors.counting()
            ));

        return operationsPerCard.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
    }

    public Map<TypeOperation, BigDecimal> getMonthlyStatistics(YearMonth month) throws SQLException {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

        List<OperationCarte> operations = operationDAO.findByDateRange(start, end);

        return operations.stream()
            .collect(Collectors.groupingBy(
                OperationCarte::type,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    OperationCarte::amount,
                    BigDecimal::add
                )
            ));
    }

    public Map<TypeOperation, Long> getMonthlyOperationCount(YearMonth month) throws SQLException {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(23, 59, 59);

        List<OperationCarte> operations = operationDAO.findByDateRange(start, end);

        return operations.stream()
            .collect(Collectors.groupingBy(
                OperationCarte::type,
                Collectors.counting()
            ));
    }

    public List<Carte> getBlockedCards() throws SQLException {
        List<Carte> cards = carteDAO.findAll();

        return cards.stream()
            .filter(card -> card.getStatus() == StatutCarte.BLOCKED)
            .collect(Collectors.toList());
    }

    public List<Carte> getSuspendedCards() throws SQLException {
        List<Carte> cards = carteDAO.findAll();

        return cards.stream()
            .filter(card -> card.getStatus() == StatutCarte.SUSPENDED)
            .collect(Collectors.toList());
    }

    public List<Carte> getSuspiciousCards() throws SQLException {
        List<Carte> cards = carteDAO.findAll();

        return cards.stream()
            .filter(card -> card.getStatus() == StatutCarte.BLOCKED ||
                           card.getStatus() == StatutCarte.SUSPENDED)
            .collect(Collectors.toList());
    }

    public BigDecimal getTotalAmountForPeriod(LocalDateTime start, LocalDateTime end) throws SQLException {
        List<OperationCarte> operations = operationDAO.findByDateRange(start, end);

        return operations.stream()
            .map(OperationCarte::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAverageOperationAmount() throws SQLException {
        List<OperationCarte> operations = operationDAO.findAll();

        if (operations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = operations.stream()
            .map(OperationCarte::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(new BigDecimal(operations.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    public Map<Integer, Long> getOperationCountPerCard() throws SQLException {
        List<OperationCarte> operations = operationDAO.findAll();

        return operations.stream()
            .collect(Collectors.groupingBy(
                OperationCarte::cardId,
                Collectors.counting()
            ));
    }

    public Map<Integer, BigDecimal> getTotalAmountPerCard() throws SQLException {
        List<OperationCarte> operations = operationDAO.findAll();

        return operations.stream()
            .collect(Collectors.groupingBy(
                OperationCarte::cardId,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    OperationCarte::amount,
                    BigDecimal::add
                )
            ));
    }

    public String generateCompleteReport() throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("=== BANK REPORT ===\n\n");

        report.append("Top 5 Most Used Cards:\n");
        List<Map.Entry<Integer, Long>> top5 = getTop5MostUsedCards();
        for (int i = 0; i < top5.size(); i++) {
            report.append(String.format("%d. Card ID %d: %d operations\n",
                i + 1, top5.get(i).getKey(), top5.get(i).getValue()));
        }

        report.append("\nBlocked Cards: ").append(getBlockedCards().size()).append("\n");
        report.append("Suspended Cards: ").append(getSuspendedCards().size()).append("\n");

        YearMonth currentMonth = YearMonth.now();
        report.append("\nCurrent Month Statistics:\n");
        Map<TypeOperation, Long> stats = getMonthlyOperationCount(currentMonth);
        stats.forEach((type, count) ->
            report.append(String.format("  %s: %d operations\n", type, count))
        );

        return report.toString();
    }
}
