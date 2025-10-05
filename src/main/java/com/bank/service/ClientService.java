package com.bank.service;

import com.bank.dao.ClientDAO;
import com.bank.entity.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public Client createClient(String name, String email, String phone) throws SQLException {
        Optional<Client> existing = clientDAO.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A client with this email already exists");
        }

        Client client = new Client(0, name, email, phone);
        return clientDAO.save(client);
    }

    public Optional<Client> getClient(int id) throws SQLException {
        return clientDAO.findById(id);
    }

    public List<Client> getAllClients() throws SQLException {
        return clientDAO.findAll();
    }

    public Optional<Client> searchByEmail(String email) throws SQLException {
        return clientDAO.findByEmail(email);
    }

    public Optional<Client> searchByPhone(String phone) throws SQLException {
        return clientDAO.findByPhone(phone);
    }

    public boolean updateClient(Client client) throws SQLException {
        Optional<Client> existing = clientDAO.findById(client.id());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Client not found");
        }

        return clientDAO.update(client);
    }

    public boolean deleteClient(int id) throws SQLException {
        Optional<Client> existing = clientDAO.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Client not found");
        }

        return clientDAO.delete(id);
    }
}
