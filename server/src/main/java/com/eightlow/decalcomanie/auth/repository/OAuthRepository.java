package com.eightlow.decalcomanie.auth.repository;

import com.eightlow.decalcomanie.auth.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthRepository extends JpaRepository<UserCredential, String> {
    UserCredential findByEmail(String email);

    UserCredential findByUserId(String userId);
}
