package com.school.library.rent.service;

import com.school.library.auth.model.User;
import com.school.library.auth.repository.UserRepository;
import com.school.library.catalog.model.Book;
import com.school.library.catalog.repository.BookRepository;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;
import com.school.library.rent.dto.RentResponseDTO;
import com.school.library.rent.model.Rent;
import com.school.library.rent.model.RentStatus;
import com.school.library.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentService {

    private final RentRepository rentRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    @Transactional
    public RentResponseDTO rentBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

                List<Rent> activeRents = rentRepository.findAll().stream()
                .filter(r -> r.getMember().getId().equals(member.getId()))
                .filter(r -> r.getStatus() == RentStatus.WAITING || r.getStatus() == RentStatus.BORROWED)
                .toList();
    
        boolean alreadyRentedSameTitle = activeRents.stream()
                .anyMatch(r -> r.getBook().getTitle().equalsIgnoreCase(book.getTitle()));
    
        if (alreadyRentedSameTitle) {
            throw new RuntimeException("You already have an active loan for a book with the same title");
        }
    
        if (activeRents.size() >= 3) {
            throw new RuntimeException("You have reached the maximum number of active loans (3)");
        }
    
        if (book.getStock() <= 0) {
            throw new RuntimeException("No available stock for this book");
        }

        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        Rent rent = Rent.builder()
                .member(member)
                .book(book)
                .rentDate(LocalDate.now())
                .dueDate(LocalDate.now().plusWeeks(2))
                .status(RentStatus.WAITING)                
                .build();

        return toResponse(rentRepository.save(rent));
    }

    @Transactional
    public RentResponseDTO approveRent(Long rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent not found"));
        if (rent.getStatus() != RentStatus.WAITING) {
            throw new RuntimeException("Already approved or returned");
        }
        rent.setStatus(RentStatus.BORROWED);
        return toResponse(rentRepository.save(rent));
    }    

    @Transactional
    public RentResponseDTO returnBook(Long rentId) {
        Rent record = rentRepository.findById(rentId)
                .orElseThrow(() -> new RuntimeException("Rent record not found"));

        record.setReturnDate(LocalDate.now());
        record.setStatus(RentStatus.RETURNED);        

        Book book = record.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        return toResponse(rentRepository.save(record));
    }


    public List<RentResponseDTO> getHistoryForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        List<Rent> allRents = rentRepository.findAll();

        return allRents.stream()
                .filter(rent -> isAdmin || rent.getMember().getUser().getId().equals(user.getId()))
                .sorted((r1, r2) -> {
                    if (r1.getStatus() == RentStatus.BORROWED && r2.getStatus() != RentStatus.BORROWED) return -1;
                    if (r1.getStatus() != RentStatus.BORROWED && r2.getStatus() == RentStatus.BORROWED) return 1;
                    return r2.getRentDate().compareTo(r1.getRentDate());
                })
                .map(this::toResponse)
                .toList();
    }
    

    private RentResponseDTO toResponse(Rent record) {
        return RentResponseDTO.builder()
                .id(record.getId())
                .bookTitle(record.getBook().getTitle())
                .memberId(record.getMember().getId())
                .bookId(record.getBook().getId())
                .rentDate(record.getRentDate().toString())
                .dueDate(record.getDueDate().toString())
                .returnDate(record.getReturnDate() != null ? record.getReturnDate().toString() : null)
                .status(record.getStatus().name())
                .build();
    }
}
