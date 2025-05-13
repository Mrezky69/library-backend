package com.school.library.catalog;

import com.school.library.catalog.dto.BookRequestDTO;
import com.school.library.catalog.repository.BookRepository;
import com.school.library.catalog.service.BookService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // @Test
    // void createBook_shouldThrowIfIsbnExists() {
    //     BookRequestDTO request = BookRequestDTO.builder().isbn("123456").build();
    //     when(bookItemRepository.findByIsbn("123456")).thenReturn(Optional.of(new BookItem()));
    //     assertThrows(RuntimeException.class, () -> bookService.createBook(request));
    // }

}
