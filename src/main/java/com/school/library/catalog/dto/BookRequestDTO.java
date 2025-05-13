package com.school.library.catalog.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {
    private String coverImageBase64;    
    private String title;
    private String author;
    private Integer publicationYear;
    private Integer stock;
}
