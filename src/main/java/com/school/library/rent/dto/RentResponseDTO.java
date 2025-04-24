package com.school.library.rent.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentResponseDTO {
    private Long id;
    private Long memberId;
    private Long bookItemId;
    private String isbn;
    private String rentDate;
    private String dueDate;
    private String returnDate;
}
