package com.cli.livro.persistence.dao;

import com.cli.livro.persistence.entity.LivroEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class LivroDAO {
    private final Connection connection;

    public LivroEntity insert(final LivroEntity entity) throws SQLException {
        var sql = "INSERT INTO LIVROS (titulo, autor, ano_publicacao) VALUES (?, ?, ?) RETURNING id;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getTitulo());
            statement.setString(2, entity.getAutor());

            if (entity.getAnoPublicacao() != null) {
                statement.setInt(3, entity.getAnoPublicacao());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
        }
        return entity;
    }

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM LIVROS WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<LivroEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, titulo, autor, ano_publicacao FROM LIVROS WHERE id = ?;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var entity = new LivroEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setTitulo(resultSet.getString("titulo"));
                    entity.setAutor(resultSet.getString("autor"));
                    entity.setAnoPublicacao(resultSet.getObject("ano_publicacao", Integer.class));
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    public List<LivroEntity> findByTituloParcial(final String tituloParcial) throws SQLException {
        var sql = "SELECT id, titulo, autor, ano_publicacao FROM LIVROS WHERE titulo ILIKE ?;"; //ilike = sem case-sensitive
        List<LivroEntity> livros = new ArrayList<>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + tituloParcial + "%");

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var entity = new LivroEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setTitulo(resultSet.getString("titulo"));
                    entity.setAutor(resultSet.getString("autor"));
                    entity.setAnoPublicacao(resultSet.getObject("ano_publicacao", Integer.class));
                    livros.add(entity);
                }
            }
        }
        return livros;
    }

    public List<LivroEntity> findAll() throws SQLException {
        var sql = "SELECT id, titulo, autor, ano_publicacao FROM LIVROS;";
        List<LivroEntity> livros = new ArrayList<>();

        try (var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                var entity = new LivroEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setTitulo(resultSet.getString("titulo"));
                entity.setAutor(resultSet.getString("autor"));
                entity.setAnoPublicacao(resultSet.getObject("ano_publicacao", Integer.class)); //Sem getObject() exibe 0 no null
                livros.add(entity);
            }
        }
        return livros;
    }

    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM LIVROS WHERE id = ?;";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
