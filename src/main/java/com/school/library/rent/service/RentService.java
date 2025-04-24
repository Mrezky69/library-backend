package com.school.library.rent.service;

import com.school.library.catalog.model.Book;
import com.school.library.catalog.model.BookItem;
import com.school.library.catalog.repository.BookItemRepository;
import com.school.library.catalog.repository.BookRepository;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;
import com.school.library.rent.dto.RentResponseDTO;
import com.school.library.rent.model.Rent;
import com.school.library.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RentResponseDTO rentBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookItem availableItem = bookItemRepository.findFirstByBookAndIdNotIn(book, rentRepository.findAll()
                .stream().filter(r -> r.getReturnDate() == null)
                .map(r -> r.getBookItem().getId()).toList())
                .orElseThrow(() -> new RuntimeException("No available copies for this book"));

        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        Rent rent = Rent.builder()
                .member(member)
                .bookItem(availableItem)
                .rentDate(LocalDate.now())
                .dueDate(LocalDate.now().plusWeeks(2))
                .build();

        return toResponse(rentRepository.save(rent));
    }

    @Transactional
    public RentResponseDTO returnBook(Long rentId) {
        Rent record = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent record not found"));

        record.setReturnDate(LocalDate.now());

        Book book = record.getBookItem().getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        return toResponse(rentRepository.save(record));
    }

    private RentResponseDTO toResponse(Rent record) {
        return RentResponseDTO.builder()
                .id(record.getId())
                .memberId(record.getMember().getId())
                .bookItemId(record.getBookItem().getId())
                .isbn(record.getBookItem().getIsbn())
                .rentDate(record.getRentDate().toString())
                .dueDate(record.getDueDate().toString())
                .returnDate(record.getReturnDate() != null ? record.getReturnDate().toString() : null)
                .build();
    }
}
