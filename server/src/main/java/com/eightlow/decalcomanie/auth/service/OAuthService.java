package com.eightlow.decalcomanie.auth.service;

import com.eightlow.decalcomanie.auth.entity.UserCredential;
import com.eightlow.decalcomanie.user.entity.User;

public interface OAuthService {
    void register(UserCredential userCredential, User user);

    UserCredential isMember(String email);

    void signIn(UserCredential userCredential);

    void updateRefreshToken(String refreshToken, String userId);

    void signOut(String userId);
}
