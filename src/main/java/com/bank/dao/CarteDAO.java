package com.bank.dao;

import com.bank.entity.*;
import com.bank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarteDAO {

    public Carte save(Carte card) throws SQLException {
        String sql = "INSERT INTO Carte (numero, dateExpiration, statut, typeCarte, idClient, plafondJournalier, plafondMensuel, tauxInteret, soldeDisponible) VALUES (?, ?, ?::statut_carte, ?::type_carte, ?, ?, ?, ?, ?) RETURNING id";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, card.getNumber());
            stmt.setDate(2, Date.valueOf(card.getExpirationDate()));
            stmt.setString(3, card.getStatus().name());
            stmt.setString(4, card.getCardType().name());
            stmt.setInt(5, card.getClientId());

            if (card instanceof CarteDebit cd) {
                stmt.setBigDecimal(6, cd.getDailyLimit());
                stmt.setNull(7, Types.NUMERIC);
                stmt.setNull(8, Types.NUMERIC);
                stmt.setNull(9, Types.NUMERIC);
            } else if (card instanceof CarteCredit cc) {
                stmt.setNull(6, Types.NUMERIC);
                stmt.setBigDecimal(7, cc.getMonthlyLimit());
                stmt.setBigDecimal(8, cc.getInterestRate());
                stmt.setNull(9, Types.NUMERIC);
            } else if (card instanceof CartePrepayee cp) {
                stmt.setNull(6, Types.NUMERIC);
                stmt.setNull(7, Types.NUMERIC);
                stmt.setNull(8, Types.NUMERIC);
                stmt.setBigDecimal(9, cp.getAvailableBalance());
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                card.setId(id);
                return card;
            }
            throw new SQLException("Failed to create card");
        }
    }

    public Optional<Carte> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCard(rs));
            }
            return Optional.empty();
        }
    }

    public List<Carte> findAll() throws SQLException {
        String sql = "SELECT * FROM Carte";
        List<Carte> cards = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    public List<Carte> findByClientId(int clientId) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE idClient = ?";
        List<Carte> cards = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    public boolean update(Carte card) throws SQLException {
        String sql = "UPDATE Carte SET numero = ?, dateExpiration = ?, statut = ?::statut_carte, plafondJournalier = ?, plafondMensuel = ?, tauxInteret = ?, soldeDisponible = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, card.getNumber());
            stmt.setDate(2, Date.valueOf(card.getExpirationDate()));
            stmt.setString(3, card.getStatus().name());

            if (card instanceof CarteDebit cd) {
                stmt.setBigDecimal(4, cd.getDailyLimit());
                stmt.setNull(5, Types.NUMERIC);
                stmt.setNull(6, Types.NUMERIC);
                stmt.setNull(7, Types.NUMERIC);
            } else if (card instanceof CarteCredit cc) {
                stmt.setNull(4, Types.NUMERIC);
                stmt.setBigDecimal(5, cc.getMonthlyLimit());
                stmt.setBigDecimal(6, cc.getInterestRate());
                stmt.setNull(7, Types.NUMERIC);
            } else if (card instanceof CartePrepayee cp) {
                stmt.setNull(4, Types.NUMERIC);
                stmt.setNull(5, Types.NUMERIC);
                stmt.setNull(6, Types.NUMERIC);
                stmt.setBigDecimal(7, cp.getAvailableBalance());
            }

            stmt.setInt(8, card.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Carte WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private Carte mapResultSetToCard(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String number = rs.getString("numero");
        LocalDate expirationDate = rs.getDate("dateExpiration").toLocalDate();
        StatutCarte status = StatutCarte.valueOf(rs.getString("statut"));
        TypeCarte cardType = TypeCarte.valueOf(rs.getString("typeCarte"));
        int clientId = rs.getInt("idClient");

        return switch (cardType) {
            case DEBIT -> new CarteDebit(
                id, number, expirationDate, status, clientId,
                rs.getBigDecimal("plafondJournalier")
            );
            case CREDIT -> new CarteCredit(
                id, number, expirationDate, status, clientId,
                rs.getBigDecimal("plafondMensuel"),
                rs.getBigDecimal("tauxInteret")
            );
            case PREPAYEE -> new CartePrepayee(
                id, number, expirationDate, status, clientId,
                rs.getBigDecimal("soldeDisponible")
            );
        };
    }
}
