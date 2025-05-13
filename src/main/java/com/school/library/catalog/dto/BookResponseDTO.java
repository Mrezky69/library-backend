package com.school.library.catalog.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    private Long id;
    private String coverImage;    
    private String title;
    private String author;
    private Integer publicationYear;
    private Integer stock;
}