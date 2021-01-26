package io.github.nandandesai.insecure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class InsecureServerApplication {

	@Bean
	public Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {
		final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		final String jdbcUrl = "jdbc:derby:bookshelfdb;create=true";
		Class.forName(driver);
		Connection con = DriverManager.getConnection(jdbcUrl);
		return con;
	}

	public static void main(String[] args) {
		SpringApplication.run(InsecureServerApplication.class, args);
	}

}
