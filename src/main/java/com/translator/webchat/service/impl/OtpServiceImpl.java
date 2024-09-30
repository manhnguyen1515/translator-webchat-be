package com.translator.webchat.service.impl;

import com.translator.webchat.dto.request.SendOtpRequestDTO;
import com.translator.webchat.service.OtpService;
import com.translator.webchat.util.SendEmailOtp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final SendEmailOtp sendEmailOtp;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateOtp(SendOtpRequestDTO sendOtpRequestDTO) {
        int result = sendEmailOtp.SendOtpEmail(sendOtpRequestDTO.getEmail());
        if (result == 0) {
            return "success";
        } else {
            return "failed";
        }
    }
}
