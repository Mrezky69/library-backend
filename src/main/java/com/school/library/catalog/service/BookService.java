package com.school.library.catalog.service;

import com.school.library.catalog.dto.BookRequestDTO;
import com.school.library.catalog.dto.BookResponseDTO;
import com.school.library.catalog.model.Book;
import com.school.library.catalog.repository.BookRepository;
import com.school.library.common.PagedResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Book createBook(BookRequestDTO request) {
        if (bookRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new RuntimeException("Tittle already exists");
        }
        
        return bookRepository.save(Book.builder()
                        .coverImage(request.getCoverImageBase64())
                        .title(request.getTitle())
                        .author(request.getAuthor())
                        .publicationYear(request.getPublicationYear())
                        .stock(request.getStock())
                        .build());
    }

    public PagedResponse<BookResponseDTO> getAllBooks(
            String title, String author, 
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


    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return BookResponseDTO.builder()
                .id(book.getId())
                .coverImage(book.getCoverImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .stock(book.getStock())
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
        bookRepository.delete(book);
    }
}
