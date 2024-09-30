package com.translator.webchat.service;

import com.translator.webchat.dto.request.SendOtpRequestDTO;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface OtpService {

    String generateOtp(SendOtpRequestDTO sendOtpRequestDTO) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
