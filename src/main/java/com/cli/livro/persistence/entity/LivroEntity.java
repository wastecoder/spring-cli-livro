package com.cli.livro.persistence.entity;

import lombok.Data;

@Data
public class LivroEntity {
    private Long id;
    private String titulo;
    private String autor;
    private Integer anoPublicacao;
}
