package com.school.library.rent.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentResponseDTO {
    private Long id;
    private String bookTitle;
    private Long memberId;
    private Long bookId;
    private String rentDate;
    private String dueDate;
    private String returnDate;
    private String status;
}
