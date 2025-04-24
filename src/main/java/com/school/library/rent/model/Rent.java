package com.school.library.rent.model;

import com.school.library.catalog.model.BookItem;
import com.school.library.member.model.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "rent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_item_id")
    private BookItem bookItem;


    private LocalDate rentDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
}