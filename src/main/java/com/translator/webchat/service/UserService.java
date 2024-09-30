package com.translator.webchat.service;

import com.translator.webchat.dto.request.*;
import com.translator.webchat.dto.response.ResponseGetUser;
import com.translator.webchat.dto.response.UserAuthResponse;
import com.translator.webchat.dto.response.UserSignInResponse;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface UserService {

    /**
     * Service for handle user sign up
     * @param requestDTO
     * @return  String
     */
    String signUp(UserRequestDTO requestDTO);

    /**
     * Service for handle user login
     * @param userRequestSignInDTO
     * @return  UserRequestDTO
     */
    UserAuthResponse signIn(UserRequestSignInDTO userRequestSignInDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Service for handle user login
     * @param changePasswordRequestDTO
     */
    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;


    /**
     * Service for handle user login
     * @param userRequesUserNameDTO
     * @return  User
     */
    ResponseGetUser getUser(String userRequesUserNameDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Service for handle user login
     * @param createSessionDTO
     * @return  String
     */
    String createSession(CreateSessionDTO createSessionDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;


    /**
     * Service for get all users
     * @return  User
     */
    List<UserSignInResponse> getAllUsers();
}
