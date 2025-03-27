package com.cli.livro.service;

import com.cli.livro.persistence.dao.LivroDAO;
import com.cli.livro.persistence.entity.LivroEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class LivroService {
    private final Connection connection;

    public LivroEntity insert(final LivroEntity entity) throws SQLException {
        var dao = new LivroDAO(connection);

        try {
            dao.insert(entity);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

        return entity;
    }

    public boolean delete(final Long id) throws SQLException {
        var dao = new LivroDAO(connection);

        try {
            if (!dao.exists(id)) {
                return false;
            }

            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
