package com.bank.dao;

import com.bank.entity.OperationCarte;
import com.bank.entity.TypeOperation;
import com.bank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationDAO {

    public OperationCarte save(OperationCarte operation) throws SQLException {
        String sql = "INSERT INTO OperationCarte (date, montant, type, lieu, idCarte) VALUES (?, ?, ?::type_operation, ?, ?) RETURNING id";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(operation.date()));
            stmt.setBigDecimal(2, operation.amount());
            stmt.setString(3, operation.type().name());
            stmt.setString(4, operation.location());
            stmt.setInt(5, operation.cardId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new OperationCarte(id, operation.date(), operation.amount(), operation.type(), operation.location(), operation.cardId());
            }
            throw new SQLException("Failed to create operation");
        }
    }

    public Optional<OperationCarte> findById(int id) throws SQLException {
        String sql = "SELECT * FROM OperationCarte WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToOperation(rs));
            }
            return Optional.empty();
        }
    }

    public List<OperationCarte> findAll() throws SQLException {
        String sql = "SELECT * FROM OperationCarte";
        List<OperationCarte> operations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    public List<OperationCarte> findByCardId(int cardId) throws SQLException {
        String sql = "SELECT * FROM OperationCarte WHERE idCarte = ? ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    public List<OperationCarte> findByType(TypeOperation type) throws SQLException {
        String sql = "SELECT * FROM OperationCarte WHERE type = ?::type_operation";
        List<OperationCarte> operations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    public List<OperationCarte> findByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT * FROM OperationCarte WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    public List<OperationCarte> findByCardAndDateRange(int cardId, LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = "SELECT * FROM OperationCarte WHERE idCarte = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
        List<OperationCarte> operations = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
        }
        return operations;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM OperationCarte WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private OperationCarte mapResultSetToOperation(ResultSet rs) throws SQLException {
        return new OperationCarte(
            rs.getInt("id"),
            rs.getTimestamp("date").toLocalDateTime(),
            rs.getBigDecimal("montant"),
            TypeOperation.valueOf(rs.getString("type")),
            rs.getString("lieu"),
            rs.getInt("idCarte")
        );
    }
}
