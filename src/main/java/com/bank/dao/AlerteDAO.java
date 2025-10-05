package com.bank.dao;

import com.bank.entity.AlerteFraude;
import com.bank.entity.NiveauAlerte;
import com.bank.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlerteDAO {

    public AlerteFraude save(AlerteFraude alert) throws SQLException {
        String sql = "INSERT INTO AlerteFraude (description, niveau, idCarte, dateCreation) VALUES (?, ?::niveau_alerte, ?, ?) RETURNING id";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, alert.description());
            stmt.setString(2, alert.level().name());
            stmt.setInt(3, alert.cardId());
            stmt.setTimestamp(4, Timestamp.valueOf(alert.creationDate()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new AlerteFraude(id, alert.description(), alert.level(), alert.cardId(), alert.creationDate());
            }
            throw new SQLException("Failed to create alert");
        }
    }

    public Optional<AlerteFraude> findById(int id) throws SQLException {
        String sql = "SELECT * FROM AlerteFraude WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAlert(rs));
            }
            return Optional.empty();
        }
    }

    public List<AlerteFraude> findAll() throws SQLException {
        String sql = "SELECT * FROM AlerteFraude ORDER BY dateCreation DESC";
        List<AlerteFraude> alerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        }
        return alerts;
    }

    public List<AlerteFraude> findByCarteId(int cardId) throws SQLException {
        String sql = "SELECT * FROM AlerteFraude WHERE idCarte = ? ORDER BY dateCreation DESC";
        List<AlerteFraude> alerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        }
        return alerts;
    }

    public List<AlerteFraude> findByLevel(NiveauAlerte level) throws SQLException {
        String sql = "SELECT * FROM AlerteFraude WHERE niveau = ?::niveau_alerte ORDER BY dateCreation DESC";
        List<AlerteFraude> alerts = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, level.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                alerts.add(mapResultSetToAlert(rs));
            }
        }
        return alerts;
    }

    public List<AlerteFraude> findCriticalAlerts() throws SQLException {
        return findByLevel(NiveauAlerte.CRITICAL);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM AlerteFraude WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private AlerteFraude mapResultSetToAlert(ResultSet rs) throws SQLException {
        return new AlerteFraude(
            rs.getInt("id"),
            rs.getString("description"),
            NiveauAlerte.valueOf(rs.getString("niveau")),
            rs.getInt("idCarte"),
            rs.getTimestamp("dateCreation").toLocalDateTime()
        );
    }
}
