package com.bank.dao;

import com.bank.entity.Client;
import com.bank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {

    public Client save(Client client) throws SQLException {
        String sql = "INSERT INTO Client (nom, email, telephone) VALUES (?, ?, ?) RETURNING id";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.name());
            stmt.setString(2, client.email());
            stmt.setString(3, client.phone());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new Client(id, client.name(), client.email(), client.phone());
            }
            throw new SQLException("Failed to create client");
        }
    }

    public Optional<Client> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Client WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                return Optional.of(client);
            }
            return Optional.empty();
        }
    }

    public List<Client> findAll() throws SQLException {
        String sql = "SELECT * FROM Client";
        List<Client> clients = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                clients.add(client);
            }
        }
        return clients;
    }

    public Optional<Client> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Client WHERE email = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                return Optional.of(client);
            }
            return Optional.empty();
        }
    }

    public Optional<Client> findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM Client WHERE telephone = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                return Optional.of(client);
            }
            return Optional.empty();
        }
    }

    public boolean update(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, email = ?, telephone = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.name());
            stmt.setString(2, client.email());
            stmt.setString(3, client.phone());
            stmt.setInt(4, client.id());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Client WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
