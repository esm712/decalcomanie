package com.eightlow.decalcomanie.auth.service;

import com.eightlow.decalcomanie.auth.entity.UserCredential;
import com.eightlow.decalcomanie.auth.repository.OAuthRepository;
import com.eightlow.decalcomanie.common.exception.CustomErrorCode;
import com.eightlow.decalcomanie.common.exception.CustomException;
import com.eightlow.decalcomanie.user.entity.User;
import com.eightlow.decalcomanie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthRepository oAuthRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void register(UserCredential userCredential, User user) {
        oAuthRepository.save(userCredential);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserCredential isMember(String email) {
        return oAuthRepository.findByEmail(email);
    }

    @Override
    public void signIn(UserCredential userCredential) {
        oAuthRepository.save(userCredential);
    }

    @Override
    @Transactional
    public void updateRefreshToken(String refreshToken, String userId) {
        UserCredential userCredential = oAuthRepository.findByUserId(userId);

        if(userCredential == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        UserCredential reissueUser = userCredential.toBuilder()
                .refreshToken(refreshToken)
                .build();

        oAuthRepository.save(reissueUser);
    }

    @Override
    @Transactional
    public void signOut(String userId) {
        UserCredential userCredential = oAuthRepository.findByUserId(userId);

        if(userCredential == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        UserCredential signOutUser = userCredential.toBuilder()
                .refreshToken("")
                .build();

        oAuthRepository.save(signOutUser);
    }
}
