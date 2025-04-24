package com.school.library.member.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
