package com.school.library.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(length = 1048576)
    private String coverImage;    

    @NotBlank
    private String title;

    @NotBlank
    private String author;
    private Integer publicationYear;
    private Integer stock;    
}