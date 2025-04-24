package com.school.library.rent.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentRequestDTO {
    private Long memberId;
    private Long bookId;
}