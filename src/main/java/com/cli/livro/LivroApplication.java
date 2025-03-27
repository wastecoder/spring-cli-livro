package com.cli.livro;

import com.cli.livro.persistence.migration.MigrationStrategy;
import com.cli.livro.ui.LivroMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.SQLException;

import static com.cli.livro.config.ConnectionConfig.getConnection;

@SpringBootApplication
public class LivroApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivroApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {
			try (Connection connection = getConnection()) {
				new MigrationStrategy(connection).executeMigration();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			new LivroMenu().execute();
		};
	}

}
