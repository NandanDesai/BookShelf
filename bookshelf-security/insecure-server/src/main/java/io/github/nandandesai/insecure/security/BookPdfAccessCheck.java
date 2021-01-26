package io.github.nandandesai.insecure.security;

import io.github.nandandesai.insecure.exceptions.InternalServerException;
import io.github.nandandesai.insecure.exceptions.ResourceNotFoundException;
import io.github.nandandesai.insecure.models.Book;
import io.github.nandandesai.insecure.models.User;
import io.github.nandandesai.insecure.repositories.BookRepository;
import io.github.nandandesai.insecure.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class BookPdfAccessCheck {
    private Logger logger = LoggerFactory.getLogger(BookPdfAccessCheck.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean verify(Integer bookId, Integer userId) throws ResourceNotFoundException, InternalServerException {
        Optional<Book> bookOptional = null;
        Optional<User> userOptional = null;
        try {
            bookOptional = bookRepository.findById(bookId);
            if(!bookOptional.isPresent()){
                throw new ResourceNotFoundException("book with id: "+bookId+" not found");
            }
            userOptional = userRepository.findById(userId);
            if(!userOptional.isPresent()){
                throw new ResourceNotFoundException("user with id: "+userId+" not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        Book book = bookOptional.get();
        User user = userOptional.get();
        boolean permissionGranted = (user.getRole().getValue()>=book.getRole().getValue());
        logger.info("Book ID: "+bookId+"; User ID: "+userId+"; Permission Granted: "+permissionGranted);
        return permissionGranted;
    }
}
