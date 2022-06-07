package io.github.nandandesai.insecure;

import io.github.nandandesai.insecure.configs.UserDataPaths;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
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

	@Bean
	public UserDataPaths getUserDataPaths() {
		String currentDirectory = System.getProperty("user.dir");
		String userDataRootPath = currentDirectory + File.separator + "userdata";
		String userPhotoDir = userDataRootPath + File.separator + "userphotos" + File.separator;
		String booksDir = userDataRootPath + File.separator + "books";
		String bookCoversDir = booksDir + File.separator + "covers" + File.separator;
		String bookPdfsDir = booksDir + File.separator + "pdfs" + File.separator;
		return new UserDataPaths()
				.setUserPhotoDirPath(userPhotoDir)
				.setBookCoversPath(bookCoversDir)
				.setBookPdfsPath(bookPdfsDir)
				.setCurrentJarPath(currentDirectory + File.separator);
	}

	public static void main(String[] args) {
		SpringApplication.run(InsecureServerApplication.class, args);
	}

}
