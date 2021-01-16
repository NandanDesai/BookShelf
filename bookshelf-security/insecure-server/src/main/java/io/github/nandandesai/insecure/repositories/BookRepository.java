package io.github.nandandesai.insecure.repositories;

import io.github.nandandesai.insecure.models.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Connection con;

    @Autowired
    private RoleRepository roleRepository;

    public void save(Book book) throws SQLException {
        Optional<Book> bookOptional = findById(book.getId());
        if(bookOptional.isPresent()){
            String sql = String.format("UPDATE books SET title='%s', description='%s', pdfFileName='%s', coverPhotoName='%s', roleName='%s' WHERE id=%d",
                    book.getTitle(), book.getDescription(), book.getPdfFileName(), book.getCoverPhotoName(), book.getRole().getName(), book.getId());
            logger.info("Executing SQL statement: " + sql);
            con.createStatement().execute(sql);
            logger.info("Book successfully updated. ID: "+book.getId());
        }else {
            //here, the single quotes are replaced with 2 single quotes so as to escape it and not cause an error

            String sql = String.format("INSERT INTO books(title, description, pdfFileName, coverPhotoName, roleName) VALUES ('%s', '%s', '%s', '%s', '%s')",
                    book.getTitle().replace("'", "''"), book.getDescription().replace("'","''"), book.getPdfFileName(), book.getCoverPhotoName(), book.getRole().getName());
            logger.info("Executing SQL statement: " + sql);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                book.setId(id);
            }
            logger.info("Book successfully inserted with ID: "+book.getId());
        }
    }

    public Optional<Book> findById(Integer id) throws SQLException {
        Book book = null;
        String sql = String.format("SELECT * FROM books WHERE id=%d", id);
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        if (resultSet.next()){
            book = new Book();
            book.setId(resultSet.getInt(1));
            book.setTitle(resultSet.getString(2));
            book.setDescription(resultSet.getString(3));
            book.setPdfFileName(resultSet.getString(4));
            book.setCoverPhotoName(resultSet.getString(5));
            book.setRole(roleRepository.findById(resultSet.getString(6)).get());
        }
        return Optional.ofNullable(book);
    }

    public List<Book> findByTitleIgnoreCaseContaining(String pattern) throws SQLException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE '%"+pattern+"%'";
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        while(resultSet.next()){
            Book book = new Book();
            book.setId(resultSet.getInt(1));
            book.setTitle(resultSet.getString(2));
            book.setDescription(resultSet.getString(3));
            book.setPdfFileName(resultSet.getString(4));
            book.setCoverPhotoName(resultSet.getString(5));
            book.setRole(roleRepository.findById(resultSet.getString(6)).get());
            bookList.add(book);
        }
        return bookList;
    }

    public List<Book> findAll() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        String sql = "SELECT * FROM books";
        logger.info("Executing SQL statement: "+sql);
        ResultSet resultSet = con.createStatement().executeQuery(sql);
        while(resultSet.next()){
            Book book = new Book();
            book.setId(resultSet.getInt(1));
            book.setTitle(resultSet.getString(2));
            book.setDescription(resultSet.getString(3));
            book.setPdfFileName(resultSet.getString(4));
            book.setCoverPhotoName(resultSet.getString(5));
            book.setRole(roleRepository.findById(resultSet.getString(6)).get());
            bookList.add(book);
        }
        return bookList;
    }

    public void delete(Book book) throws SQLException {
        String sql = String.format("DELETE FROM books WHERE id=%d", book.getId());
        logger.info("Executing SQL statement: "+sql);
        con.createStatement().execute(sql);
    }
}
