package com.school.library.catalog.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemResponseDTO {
    private Long id;
    private String isbn;
}
