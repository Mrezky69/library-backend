package com.school.library.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.library.catalog.model.Book;
import com.school.library.catalog.model.BookItem;

@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Long> {
    Optional<BookItem> findByIsbn(String isbn);
    List<BookItem> findAllByBook(Book book);    
    Optional<BookItem> findFirstByBookAndIdNotIn(Book book, List<Long> rentedItemIds);
}
