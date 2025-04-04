package com.cli.livro.ui;

import com.cli.livro.config.ConnectionConfig;
import com.cli.livro.persistence.entity.LivroEntity;
import com.cli.livro.service.LivroService;

import java.sql.SQLException;
import java.util.Scanner;

public class LivroMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() throws SQLException {
        System.out.println("\nBem-vindo ao gerenciador de livros, escolha a opção desejada");
        while (true) {
            System.out.println("\n+=============================+");
            System.out.println("|        MENU DE LIVROS       |");
            System.out.println("+=============================+");
            System.out.println("| 1 - Cadastrar um novo livro |");
            System.out.println("| 2 - Listar todos os livros  |");
            System.out.println("| 3 - Buscar livro por título |");
            System.out.println("| 4 - Buscar livro por ID     |");
            System.out.println("| 5 - Atualizar um livro      |");
            System.out.println("| 6 - Excluir um livro        |");
            System.out.println("| 7 - Sair                    |");
            System.out.println("+=============================+");

            inputCharacter();
            int option = scanner.nextInt();
            switch (option) {
                case 1 -> cadastrarLivro();
                case 2 -> listarLivros();
                case 3 -> buscarLivroPorTitulo();
                case 4 -> buscarLivroPorId();
                case 5 -> atualizarLivro();
                case 6 -> excluirLivro();
                case 7 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void cadastrarLivro() throws SQLException {
        System.out.println("\n>>>> CADASTRANDO UM NOVO LIVRO");

        System.out.println("Informe o título do livro");
        inputCharacter();
        String titulo = scanner.next();

        System.out.println("Informe o autor do livro");
        inputCharacter();
        String autor = scanner.next();

        System.out.println("Informe o ano de publicação (opcional, pressione Enter para ignorar)");
        inputCharacter();
        String anoInput = scanner.next();
        Integer anoPublicacao = anoInput.isEmpty() ? null : Integer.parseInt(anoInput);

        var livro = new LivroEntity();
        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setAnoPublicacao(anoPublicacao);

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            service.insert(livro);
            System.out.println("Livro cadastrado com sucesso!");
        }
    }

    private void listarLivros() throws SQLException {
        System.out.println("\n>>>> LISTANDO TODOS OS LIVROS");

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            var livros = service.findAll();

            if (livros.isEmpty()) {
                System.out.println("Nenhum livro cadastrado.");
            } else {
                livros.forEach(livro -> System.out.printf("ID: %d | Título: %s | Autor: %s | Ano: %s\n",
                        livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getAnoPublicacao() != null ? livro.getAnoPublicacao().toString() : "N/A"));
            }
        }
    }

    private void buscarLivroPorTitulo() throws SQLException {
        System.out.println("\n>>>> BUSCANDO LIVROS PELO TÍTULO");

        System.out.println("Digite parte do título do livro que deseja buscar:");
        inputCharacter();
        var tituloParcial = scanner.next();

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            var livros = service.findByTituloParcial(tituloParcial);

            if (livros.isEmpty()) {
                System.out.println("Nenhum livro encontrado com esse título.");
            } else {
                livros.forEach(livro -> System.out.printf("ID: %d | Título: %s | Autor: %s | Ano: %s\n",
                        livro.getId(), livro.getTitulo(), livro.getAutor(),
                        livro.getAnoPublicacao() != null ? livro.getAnoPublicacao() : "N/A"));
            }
        }
    }

    private void buscarLivroPorId() throws SQLException {
        System.out.println("\n>>>> BUSCANDO LIVRO POR ID");

        System.out.println("Digite o ID do livro que deseja buscar:");
        inputCharacter();
        var id = scanner.nextLong();

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            var optionalLivro = service.findById(id);

            optionalLivro.ifPresentOrElse(
                    livro -> System.out.printf("ID: %d | Título: %s | Autor: %s | Ano: %s\n",
                            livro.getId(), livro.getTitulo(), livro.getAutor(),
                            livro.getAnoPublicacao() != null ? livro.getAnoPublicacao() : "N/A"),
                    () -> System.out.printf("Nenhum livro encontrado com ID %d\n", id)
            );
        }
    }

    private void atualizarLivro() throws SQLException {
        System.out.println("\n>>>> ATUALIZANDO LIVRO");

        System.out.println("Digite o ID do livro que deseja atualizar:");
        inputCharacter();
        var id = scanner.nextLong();

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            var optionalLivro = service.findById(id);

            if (optionalLivro.isEmpty()) {
                System.out.printf("Nenhum livro encontrado com ID %d\n", id);
                return;
            }

            var livro = optionalLivro.get();
            System.out.println("\n>>>> OBS: Nos campos abaixo, deixe vazio (ENTER) para não alterar.\n");

            // BUGFIX: Limpa o ENTER pendente antes de começar
            scanner.nextLine();

            System.out.println("Digite o novo título:");
            inputCharacter();
            var novoTitulo = scanner.nextLine();
            if (!novoTitulo.isEmpty()) {
                livro.setTitulo(novoTitulo);
            }

            System.out.println("Digite o novo autor:");
            inputCharacter();
            var novoAutor = scanner.nextLine();
            if (!novoAutor.isEmpty()) {
                livro.setAutor(novoAutor);
            }

            System.out.println("Digite o novo ano de publicação:");
            inputCharacter();
            var inputAno = scanner.nextLine();
            if (!inputAno.isEmpty()) {
                livro.setAnoPublicacao(Integer.parseInt(inputAno));
            }

            if (service.update(livro)) {
                System.out.println("Livro atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar o livro.");
            }
        }
    }

    private void excluirLivro() throws SQLException {
        System.out.println("\n>>>> EXCLUINDO UM LIVRO");
        System.out.println("Informe o ID do livro que será excluído");
        inputCharacter();
        Long id = scanner.nextLong();

        try (var connection = ConnectionConfig.getConnection()) {
            var service = new LivroService(connection);
            if (service.delete(id)) {
                System.out.printf("O livro %s foi excluído com sucesso.\n", id);
            } else {
                System.out.printf("Não foi encontrado um livro com ID %s.\n", id);
            }
        }
    }

    private void inputCharacter() {
        System.out.print(">> ");
    }
}
