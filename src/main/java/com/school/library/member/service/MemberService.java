package com.school.library.member.service;

import com.school.library.auth.model.Role;
import com.school.library.auth.model.User;
import com.school.library.auth.repository.UserRepository;
import com.school.library.member.dto.MemberRequestDTO;
import com.school.library.member.dto.MemberResponseDTO;
import com.school.library.member.model.Member;
import com.school.library.member.repository.MemberRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MemberRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDTO create(MemberRequestDTO req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .password(passwordEncoder.encode("password123"))
                .role(Role.MEMBER)
                .build();

        Member member = Member.builder()
                .phone(req.getPhone())
                .address(req.getAddress())
                .user(user)
                .build();
        userRepository.save(user);                
        Member saved = repository.save(member);
        return toResponse(saved);
    }

    public List<MemberResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MemberResponseDTO getById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Transactional
    public MemberResponseDTO update(Long id, MemberRequestDTO req) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setPhone(req.getPhone());
        member.setAddress(req.getAddress());
        User user = member.getUser();
        if (!user.getEmail().equals(req.getEmail())) {
            if (userRepository.findByEmail(req.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
            user.setEmail(req.getEmail());
            user.setName(req.getName());
            userRepository.save(user);
        }        
        member.setUser(user);
        return toResponse(repository.save(member));
    }

    @Transactional
    public void delete(Long id) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        User user = member.getUser();
        repository.delete(member);
        userRepository.delete(user);
    }
    
    private MemberResponseDTO toResponse(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .name(member.getUser().getName())
                .email(member.getUser().getEmail())
                .phone(member.getPhone())
                .address(member.getAddress()) 
                .build();
    }
}
