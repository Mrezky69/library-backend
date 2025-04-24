package com.school.library.catalog.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailResponseDTO {
    private Long id;
    private String coverImage;    
    private String title;
    private String author;
    private String genre;
    private Integer publicationYear;
    private Integer stock;
    private List<BookItemResponseDTO> items;
}
