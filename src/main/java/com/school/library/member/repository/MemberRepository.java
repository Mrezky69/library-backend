package com.school.library.member.repository;

import com.school.library.auth.model.User;
import com.school.library.member.model.Member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUser(User user);
}
