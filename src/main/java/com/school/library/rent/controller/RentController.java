package com.school.library.rent.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.school.library.rent.dto.RentResponseDTO;
import com.school.library.rent.service.RentService;

@RestController
@RequestMapping("/v1/rent")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @PostMapping("/{memberId}/{bookId}")
    public RentResponseDTO rentBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        return rentService.rentBook(memberId, bookId);
    }

    @PostMapping("/return/{rentId}")
    public RentResponseDTO returnBook(@PathVariable Long rentId) {
        return rentService.returnBook(rentId);
    }

    @GetMapping("/history")
    public List<RentResponseDTO> getRentHistory() {
        return rentService.getHistoryForCurrentUser();
    }    

    @PostMapping("/approve/{rentId}")
    public RentResponseDTO approve(@PathVariable Long rentId) {
        return rentService.approveRent(rentId);
    }
}