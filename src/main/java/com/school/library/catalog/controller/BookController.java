package com.school.library.catalog.controller;

import com.school.library.catalog.dto.BookRequestDTO;
import com.school.library.catalog.dto.BookResponseDTO;
import com.school.library.catalog.dto.BookDetailResponseDTO;
import com.school.library.catalog.service.BookService;
import com.school.library.common.PagedResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public void createBook(@RequestBody BookRequestDTO request) {
        bookService.createBook(request);
    }

    @GetMapping("/{id}")
    public BookDetailResponseDTO getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping
    public PagedResponse<BookResponseDTO> getAllBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String genre,
        @RequestParam(required = false) Integer publicationYear,
        @PageableDefault(size = 10) Pageable pageable) {
        
        return bookService.getAllBooks(title, author, genre, publicationYear, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDTO updateBook(@PathVariable Long id, 
                                    @RequestBody BookRequestDTO request){
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
