package io.github.nandandesai.base.repositories;

import io.github.nandandesai.base.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleIgnoreCaseContaining(String pattern);
}
