package com.cli.livro.service;

import com.cli.livro.persistence.dao.LivroDAO;
import com.cli.livro.persistence.entity.LivroEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public Optional<LivroEntity> findById(final Long id) throws SQLException {
        var dao = new LivroDAO(connection);
        return dao.findById(id);
    }

    public List<LivroEntity> findByTituloParcial(final String tituloParcial) throws SQLException {
        var dao = new LivroDAO(connection);
        return dao.findByTituloParcial(tituloParcial);
    }

    public List<LivroEntity> findAll() throws SQLException {
        var dao = new LivroDAO(connection);
        return dao.findAll();
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

    public boolean update(final LivroEntity livro) throws SQLException {
        var dao = new LivroDAO(connection);

        try {
            if (!dao.exists(livro.getId())) {
                return false;
            }

            boolean updated = dao.update(livro);
            connection.commit();
            return updated;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
