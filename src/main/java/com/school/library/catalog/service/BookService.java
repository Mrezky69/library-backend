package com.school.library.catalog.service;

import com.school.library.catalog.dto.BookDetailResponseDTO;
import com.school.library.catalog.dto.BookItemResponseDTO;
import com.school.library.catalog.dto.BookRequestDTO;
import com.school.library.catalog.dto.BookResponseDTO;
import com.school.library.catalog.model.Book;
import com.school.library.catalog.model.BookItem;
import com.school.library.catalog.repository.BookItemRepository;
import com.school.library.catalog.repository.BookRepository;
import com.school.library.common.PagedResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private final BookItemRepository bookItemRepository;

    @Transactional
    public void createBook(BookRequestDTO request) {
        if (bookItemRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new RuntimeException("ISBN already exists");
        }
        
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseGet(() -> bookRepository.save(Book.builder()
                        .coverImage(request.getCoverImageBase64())
                        .title(request.getTitle())
                        .author(request.getAuthor())
                        .publicationYear(request.getPublicationYear())
                        .stock(request.getStock())
                        .build())
                );

        BookItem bookItem = BookItem.builder()
                .book(book)
                .isbn(request.getIsbn())
                .build();

        bookItemRepository.save(bookItem);
    }

    public PagedResponse<BookResponseDTO> getAllBooks(
            String title, String author, String genre, 
            Integer publicationYear, Pageable pageable) {
        
        Specification<Book> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (author != null && !author.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
        }

        if (genre != null && !genre.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.equal(cb.lower(root.get("genre")), genre.toLowerCase()));
        }

        if (publicationYear != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("publicationYear"), publicationYear));
        }

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        List<BookResponseDTO> content = bookPage.getContent().stream()
            .map(book -> BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .stock(book.getStock())
                .coverImage(book.getCoverImage())
                .build()
            ).toList();

        return new PagedResponse<>(
            content,
            bookPage.getNumber(),
            bookPage.getSize(),
            bookPage.getTotalElements(),
            bookPage.getTotalPages(),
            bookPage.isLast()
        );
    }


    public BookDetailResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookItemResponseDTO> items = bookItemRepository.findAllByBook(book).stream()
                .map(item -> BookItemResponseDTO.builder()
                        .id(item.getId())
                        .isbn(item.getIsbn())
                        .build())
                .collect(Collectors.toList());

        return BookDetailResponseDTO.builder()
                .id(book.getId())
                .coverImage(book.getCoverImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .stock(book.getStock())
                .items(items)
                .build();
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if(request.getCoverImageBase64() != null && !request.getCoverImageBase64().isEmpty()){
            book.setCoverImage(request.getCoverImageBase64());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setStock(request.getStock());

        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .stock(book.getStock())
                .build();
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        bookItemRepository.deleteAll(bookItemRepository.findAllByBook(book));
        bookRepository.delete(book);
    }
}
