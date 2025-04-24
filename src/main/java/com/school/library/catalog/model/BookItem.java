package com.school.library.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "book_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
